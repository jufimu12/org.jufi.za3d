package org.jufi.za3d;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.*;

public class GameLauncher extends JFrame {
	private static final long serialVersionUID = -829986982144280127L;
	private JPanel panel_settings;
	private JLabel label_description;
	private JButton button_easy, button_medium, button_hard;
	private JCheckBox checkbox_shader;
	private JRadioButton radiobutton_res_0, radiobutton_res_1, radiobutton_res_2;
	private ButtonGroup resolution;
	
	public GameLauncher() {
		super("ZombieApocalypse 3D Launcher");
		
		setSize(350, 150);
		
		initComponents();
		initListeners();
		
		setLocationByPlatform(true);
		
		setVisible(true);
	}
	
	private void initComponents() {
		label_description = new JLabel("Choose a difficulty to start the game.");
		button_easy = new JButton("Easy");
		button_medium = new JButton("Medium");
		button_hard = new JButton("Hard");
		checkbox_shader = new JCheckBox("Shader", true);
		
		radiobutton_res_0 = new JRadioButton("1280x720");
		radiobutton_res_0.setMnemonic(KeyEvent.VK_1);
		radiobutton_res_0.setActionCommand("1280x720");
		radiobutton_res_1 = new JRadioButton("1600x900");
		radiobutton_res_1.setMnemonic(KeyEvent.VK_2);
		radiobutton_res_1.setActionCommand("1600x900");
		radiobutton_res_2 = new JRadioButton("Fullscreen");
		radiobutton_res_2.setMnemonic(KeyEvent.VK_2);
		radiobutton_res_2.setActionCommand("Fullscreen");
		radiobutton_res_2.setSelected(true);
		
		resolution = new ButtonGroup();
		resolution.add(radiobutton_res_0);
		resolution.add(radiobutton_res_1);
		resolution.add(radiobutton_res_2);
		
		panel_settings = new JPanel(new FlowLayout());
		panel_settings.add(radiobutton_res_0);
		panel_settings.add(radiobutton_res_1);
		panel_settings.add(radiobutton_res_2);
		panel_settings.add(checkbox_shader);
		
		setLayout(new BorderLayout(3, 3));
		add(label_description, BorderLayout.PAGE_START);
		add(button_easy, BorderLayout.LINE_START);
		add(button_medium, BorderLayout.CENTER);
		add(button_hard, BorderLayout.LINE_END);
		add(panel_settings, BorderLayout.PAGE_END);
		
	}
	private void initListeners() {
		button_easy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (radiobutton_res_0.isSelected()) Main.game = new Game(0.03f, 2f, 0, checkbox_shader.isSelected());
				else if (radiobutton_res_1.isSelected()) Main.game = new Game(0.03f, 2f, 1, checkbox_shader.isSelected());
				else Main.game = new Game(0.03f, 2f, 2, checkbox_shader.isSelected());
				Main.game.start();
				setVisible(false);
			}
		});
		button_medium.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (radiobutton_res_0.isSelected()) Main.game = new Game(0.07f, 1.5f, 0, checkbox_shader.isSelected());
				else if (radiobutton_res_1.isSelected()) Main.game = new Game(0.07f, 1.5f, 1, checkbox_shader.isSelected());
				else Main.game = new Game(0.07f, 1.5f, 2, checkbox_shader.isSelected());
				Main.game.start();
				setVisible(false);
			}
		});
		button_hard.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (radiobutton_res_0.isSelected()) Main.game = new Game(0.12f, 1f, 0, checkbox_shader.isSelected());
				else if (radiobutton_res_1.isSelected()) Main.game = new Game(0.12f, 1f, 1, checkbox_shader.isSelected());
				else Main.game = new Game(0.12f, 1f, 2, checkbox_shader.isSelected());
				Main.game.start();
				setVisible(false);
			}
		});
		addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent arg0) {
				
			}
			@Override
			public void windowIconified(WindowEvent arg0) {
				
			}
			@Override
			public void windowDeiconified(WindowEvent arg0) {
				
			}
			@Override
			public void windowDeactivated(WindowEvent arg0) {
				
			}
			@Override
			public void windowClosing(WindowEvent arg0) {
				Main.printInfo("Exiting because of closerequest from launcher");
				System.exit(0);
			}
			@Override
			public void windowClosed(WindowEvent arg0) {
				
			}
			@Override
			public void windowActivated(WindowEvent arg0) {
				
			}
		});
	}
}
