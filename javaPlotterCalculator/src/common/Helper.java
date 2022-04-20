/******************************************************
Copyright (c/c++) 2013-doomsday by Aleksey Slovesnov 
homepage http://slovesnov.users.sourceforge.net/?parser
email slovesnov@yandex.ru
All rights reserved.
******************************************************/

package common;

import java.awt.Desktop;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Helper {

	/** Returns an ImageIcon, or null if the path was invalid. */
	public static ImageIcon createImageIcon(String path) {

		path = "/images/" + path;
		java.net.URL imgURL = Helper.class.getResource(path);

		/*
		 * path="images/"+path; java.net.URL imgURL
		 * =ClassLoader.getSystemResource(path);
		 */
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	public static void openWebpage(String address) {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		boolean open = false;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(new URI(address));
				open = true;
			} catch (Exception e) {
			}
		}
		if (!open) {
			JOptionPane.showMessageDialog(null, "cann't browse " + address, "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public static 	List<Image> createImageIcons(String path) {
		List<Image> l = new ArrayList<Image>();
		int a[] = { 16, 32, 48, 128 };
		for (int i : a) {
			l.add(Helper.createImageIcon(path + i + ".png").getImage());
		}
		return l;
	}	

	public static int loadLanguageFromFile(String fileName) {
		int l=0;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String line;
			while ((line = reader.readLine()) != null) {
				l=Integer.parseInt(line);
				break;
			}
			reader.close();
		}
	    catch (Exception e) {
		}
		return l;	
	}

	public static void saveLanguageToFile(String fileName,int language) {
		try {
			BufferedWriter w=new BufferedWriter(new FileWriter(fileName));
			w.write(language+"");
			w.close();
		} catch (IOException e) {
		}
	}
}
