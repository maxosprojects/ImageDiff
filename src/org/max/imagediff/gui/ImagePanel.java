package org.max.imagediff.gui;

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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ImagePanel extends JPanel {

	private static final long serialVersionUID = 7929201344523656910L;
	private BufferedImage image;
	private Dimension originalSize;
	private double scale;
	private TexturePaint gridTexture;
	private boolean gridEnabled;
	private boolean infoEnabled;
	private RenderingHints hints;
	private Color infoBackColor;

	public ImagePanel() {
		super();
		hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		hints.add(new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON));
		infoBackColor = new Color(255, 221, 191, 200);
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;

		// Draw image.
		AffineTransform at = new AffineTransform();
		g2.setRenderingHints(hints);
		at.scale(scale, scale);
		g2.drawImage(image, at, null);

		AffineTransform tempAt = g2.getTransform();

		// Draw grid over.
		if (gridEnabled) {
			g2.setPaint(gridTexture);

			at.setToTranslation(tempAt.getTranslateX(), tempAt.getTranslateY());
			at.scale(scale, scale);
			g2.setTransform(at);

			g2.fillRect(0, 0, image.getWidth(), image.getHeight());
			g2.setTransform(tempAt);
		}

		// Display info if enabled.
		if (infoEnabled) {
			at = new AffineTransform();
			at.translate(0, 0);
			g2.setTransform(at);

			g2.setFont(g2.getFont().deriveFont(Font.BOLD));
			FontMetrics fm = g2.getFontMetrics();
			Rectangle2D rect = fm.getStringBounds(getName(), g2);
			g2.setColor(infoBackColor);
			g2.fillRect(5, 5, (int) rect.getWidth() + 8,
					(int) rect.getHeight() + 8);
			g2.setColor(Color.BLACK);
			g2.drawString(getName(), 9, 9 + fm.getAscent());
		}
	}

	public void setOriginalSize(Dimension size) {
		this.originalSize = size;
	}

	public void rescale() {
		setPreferredSize(new Dimension((int) (originalSize.width * scale),
				(int) (originalSize.height * scale)));
		repaint();
	}

	public double getScale() {
		return scale;
	}

	public void setScale(double newScale) {
		this.scale = newScale;
	}

	public void setGridTexture(TexturePaint gridTexture) {
		this.gridTexture = gridTexture;
	}

	public void showGrid(boolean enable) {
		gridEnabled = enable;
	}

	public void showInfo(boolean enable) {
		infoEnabled = enable;
	}
}
