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

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class CursorBuilder {

	static Cursor hand;
	static Cursor holdingHand;

	/**
	 * This is a utility class with singleton cursor instances created on class
	 * load.
	 */
	private CursorBuilder() {
	}

	static {
		hand = getCursor("/cursors/hand3.png");
		holdingHand = getCursor("/cursors/holdingHand3.png");
	}

	private static Cursor getCursor(String fileName) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		URL imageUrl = CursorBuilder.class.getResource(fileName);
		BufferedImage image = null;
		try {
			image = ImageIO.read(imageUrl);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Dimension size = toolkit.getBestCursorSize(0, 0);
		BufferedImage fullSizeImage = new BufferedImage(size.width,
				size.height, BufferedImage.TYPE_INT_ARGB);
		fullSizeImage.getGraphics().drawImage(image, 0, 0, null);

		return toolkit.createCustomCursor(fullSizeImage, new Point(0, 0),
				"hand3");
	}

	public static Cursor getHand() {
		return hand;
	}

	public static Cursor getHoldingHand() {
		return holdingHand;
	}
}
