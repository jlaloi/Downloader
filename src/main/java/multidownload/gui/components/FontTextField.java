package main.java.multidownload.gui.components;

import javax.swing.JTextField;

import main.java.multidownload.Factory;

public class FontTextField extends JTextField {

	public FontTextField(String text) {
		super(text);
		initFont();
	}

	public FontTextField() {
		super();
		initFont();
	}

	private void initFont() {
		setFont(Factory.font);
	}

}
