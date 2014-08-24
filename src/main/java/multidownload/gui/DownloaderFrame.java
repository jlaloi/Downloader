package main.java.multidownload.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Calendar;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import main.java.multidownload.Factory;
import main.java.multidownload.engine.DownloaderMaster;
import main.java.multidownload.gui.components.FontButton;
import main.java.multidownload.gui.components.FontComboBox;
import main.java.multidownload.gui.components.FontLabel;
import main.java.multidownload.gui.components.FontTextField;

public class DownloaderFrame extends JFrame {

	private JProgressBar pb;
	private FontTextField url;
	private FontButton start;
	private FontComboBox nbConnection;
	private DownloaderMaster dm;
	private Download dl;
	private FontLabel statut;

	public DownloaderFrame() {
		pb = new JProgressBar();
		pb.setMinimum(0);
		pb.setMaximum(100);
		url = new FontTextField();
		url.setPreferredSize(new Dimension(280, url.getPreferredSize().height));
		url.setToolTipText("URL");
		nbConnection = new FontComboBox();
		nbConnection.setToolTipText("Nb connection");
		for (int i = 1; i < 16; i++)
			nbConnection.addItem("" + i);
		nbConnection.setSelectedIndex(4);
		start = new FontButton(" Start ");

		setLayout(new BorderLayout());
		JPanel top = new JPanel();
		top.setLayout(new FlowLayout());
		top.add(nbConnection);
		top.add(url);
		top.add(start);

		statut = new FontLabel(" - ");
		statut.setHorizontalAlignment(JLabel.CENTER);

		add(top, BorderLayout.NORTH);
		add(pb, BorderLayout.CENTER);
		add(statut, BorderLayout.SOUTH);

		start.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent arg0) {
				if (url == null || url.getText().trim().length() < 6) {
					return;
				}
				if (dm == null) {
					url.setEditable(false);
					nbConnection.setEnabled(false);
					start.setText(" Stop ");
					dl.start();
				} else {
					dl.setStop(true);
					start.setEnabled(false);
				}
			}
		});

		pack();
		setVisible(true);
		setResizable(false);
		setLocationRelativeTo(getParent());
		setTitle("Downloader");

		dl = new Download();

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dl.setStop(true);
				dispose();
			}
		});

		url.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON2) {
					url.setText("");
				}
				if (e.getButton() == MouseEvent.BUTTON3) {
					Transferable contents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
					if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
						try {
							String link = (String) contents.getTransferData(DataFlavor.stringFlavor);
							url.setText(link);
						} catch (Exception ex) {
						}
					}
				}
			}
		});
		url.requestFocus();
	}

	class Download extends Thread {
		private boolean stop = false;
		private long lastCheck = 0;
		private long lastDownloaded = 0;
		private long speed = 0;
		private long eta = 0;
		private int progress = 0;
		private long downloaded;
		private long time;

		public void run() {
			lastCheck = Calendar.getInstance().getTimeInMillis();
			try {
				dm = new DownloaderMaster(getFile(), url.getText(), nbConnection.getSelectedIndex() + 1);
				dm.start();
				while (!stop && !dm.isAllDone() && !dm.isCancel()) {
					updateStatut();
					sleep(250);
				}
				updateStatut();
				System.out.println("Extra: " + Factory.formatSize(dm.getRealDownloaded() - dm.getDownloaded()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			start.setEnabled(false);
		}

		public void updateStatut() {
			downloaded = dm.getDownloaded();
			time = Calendar.getInstance().getTimeInMillis();
			speed = 0;
			if (downloaded > 0) {
				progress = (int) (100f * downloaded / dm.getFileSize());
				speed = (downloaded - lastDownloaded) / (time - lastCheck);
				if (speed > 0) {
					eta = (dm.getFileSize() - downloaded) / speed;
				}
				System.out.println(speed + " " + eta);
			}
			statut.setText(Factory.formatSize(dm.getDownloaded()) + " on " + Factory.formatSize(dm.getFileSize()) + " - " + Factory.formatSize(speed * 1000) + " (" + dm.getNbConnection() + ") - " + Factory.formatTime(eta));
			setTitle(progress + "% - " + Factory.getName(dm.getFile().getName()));
			pb.setValue(progress);
			lastDownloaded = downloaded;
			lastCheck = time;
		}

		public boolean isStop() {
			return stop;
		}

		public void setStop(boolean stop) {
			this.stop = stop;
			if (stop && dm != null) {
				dm.cancel();
			}
		}
	}

	private File getFile() {
		File file = null;
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setSelectedFile(new File(Factory.getName(url.getText())));
		int returnVal = chooser.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = chooser.getSelectedFile();
		}
		return file;
	}
}
