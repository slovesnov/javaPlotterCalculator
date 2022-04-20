/******************************************************
Copyright (c/c++) 2013-doomsday by Aleksey Slovesnov 
homepage http://slovesnov.users.sourceforge.net/?parser
email slovesnov@yandex.ru
All rights reserved.
******************************************************/

package calculator;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import common.Helper;

@SuppressWarnings("serial")
public class CalculatorFrame extends JFrame {

	public CalculatorFrame() {
		CalculatorPanel c = new CalculatorPanel();
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				c.saveConfig();
				System.exit(0);
			}
		});

		setIconImages(Helper.createImageIcons("calculator"));

		add(c);
		setTitle(c.getTitle());

		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public static void main(String[] args) {
		new CalculatorFrame();
	}
}