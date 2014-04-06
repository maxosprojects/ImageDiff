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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import org.jdesktop.swingx.border.DropShadowBorder;
import org.max.imagediff.AllKeyListener;

public class AppWindow {

	private JFrame frame;
	private JPanel mainPanel;
	private JLayeredPane layeredPane;
	private JScrollPane infoScrollPane;
	private JPanel infoBoxHolder;

	/**
	 * Initialize the contents of the frame.
	 * 
	 * @wbp.parser.entryPoint
	 */
	public void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 80, 900, 650);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public void setToolbar(JToolBar toolBar) {
		frame.getContentPane().add(toolBar, BorderLayout.NORTH);
	}

	public void setMainPanel(JPanel mainPanel) {
		layeredPane = new JLayeredPane();
		layeredPane.setLayout(new FillLayout());

		layeredPane.add(mainPanel, Integer.valueOf(1));
		this.mainPanel = mainPanel;

		frame.getContentPane().add(layeredPane, BorderLayout.CENTER);
	}

	public JPanel getMainPanel() {
		return mainPanel;
	}

	public void setKeyListener(final AllKeyListener keyListener) {

		frame.addFocusListener(new FocusListener() {
			private final KeyEventDispatcher altDisabler = new KeyEventDispatcher() {
				@Override
				public boolean dispatchKeyEvent(KeyEvent e) {
					return keyListener.keyPressed(e);
				}
			};

			@Override
			public void focusGained(FocusEvent e) {
				KeyboardFocusManager.getCurrentKeyboardFocusManager()
						.addKeyEventDispatcher(altDisabler);
			}

			@Override
			public void focusLost(FocusEvent e) {
				KeyboardFocusManager.getCurrentKeyboardFocusManager()
						.removeKeyEventDispatcher(altDisabler);
			}
		});
	}

	public void setInfoBox(InfoBox infoBox) {

		infoBoxHolder = new JPanel();
		infoBoxHolder.setOpaque(false);
		infoBoxHolder.setLayout(new CenterLayout());
		infoBoxHolder.setVisible(false);

		infoBox.setOpaque(true);
		infoBox.setBackground(new Color(255, 221, 191, 235));
		infoBox.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		infoScrollPane = new JScrollPane(infoBox);
		infoScrollPane.setBorder(BorderFactory.createEmptyBorder());
		infoScrollPane.getViewport().setOpaque(false);
		infoScrollPane.setOpaque(false);
		infoScrollPane.setBorder(new DropShadowBorder(Color.GRAY, 10, 0.8F, 10,
				false, false, true, true));

		infoBoxHolder.add(infoScrollPane);

		layeredPane.add(infoBoxHolder, Integer.valueOf(2));
	}

	public void setInfoVisible(boolean show) {
		infoScrollPane.getViewport().setViewPosition(new Point(0, 0));
		infoBoxHolder.setVisible(show);
	}
}
