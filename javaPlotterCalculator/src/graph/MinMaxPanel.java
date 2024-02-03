/******************************************************
Copyright (c/c++) 2013-doomsday by Aleksey Slovesnov 
homepage http://slovesnov.users.sourceforge.net/?parser
email slovesnov@yandex.ru
All rights reserved.
******************************************************/

package graph;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Locale;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import estimator.ExpressionEstimator;

@SuppressWarnings("serial")
public class MinMaxPanel extends JPanel {
	private JTextField t[] = new JTextField[2];
	private double v[] = new double[2];
	private JLabel label = new JLabel();
	static Color errorColor = Color.red;
	static Color okColor = Color.black;
	private GraphView view;
	ElementaryGraphPanel el;
	boolean ok[] = new boolean[2];

	public MinMaxPanel(GraphPanel graph, String name, ElementaryGraphPanel el) {
		int i;
		this.view = graph.view;
		this.el = el;
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setBackground(graph.getBackgroundColor());

		if (name != null) {
			setName(name);
		}

		for (i = 0; i < t.length; i++) {
			t[i] = new JTextField();
			t[i].addKeyListener(new KeyL());
		}

		for (Component c : new Component[] { label, t[0], new JLabel(" - "), t[1] }) {
			add(c);
		}

	}

	public void setName(String name) {
		label.setText(name + " ");
	}

	public double getMin() {
		return v[0];
	}

	public double getMax() {
		return v[1];
	}

	public static String format(double v) {
		String s = String.format(Locale.US, "%.8f", v);
		s = s.indexOf(".") < 0 ? s : s.replaceAll("0*$", "").replaceAll("\\.$", "");
		return s;
	}

	void recount() {
		setValues(false, t[0].getText(), t[1].getText());
		view.redrawImage();
	}

	private class KeyL implements KeyListener {
		public void keyTyped(KeyEvent arg0) {
		}

		public void keyPressed(KeyEvent arg0) {
		}

		public void keyReleased(KeyEvent arg0) {
			setValues(false, t[0].getText(), t[1].getText());
			if (el != null) {
				el.recalculate();
			}
			view.redrawImage();
		}
	}

	public void setValues(Double... v) {
		setValues(true, v[0].toString(), v[1].toString());
	}

	public void setValues(String... s) {
		setValues(true, s[0], s[1]);
	}

	public void setValues(boolean settext, String... s) {
		int i;
		double a;
		for (i = 0; i < 2; i++) {
			if (settext) {
				try {
					a = Double.parseDouble(s[i]);
					t[i].setText(format(a));
				} catch (Exception e) {
					t[i].setText(s[i]);
				}
			}
			v[i] = parseValueSetColor(t[i]);
			ok[i] = !Double.isNaN(v[i]);
		}
		if (ok()) {
			if (v[0] >= v[1]) {
				ok[0] = ok[1] = false;
				for (i = 0; i < 2; i++) {
					t[i].setForeground(errorColor);
				}
			}
		}
	}

	boolean ok() {
		return ok[0] && ok[1];
	}

	static double parseValueSetColor(JTextField t) {
		return parseValueSetColor(t, false);
	}

	static double parseValueSetColor(JTextField t, boolean steps) {
		double v;
		try {
			v = ExpressionEstimator.calculate(t.getText());
			if (steps && (v <= 0 || v != (int) v)) {
				throw new Exception();
			}

		} catch (Exception e) {
			v = Double.NaN;
		}
		// v==Double.NaN is not working
		t.setForeground(Double.isNaN(v) ? errorColor : okColor);
		return v;
	}

}
