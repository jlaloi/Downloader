package main.java.multidownload.gui.components;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import main.java.multidownload.Factory;

public class FontLabel extends JLabel {
	public FontLabel() {
		super();
		initFont();
	}

	public FontLabel(String text) {
		super(text);
		initFont();
	}

	public FontLabel(ImageIcon icon) {
		super(icon);
		initFont();
	}

	private void initFont() {
		setFont(Factory.font);
	}

	public void center() {
		setHorizontalAlignment(JLabel.CENTER);
	}

	public void right() {
		setHorizontalAlignment(JLabel.RIGHT);
	}

}
