package org.max.imagediff.util;

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
import java.awt.Graphics;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class TextureBuilder {

	private static TexturePaint transparentTexture;
	private static TexturePaint gridTexture;

	public static TexturePaint createTransparentTexture() {

		if (transparentTexture == null) {
			BufferedImage bg = new BufferedImage(16, 16,
					BufferedImage.TYPE_INT_RGB);

			Graphics bgG = bg.getGraphics();

			bgG.setColor(Color.WHITE);
			bgG.fillRect(0, 0, 16, 16);

			bgG.setColor(Color.decode("#BFBFBF"));
			bgG.fillRect(0, 0, 8, 8);
			bgG.fillRect(8, 8, 8, 8);

			transparentTexture = new TexturePaint(bg, new Rectangle2D.Float(0,
					0, 16, 16));
		}

		return transparentTexture;
	}

	public static TexturePaint createGridTexture() {
		if (gridTexture == null) {
			BufferedImage bg = new BufferedImage(20, 20,
					BufferedImage.TYPE_INT_ARGB);

			Graphics bgG = bg.getGraphics();

			bgG.setColor(new Color(190, 190, 190, 100));

			// Draw lines every 2 pixels.
			for (int i = 4; i < 20; i += 5) {
				// Vertical lines.
				bgG.drawLine(i, 0, i, 18);
				// Horizontal lines.
				bgG.drawLine(0, i, 18, i);
			}

			// Draw 20-pixels lines.
			bgG.setColor(new Color(160, 160, 160, 100));
			bgG.drawLine(19, 0, 19, 18);
			bgG.drawLine(0, 19, 19, 19);

			gridTexture = new TexturePaint(bg, new Rectangle2D.Float(0, 0, 20,
					20));
		}

		return gridTexture;
	}
}
