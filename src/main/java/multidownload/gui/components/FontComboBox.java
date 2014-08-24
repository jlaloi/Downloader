package main.java.multidownload.gui.components;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComboBox;

import main.java.multidownload.Factory;

public class FontComboBox extends JComboBox<String> {

	private static final long serialVersionUID = 1L;

	private FontComboBox me;

	public FontComboBox() {
		super();
		me = this;
		this.setBorder(null);
		this.setBackground(null);
		this.addMouseListener(new MouseAdapter() {
			public void mouseExited(MouseEvent arg0) {
				setCursor(Cursor.getDefaultCursor());
			}

			public void mouseEntered(MouseEvent arg0) {
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
		});
		this.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					if (me.getSelectedIndex() + 1 < me.getItemCount()) {
						me.setSelectedIndex(me.getSelectedIndex() + 1);
					}
				}
				if (e.getButton() == MouseEvent.BUTTON2) {
					if (me.getSelectedIndex() - 1 >= 0) {
						me.setSelectedIndex(me.getSelectedIndex() - 1);
					}
				}
			}
		});
		initFont();
	}

	private void initFont() {
		setFont(Factory.font);
	}

}
