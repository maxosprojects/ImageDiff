package org.max.imagediff.gui.mock;

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
import java.awt.HeadlessException;
import java.io.File;

import javax.swing.JFileChooser;

public class JFileChooserMock extends JFileChooser {

	private static final long serialVersionUID = -4261652942265008435L;
	
	public JFileChooserMock(String string) {
	}

	@Override
	public int showDialog(Component arg0, String arg1) throws HeadlessException {
		return APPROVE_OPTION;
	}
	
	@Override
	public File getSelectedFile() {
		return new File("D:/eclipseWorkspace/orion/Automation/ImageDiff/directory");
	}
}
