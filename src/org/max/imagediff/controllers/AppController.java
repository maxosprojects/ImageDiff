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
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.Raster;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.InvalidPreferencesFormatException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.border.Border;

import org.max.imagediff.AllKeyListener;
import org.max.imagediff.ImageHolder;
import org.max.imagediff.conf.ConfManager;
import org.max.imagediff.conf.ImgDescriptor;
import org.max.imagediff.gui.AppWindow;
import org.max.imagediff.gui.InfoBox;
import org.max.imagediff.util.ImageDiffBuilder;
import org.max.imagediff.util.ImageLoader;

public class AppController {

	AppWindow appWindow;
	private MultiPanelController mpCtrl;
	private List<ImgDescriptor> images;
	private JToolBar toolBar;
	private File directory;
	private List<JToggleButton> imgButtons = new ArrayList<>();
	private File[] dirs;
	private int currentDir;
	private JButton btnPrev;
	private JButton btnNext;
	private InfoBox infoBox;
	private boolean altDown;
	private boolean shiftDown;
	private ImgDescriptor diffDescr = new ImgDescriptor("Diff", "");
	private ImageHolder diffController;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AppController controller = new AppController();
					controller.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Start application.
	 */
	private void start() {

		// try {
		// UIManager.setLookAndFeel(new
		// com.nilo.plaf.nimrod.NimRODLookAndFeel());
		// NimRODTheme nt = new NimRODTheme("max.theme");
		//
		// NimRODLookAndFeel nf = new NimRODLookAndFeel();
		// NimRODLookAndFeel.setCurrentTheme(nt);
		// UIManager.setLookAndFeel(nf);
		// } catch (UnsupportedLookAndFeelException e) {
		// e.printStackTrace();
		// }

		try {
			ConfManager.loadProperties();
		} catch (IOException e) {
			JOptionPane
					.showMessageDialog(
							null,
							"There was an error loading application settings."
									+ System.getProperty("line.separator")
									+ "Make sure config.properties exists and has correct format.");
			return;
		}

		try {
			images = ConfManager.getImages();
		} catch (InvalidPreferencesFormatException e) {
			JOptionPane.showMessageDialog(
					null,
					"There was an error loading application settings."
							+ System.getProperty("line.separator")
							+ e.getMessage());
			return;
		}

		appWindow = new AppWindow();
		appWindow.initialize();

		toolBar = new JToolBar("ImageDiff toolbar");
		addToolButtons();
		appWindow.setToolbar(toolBar);
		// appWindow.setMainPanel(new JPanel(new GridLayout(1, 0, 1, 0)));
		JPanel pan = new JPanel();
		pan.setLayout(new BoxLayout(pan, BoxLayout.X_AXIS));
		appWindow.setMainPanel(pan);

		mpCtrl = new MultiPanelController();
		mpCtrl.setMainPanel(appWindow.getMainPanel());

		infoBox = new InfoBox();
		infoBox.setText("Feature description not available");
		appWindow.setInfoBox(infoBox);

		appWindow.setKeyListener(setKeyListener());
	}

	private AllKeyListener setKeyListener() {
		return new AllKeyListener() {
			@Override
			public boolean keyPressed(KeyEvent evt) {

				int evtId = evt.getID();
				int evtCode = evt.getKeyCode();

				if (evtId == KeyEvent.KEY_PRESSED) {
					if (!altDown && evtCode == KeyEvent.VK_ALT) {
						altDown = true;
						processAltKey();
					}
					if (!shiftDown && evtCode == KeyEvent.VK_SHIFT) {
						shiftDown = true;
						processShiftKey();
					}
				}
				if (evtId == KeyEvent.KEY_RELEASED) {
					if (altDown && evtCode == KeyEvent.VK_ALT) {
						altDown = false;
						processAltKey();
					}
					if (shiftDown && evtCode == KeyEvent.VK_SHIFT) {
						shiftDown = false;
						processShiftKey();
					}
				}

				// Let Alt+F4 pass to the system.
				if (evt.getKeyCode() == KeyEvent.VK_F4 && evt.isAltDown()) {
					return false;
				}
				return true;
			}
		};
	}

	private void processShiftKey() {
		mpCtrl.showImageNames(shiftDown);
	}

	private void processAltKey() {
		appWindow.setInfoVisible(altDown);
	}

	private void addToolButtons() {

		toolBar.add(createIconButton("button_folder.png", "folder",
				createFolderButtonListener(), true));

		toolBar.addSeparator();

		ActionListener prevNextListener = createPrevNextListener();
		btnPrev = createIconButton("button_prev.png", "prev", prevNextListener,
				true);
		toolBar.add(btnPrev);
		btnNext = createIconButton("button_next.png", "next", prevNextListener,
				true);
		toolBar.add(btnNext);
		btnPrev.setEnabled(false);
		btnNext.setEnabled(false);

		toolBar.addSeparator();

		JToggleButton button;

		button = createToggleButton(diffDescr, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				JToggleButton btn = (JToggleButton) evt.getSource();
				mpCtrl.setPanelEnabled(0, btn.isSelected());
				diffDescr.setEnabled(btn.isSelected());
			}
		});
		imgButtons.add(button);
		toolBar.add(button);

		ActionListener imgListener = createImgListener();

		for (ImgDescriptor img : images) {
			button = createToggleButton(img, imgListener);
			imgButtons.add(button);
			toolBar.add(button);
		}

		ActionListener zoomListener = createZoomListener();

		toolBar.addSeparator();

		toolBar.add(createIconButton("button_zoom_fit.png", "zoom_fit",
				zoomListener, true));
		toolBar.add(createIconButton("button_zoom_out.png", "zoom_out",
				zoomListener, true));
		toolBar.add(createIconButton("button_zoom_in.png", "zoom_in",
				zoomListener, true));
		toolBar.add(createIconButton("button_zoom_100.png", "zoom_100",
				zoomListener, true));

		toolBar.addSeparator();

		button = createToggleButton("button_grid.png",
				createToggleButtonListener());
		toolBar.add(button);
	}

	private ActionListener createToggleButtonListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				JToggleButton button = (JToggleButton) evt.getSource();
				mpCtrl.showGrid(button.isSelected());
			}
		};
	}

	private ActionListener createFolderButtonListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				openFolder();
			}
		};
	}

	private ActionListener createZoomListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				doZoom(evt.getActionCommand());
			}
		};
	}

	private ActionListener createImgListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				JToggleButton button = (JToggleButton) evt.getSource();
				String command = evt.getActionCommand();

				ImgDescriptor img = getImageByName(command);
				mpCtrl.setPanelEnabled(images.indexOf(img) + 1,
						button.isSelected());
				img.setEnabled(button.isSelected());

				updateDiff(true);
			}
		};
	}

	private void updateDiff(boolean force) {
		if (force || diffDescr.isEnabled()) {
			List<Raster> diffList = new ArrayList<>();
			for (ImgDescriptor img : images) {
				if (img.isEnabled()) {
					diffList.add(img.getImage().getRaster());
				}
			}
			if (diffList.size() > 0) {
				diffDescr.setImage(ImageDiffBuilder.getGrayDiff(diffList));
				diffDescr.setEnabled(true);
				if (diffController != null) {
					diffController.updateImage(diffDescr.getImage());
				}
			} else {
				diffDescr.setEnabled(false);
			}
		}
	}

	private ActionListener createPrevNextListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				if (evt.getActionCommand().equals("next")) {
					if (currentDir < dirs.length - 1) {
						currentDir++;
						buildPanels(dirs[currentDir]);
						readFeatureInfo(dirs[currentDir]);
					}
				} else {
					if (currentDir > 0) {
						currentDir--;
						buildPanels(dirs[currentDir]);
						readFeatureInfo(dirs[currentDir]);
					}
				}
				enablePrevNextButtons();
			}
		};
	}

	private void openFolder() {

		if (directory == null) {
			directory = new File("");
		}
		JFileChooser fc = new JFileChooser(directory.toPath().toAbsolutePath()
				.toString());
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int result = fc.showDialog(appWindow.getMainPanel(), "Open");
		if (result == JFileChooser.APPROVE_OPTION) {
			directory = fc.getSelectedFile();
			dirs = getDirectories(directory);
			if (dirs.length == 0) {
				buildPanels(directory);
			} else {
				buildPanels(dirs[0]);
			}
			currentDir = 0;
			enablePrevNextButtons();

			readFeatureInfo(dirs[0]);
		}
	}

	/**
	 * Tries to read info.feature file and set its contents to infoBox.
	 */
	private void readFeatureInfo(File dir) {
		try {
			String info = "<html><pre>"
					+ new String(Files.readAllBytes(Paths.get(dir.toString()
							+ "/info.feature"))) + "</pre></html>";
			infoBox.setText(info);
		} catch (IOException e) {
			infoBox.setText("Feature description not available");
		}
	}

	private static File[] getDirectories(File directory) {
		File[] tempDirs = directory.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
		return tempDirs;
	}

	private JToggleButton createToggleButton(ImgDescriptor img,
			ActionListener listener) {
		JToggleButton button;
		button = new JToggleButton();
		button.setActionCommand(img.getName());
		button.setText(img.getName());
		button.addActionListener(listener);
		button.setFocusable(false);
		button.setSelected(false);
		button.setEnabled(false);
		return button;
	}

	private JToggleButton createToggleButton(String icon,
			ActionListener listener) {
		JToggleButton button = new JToggleButton();
		button.addActionListener(listener);
		button.setFocusable(false);
		try {
			Image img = ImageIO.read(getClass().getResource("/icons/" + icon));
			button.setIcon(new ImageIcon(img));
		} catch (IOException ex) {
			return null;
		}
		Border innerBorder = BorderFactory.createEmptyBorder(1, 1, 1, 1);
		Border outerBorder = BorderFactory
				.createCompoundBorder(BorderFactory.createEmptyBorder(1, 1, 1,
						1), BorderFactory.createLineBorder(new Color(127, 127,
						127, 50), 1));
		button.setBorder(BorderFactory.createCompoundBorder(outerBorder,
				innerBorder));
		button.setSelected(false);
		return button;
	}

	private void doZoom(String command) {
		switch (command) {
		case "zoom_fit":
			mpCtrl.zoomFit();
			break;
		case "zoom_out":
			mpCtrl.zoomOut();
			break;
		case "zoom_in":
			mpCtrl.zoomIn();
			break;
		case "zoom_100":
			mpCtrl.zoom100();
			break;
		}
	}

	private JButton createIconButton(String icon, String command,
			ActionListener listener, boolean border) {
		JButton button = new JButton();
		button.setActionCommand(command);
		button.addActionListener(listener);
		button.setFocusable(false);
		try {
			Image img = ImageIO.read(getClass().getResource("/icons/" + icon));
			button.setIcon(new ImageIcon(img));
		} catch (IOException ex) {
			return null;
		}
		if (border) {
			Border innerBorder = BorderFactory.createEmptyBorder(1, 1, 1, 1);
			Border outerBorder = BorderFactory.createCompoundBorder(
					BorderFactory.createEmptyBorder(1, 1, 1, 1), BorderFactory
							.createLineBorder(new Color(127, 127, 127, 50), 1));
			button.setBorder(BorderFactory.createCompoundBorder(outerBorder,
					innerBorder));
		} else {
			button.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		}
		return button;
	}

	private ImgDescriptor getImageByName(String name) {
		for (ImgDescriptor img : images) {
			if (img.getName().equals(name)) {
				return img;
			}
		}
		return null;
	}

	private void buildPanels(File dir) {
		mpCtrl.removeAllPanels();

		for (ImgDescriptor img : images) {
			JToggleButton btn = getImgButtonByName(img.getName());
			try {
				img.setImage(ImageLoader.loadImage(dir.getAbsolutePath() + "/"
						+ img.getFilename()));
				img.setEnabled(true);
				btn.setEnabled(true);
				btn.setSelected(true);
			} catch (IOException e) {
				img.setImage(null);
				img.setEnabled(false);
				btn.setSelected(false);
				btn.setEnabled(false);
			}
		}
		updateDiff(true);

		// Add diffImage panel.
		JToggleButton diffBtn = getImgButtonByName(diffDescr.getName());
		if (diffDescr.isEnabled()) {
			diffController = mpCtrl.addPanel(diffDescr);
			diffBtn.setEnabled(true);
		} else {
			diffBtn.setEnabled(false);
		}

		if (!diffBtn.isSelected() && diffBtn.isEnabled()) {
			mpCtrl.setPanelEnabled(0, false);
		}
		for (ImgDescriptor img : images) {
			if (img.isEnabled()) {
				mpCtrl.addPanel(img);
			}
		}
	}

	private JToggleButton getImgButtonByName(String name) {
		for (JToggleButton btn : imgButtons) {
			if (btn.getText().equals(name)) {
				return btn;
			}
		}
		return null;
	}

	private void enablePrevNextButtons() {
		btnPrev.setEnabled(currentDir > 0);
		btnNext.setEnabled(currentDir < dirs.length - 1);
	}
}
