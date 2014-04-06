package org.max.imagediff.controllers;

/**
 * Copyright 2014 Max Chornyi
 * 
 * This file is part of ImageDiff application.
 * 
 * ImageDiff is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ImageDiff is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ImageDiff.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.TexturePaint;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.Observable;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import org.max.imagediff.ImageHolder;
import org.max.imagediff.conf.ImgDescriptor;
import org.max.imagediff.gui.ImagePanel;
import org.max.imagediff.util.CursorBuilder;
import org.max.imagediff.util.TextureBuilder;

public class ImagePanelController extends Observable implements ImageHolder {

	private ImagePanel panel;
	private JScrollPane scrollPane;
	private Point mouseStartLocation;
	private Point viewPortStartLocation;
	private Point viewPortNewLocation = new Point();
	private JViewport viewPort;
	protected boolean motionInProgress;
	private BufferedImage image;

	public void createPanel(ImgDescriptor imgDescriptor) {
		panel = new ImagePanel();
		scrollPane = new JScrollPane(panel);

		final TexturePaint tp = TextureBuilder.createTransparentTexture();

		panel.setGridTexture(TextureBuilder.createGridTexture());

		viewPort = new JViewport() {
			private static final long serialVersionUID = 7825378073099760338L;

			protected void paintComponent(java.awt.Graphics g) {

				Graphics2D g2 = (Graphics2D) g;

				g2.setPaint(tp);

				Dimension size = getSize();

				g2.fillRect(0, 0, size.width, size.height);
			};
		};
		scrollPane.setViewport(viewPort);
		viewPort.setView(panel);

		panel.setOpaque(false);
		setImage(imgDescriptor.getImage());

		viewPort.setCursor(CursorBuilder.getHand());
		panel.setScale(1);
		panel.setName(imgDescriptor.getName());

		scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

		viewPort.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent evt) {
				super.mousePressed(evt);
				if (evt.getButton() == MouseEvent.BUTTON1) {
					mouseStartLocation = evt.getPoint();
					viewPortStartLocation = viewPort.getViewPosition();
					viewPort.setCursor(CursorBuilder.getHoldingHand());
					motionInProgress = true;
				}
			}

			@Override
			public void mouseReleased(MouseEvent evt) {
				super.mouseReleased(evt);
				if (evt.getButton() == MouseEvent.BUTTON1) {
					motionInProgress = false;
					viewPort.setCursor(CursorBuilder.getHand());
				}
			}
		});

		viewPort.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent evt) {
				super.mouseDragged(evt);
				if (motionInProgress) {
					ImagePanelController.this.mouseDragged(evt);
				}
			}
		});

		viewPort.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent evt) {
				int direction = evt.getWheelRotation();
				setChanged();
				notifyObservers(direction);
			}
		});

		scrollPane.getHorizontalScrollBar().addAdjustmentListener(
				new AdjustmentListener() {

					@Override
					public void adjustmentValueChanged(AdjustmentEvent e) {
						scrollBarAdjusted();
					}
				});
		scrollPane.getVerticalScrollBar().addAdjustmentListener(
				new AdjustmentListener() {

					@Override
					public void adjustmentValueChanged(AdjustmentEvent e) {
						scrollBarAdjusted();
					}
				});
	}

	public void setImage(BufferedImage img) {
		this.image = img;
		panel.setOriginalSize(new Dimension(image.getWidth(), image.getHeight()));
		panel.setImage(image);
	}

	private void scrollBarAdjusted() {
		setChanged();
		notifyObservers(viewPort.getViewPosition());
	}

	public JComponent getContainer() {
		return scrollPane;
	}

	private void mouseDragged(MouseEvent evt) {
		Point point = evt.getPoint();
		int x = mouseStartLocation.x + viewPortStartLocation.x - point.x;
		int y = mouseStartLocation.y - point.y + viewPortStartLocation.y;

		setViewPort(point, x, y);

		setChanged();
		notifyObservers(viewPortNewLocation);
	}

	public void setViewPort(Point point, int x, int y) {
		int tempX = 0;
		int tempY = 0;

		if (mouseStartLocation == null) {
			mouseStartLocation = new Point();
		}

		if (viewPortStartLocation == null) {
			viewPortStartLocation = new Point();
		}

		if (x < 0) {
			x = 0;
			mouseStartLocation.x = point.x;
			viewPortStartLocation.x = 0;
		} else if (x > (tempX = panel.getPreferredSize().width
				- viewPort.getWidth())
				&& tempX > 0) {
			x = tempX;
			mouseStartLocation.x = point.x;
			viewPortStartLocation.x = x;
		}

		if (y < 0) {
			y = 0;
			mouseStartLocation.y = point.y;
			viewPortStartLocation.y = 0;
		} else if (y > (tempY = panel.getPreferredSize().height
				- viewPort.getHeight())
				&& tempY > 0) {
			y = tempY;
			mouseStartLocation.y = point.y;
			viewPortStartLocation.y = y;
		}

		viewPortNewLocation.setLocation(x, y);
		viewPort.setViewPosition(viewPortNewLocation);
	}

	/**
	 * <b>Preconditions:</b> check using {@link #canZoom(Object)} first.
	 * 
	 * @param zoom
	 *            expects {@link Boolean} or {@link Integer}. For integer
	 *            argument positive value means zoom out, negative - zoom in.
	 */
	public <ARG> void zoom(ARG zoom) {
		double scale = panel.getScale();

		if (zoom instanceof Integer) {
			scale += (Integer) zoom * 0.1;
		} else {
			scale = (Double) zoom;
		}
		panel.setScale(scale);
		panel.rescale();
		panel.revalidate();
	}

	/**
	 * @param zoom
	 *            expects {@link Boolean} or {@link Integer}. For integer values
	 *            see {@link #zoom(Object)}.
	 * @return
	 */
	public <ARG> boolean canZoom(ARG zoom) {
		Dimension parentSize = scrollPane.getSize();
		Dimension currentSize = panel.getPreferredSize();

		double scale = panel.getScale();

		if (zoom instanceof Integer) {
			scale += (Integer) zoom * 0.1;
		} else {
			scale = (Double) zoom;
		}

		if (scale > 0.05
				&& scale < 10
				&& (scale > panel.getScale() || (parentSize.width <= currentSize.width || parentSize.height <= currentSize.height))) {
			return true;
		}
		return false;
	}

	public Dimension getPanelSize() {
		return panel.getPreferredSize();
	}

	public void setSize(Dimension size) {
		panel.setOriginalSize(size);
		panel.rescale();
	}

	public Dimension getImgSize() {
		return new Dimension(image.getWidth(), image.getHeight());
	}

	public void showGrid(boolean enable) {
		panel.showGrid(enable);
		panel.repaint();
	}

	public void showInfo(boolean show) {
		panel.showInfo(show);
		panel.repaint();
	}

	public void setVisible(boolean enabled) {
		scrollPane.setVisible(enabled);
	}

	public Dimension getSize() {
		return scrollPane.getSize();
	}

	@Override
	public void updateImage(BufferedImage img) {
		setImage(img);
	}
}
