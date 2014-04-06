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

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.List;

public class ImageDiffBuilder {

	public static BufferedImage getGrayDiff(List<Raster> diffList) {

		// Find smallest size.
		int width = Integer.MAX_VALUE;
		int height = Integer.MAX_VALUE;
		for (Raster img : diffList) {
			if (img.getWidth() < width) {
				width = img.getWidth();
			}
			if (img.getHeight() < height) {
				height = img.getHeight();
			}
		}

		BufferedImage result = new BufferedImage(width, height,
				BufferedImage.TYPE_BYTE_GRAY);

		WritableRaster raster = result.getRaster();

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int[] avg = getGrayRGBdiff(x, y, diffList);
				raster.setPixel(x, y, avg);
			}
		}

		return result;
	}

	private static int[] getGrayRGBdiff(int x, int y, List<Raster> diffList) {
		int minR = Integer.MAX_VALUE;
		int minG = Integer.MAX_VALUE;
		int minB = Integer.MAX_VALUE;
		int maxR = 0;
		int maxG = 0;
		int maxB = 0;

		int[] tempColor = new int[4];

		// Find minimum and maximum for every color among all images.
		for (Raster raster : diffList) {
			int[] color = raster.getPixel(x, y, tempColor);
			minR = Math.min(minR, color[0]);
			minG = Math.min(minG, color[1]);
			minB = Math.min(minB, color[2]);

			maxR = Math.max(maxR, color[0]);
			maxG = Math.max(maxG, color[1]);
			maxB = Math.max(maxB, color[2]);
		}

		// Find diff.
		int diff = 0;
		diff = Math.max(diff, maxR - minR);
		diff = Math.max(diff, maxG - minG);
		diff = Math.max(diff, maxB - minB);

		int[] result = { diff, diff, diff };
		return result;
	}

}
