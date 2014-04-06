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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Box;

import org.max.imagediff.ImageHolder;
import org.max.imagediff.conf.ImgDescriptor;

public class MultiPanelController implements Observer {

	private Container mainPanel;
	List<PanelLocalInfo> panels = new ArrayList<>();
	Dimension maxSize = new Dimension();

	public void setMainPanel(Container mainPanel) {
		this.mainPanel = mainPanel;
	}

	public ImageHolder addPanel(ImgDescriptor imgDescriptor) {
		PanelLocalInfo panelInfo = new PanelLocalInfo();
		panels.add(panelInfo);

		ImagePanelController contr = new ImagePanelController();
		contr.createPanel(imgDescriptor);

		panelInfo.setController(contr);
		panelInfo.setDecriptor(imgDescriptor);

		Dimension size = imgDescriptor.getSize();

		if (size.width > maxSize.width) {
			maxSize.width = size.width;
		}
		if (size.height > maxSize.height) {
			maxSize.height = size.height;
		}

		// Add gap component between panels if there is already something there.
		if (mainPanel.getComponentCount() > 0) {
			Component gap = Box.createRigidArea(new Dimension(2, 0));
			mainPanel.add(gap);
			panelInfo.setGap(gap);
		}
		mainPanel.add(contr.getContainer());

		for (PanelLocalInfo pInf : panels) {
			ImagePanelController tempContr = pInf.getController();
			if (tempContr != contr) {
				tempContr.addObserver(this);
			}
			tempContr.setSize(maxSize);
		}

		mainPanel.revalidate();

		return contr;
	}

	public void setPanelEnabled(int index, boolean enabled) {
		panels.get(index).setVisible(enabled);
		mainPanel.revalidate();
	}

	/**
	 * @param zoom
	 *            Look {@link ImagePanelController#zoom(int)}.
	 */
	private <ARG> void zoomAll(ARG zoom) {
		// First check whether all panels can be zoomed.
		boolean canBeZoomed = true;
		Iterator<PanelLocalInfo> iter = panels.iterator();
		while (canBeZoomed && iter.hasNext()) {
			PanelLocalInfo panel = iter.next();
			canBeZoomed = canBeZoomed && panel.getController().canZoom(zoom);
		}
		// Now zoom all panels if all they can be zoomed.
		if (canBeZoomed) {
			for (PanelLocalInfo panel : panels) {
				panel.getController().zoom(zoom);
			}
		}
	}

	public void showGrid(boolean enable) {
		for (PanelLocalInfo panel : panels) {
			panel.getController().showGrid(enable);
		}
	}

	public void zoomFit() {
		// Find smallest window.
		int width = Integer.MAX_VALUE;
		int height = Integer.MAX_VALUE;
		Dimension size;
		for (PanelLocalInfo panel : panels) {
			if (panel.isVisible()) {
				size = panel.getController().getSize();
				width = Math.min(width, size.width - 2);
				height = Math.min(height, size.height - 2);
			}
		}
		double scale = Double.MAX_VALUE;
		double tempScale;
		// Find smallest needed scale.
		for (PanelLocalInfo panel : panels) {
			if (panel.isVisible()) {
				size = panel.getDescriptor().getSize();
				tempScale = width / (double) size.width;
				scale = Math.min(scale, tempScale);
				tempScale = height / (double) size.height;
				scale = Math.min(scale, tempScale);
			}
		}
		zoomAll(scale);
	}

	public void zoomOut() {
		zoomAll(-1);
	}

	public void zoomIn() {
		zoomAll(1);
	}

	public void zoom100() {
		zoomAll(1d);
	}

	public void removeAllPanels() {
		for (PanelLocalInfo panel : panels) {
			panel.getController().deleteObservers();
		}
		panels.clear();
		mainPanel.removeAll();
		mainPanel.repaint();
	}

	public void showImageNames(boolean show) {
		for (PanelLocalInfo panel : panels) {
			panel.getController().showInfo(show);
		}
	}

	@Override
	public void update(Observable observable, Object arg) {
		if (arg instanceof Point) {
			// If event is of type Point then drag.
			Point point = (Point) arg;
			for (PanelLocalInfo panel : panels) {
				ImagePanelController contr = panel.getController();
				if (contr != observable) {
					contr.setViewPort(point, point.x, point.y);
				}
			}
		} else {
			// Or else event is scrolling.
			int direction = (int) arg;
			zoomAll(direction);
		}
	}
}

class PanelLocalInfo {
	private ImagePanelController controller;
	private ImgDescriptor descriptor;
	private Component gap;
	private boolean visible = true;

	public void setController(ImagePanelController controller) {
		this.controller = controller;
	}

	public ImgDescriptor getDescriptor() {
		return descriptor;
	}

	public ImagePanelController getController() {
		return controller;
	}

	public void setDecriptor(ImgDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	public void setGap(Component gap) {
		this.gap = gap;
	}

	public void setVisible(boolean visible) {
		controller.setVisible(visible);
		if (gap != null) {
			gap.setVisible(visible);
		}

		this.visible = visible;
	}

	public boolean isVisible() {
		return visible;
	}
}
