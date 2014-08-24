package main.java.multidownload;

import javax.swing.UIManager;

import main.java.multidownload.gui.DownloaderFrame;

public class Launcher {

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
		new DownloaderFrame();
	}

}
