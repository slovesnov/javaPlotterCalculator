/******************************************************
Copyright (c/c++) 2013-doomsday by Aleksey Slovesnov 
homepage http://slovesnov.users.sourceforge.net/?parser
email slovesnov@yandex.ru
All rights reserved.
******************************************************/

package form;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import common.Helper;


@SuppressWarnings("serial")
public class TestForm extends JFrame{
	
	public TestForm() {
		super("test");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		List<Image> l = new ArrayList<Image>();
		int a[] = { 16, 32, 48, 128 };
		for (int i : a) {
			l.add(Helper.createImageIcon("calculator" + i + ".png").getImage());
		}
		setIconImages(l);

		add(new TestPanel());
		setSize(new Dimension(800, 100));
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public static void main(String[] args) {
		new TestForm();
	}

}
