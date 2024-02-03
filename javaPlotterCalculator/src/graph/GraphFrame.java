/******************************************************
Copyright (c/c++) 2013-doomsday by Aleksey Slovesnov 
homepage http://slovesnov.users.sourceforge.net/?parser
email slovesnov@yandex.ru
All rights reserved.
******************************************************/

package graph;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import common.Helper;

@SuppressWarnings("serial")
public class GraphFrame extends JFrame {

	public GraphFrame() {
		GraphPanel g = new GraphPanel();
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				g.saveConfig();
				System.exit(0);
			}
		});

		setIconImages(Helper.createImageIcons("graph"));

		add(g);
		setTitle(g.getTitle());
		setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public static void main(String[] args) {
		new GraphFrame();
	}
}
