package main.java.multidownload.gui.components;

import java.awt.Insets;

import javax.swing.JButton;

import main.java.multidownload.Factory;

public class FontButton extends JButton {

	public FontButton(String text) {
		super(text);
		initFont();
	}

	public FontButton() {
		super();
		initFont();
	}

	private void initFont() {
		Insets insets = new Insets(0, 0, 0, 0);
		setMargin(insets);
		setFont(Factory.font);
	}

}
