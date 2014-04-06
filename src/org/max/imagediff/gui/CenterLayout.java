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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

import javax.swing.JScrollPane;

/**
 * This layout respects child element's (assumes only one exists) preferred size
 * as a maximum size. If parent element becomes smaller than the child element
 * then child element will be resized to fit into the parent element. Was
 * designed to contain just one {@link JScrollPane} to make it behave as a
 * lightbox.
 * 
 * @author maksymc
 * 
 */
public class CenterLayout implements LayoutManager {

	/* Required by LayoutManager. */
	public void addLayoutComponent(String name, Component comp) {
	}

	/* Required by LayoutManager. */
	public void removeLayoutComponent(Component comp) {
	}

	/* Required by LayoutManager. */
	public Dimension preferredLayoutSize(Container parent) {
		return parent.getPreferredSize();
	}

	/* Required by LayoutManager. */
	public Dimension minimumLayoutSize(Container parent) {
		Dimension dim = new Dimension(0, 0);

		// Always add the container's insets!
		Insets insets = parent.getInsets();
		dim.width = insets.left + insets.right;
		dim.height = insets.top + insets.bottom;

		return dim;
	}

	/* Required by LayoutManager. */
	/*
	 * This is called when the panel is first displayed, and every time its size
	 * changes. Note: You CAN'T assume preferredLayoutSize or minimumLayoutSize
	 * will be called -- in the case of applets, at least, they probably won't
	 * be.
	 */
	public void layoutContainer(Container parent) {

		if (parent.getComponentCount() > 0) {
			Component comp = parent.getComponents()[0];
			Dimension compSize = comp.getPreferredSize();
			Dimension parentSize = parent.getSize();
			
			int width = Math.min(parentSize.width, compSize.width);
			int height = Math.min(parentSize.height, compSize.height);
			
			int x = (parentSize.width - width) / 2;
			int y = (parentSize.height - height) / 2;
			
			comp.setBounds(x, y, width, height);
		}
	}
}
