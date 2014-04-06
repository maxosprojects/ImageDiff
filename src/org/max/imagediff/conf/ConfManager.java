package org.max.imagediff.conf;

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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.prefs.InvalidPreferencesFormatException;

public class ConfManager {

	private static Properties props;

	/**
	 * Make sure loadProperties() is called first.
	 * 
	 * @return
	 * @throws InvalidPreferencesFormatException 
	 */
	public static List<ImgDescriptor> getImages() throws InvalidPreferencesFormatException {
		
		String[] list = props.getProperty(Constants.PROP_ORDERED_LIST, "").trim().split(" *, *");
		
		List<ImgDescriptor> result = new ArrayList<>();
		
		for (String elem : list) {
			String name = props.getProperty(Constants.PROP_IMG + "." + elem + ".name");
			String filename = props.getProperty(Constants.PROP_IMG + "." + elem + ".filename");
			if (name == null) {
				throw new InvalidPreferencesFormatException("Name for \"" + elem + "\" was not found");
			}
			if (filename == null) {
				throw new InvalidPreferencesFormatException("Filename for \"" + elem + "\" was not found");
			}
			result.add(new ImgDescriptor(name, filename));
		}

		return result;
	}

	public static void loadProperties() throws IOException {
		props = new Properties();

		InputStream input = null;

		input = new FileInputStream("config.properties");
		props.load(input);
	}
}
