/******************************************************
Copyright (c/c++) 2013-doomsday by Aleksey Slovesnov 
homepage http://slovesnov.users.sourceforge.net/?parser
email slovesnov@yandex.ru
All rights reserved.
******************************************************/

package graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import common.Helper;

import estimator.ExpressionEstimator;

@SuppressWarnings("serial")
public class ElementaryGraphPanel extends JPanel implements ActionListener {
	private final static String typeLabelString[] = { "y(x)", " r(a)", "x(t)", "y(t)" };
	private JComboBox<String> type = new JComboBox<>();
	private JLabel label[] = new JLabel[4];
	private JPanel up[] = new JPanel[2];
	private JTextField tf[] = new JTextField[3];
	private GraphPanel graph;
	private final static String startFunctionString[] = { "tan(x)", "3*sin(3*a)", "3*cos(t)", "3*sin(t)" };
	private boolean ok = true;
	private ExpressionEstimator[] estimator = new ExpressionEstimator[2];
	private int previousType;
	final String GRAPH_PARAMETERS[] = { "0", "2*pi", "5 * 1000" };// min,max,step
	private int steps;
	private static final String VARIABLE_NAME[] = { "x", "a", "t" };
	private JButton button = new JButton();
	Color color;
	private MinMaxPanel parameterMinMaxPanel;
	private JPanel parameterPanel = new JPanel();
	private boolean m_signals;

	public ElementaryGraphPanel(GraphPanel graph, Color color) {
		int i;
		JPanel p;

		m_signals = true;

		this.graph = graph;
		this.color = color;
		setBackground(graph.getBackgroundColor());

		for (i = 0; i < tf.length; i++) {
			tf[i] = new JTextField(i == 2 ? GRAPH_PARAMETERS[2] : "");
			tf[i].addKeyListener(new KeyL());
		}

		parameterMinMaxPanel = new MinMaxPanel(graph, null, this);
		parameterMinMaxPanel.setValues(GRAPH_PARAMETERS);

		button = new JButton(Helper.createImageIcon("minus.png"));
		button.addActionListener(this);

		type.addActionListener(this);
		type.setMaximumSize(type.getPreferredSize());

		for (i = 0; i < label.length; i++) {
			label[i] = new JLabel();
			if (i < 2) {
				label[i].setForeground(color);
				estimator[i] = new ExpressionEstimator();
				p = up[i] = new JPanel();
				p.setBackground(graph.getBackgroundColor());
				p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
				p.add(label[i]);
				p.add(tf[i]);
				p.setMaximumSize(new Dimension(Integer.MAX_VALUE, tf[i].getPreferredSize().height));
				if (i == 1) {
					p.setVisible(false);
				}
			}
		}

		parameterPanel.setLayout(new BoxLayout(parameterPanel, BoxLayout.X_AXIS));
		parameterPanel.setBackground(graph.getBackgroundColor());

		parameterPanel.add(parameterMinMaxPanel);
		parameterPanel.add(label[3]);
		parameterPanel.add(tf[2]);
		parameterPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, tf[2].getPreferredSize().height));

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		for (JComponent _c : new JComponent[] { up[0], up[1], label[2], type, parameterPanel, button }) {
			add(Box.createRigidArea(new Dimension(1, 0)));
			add(_c);
		}

		setGraphType(0);
		changeLanguage();
		recalculate();
	}

	public void actionPerformed(ActionEvent e) {
		if (!m_signals) {
			return;
		}
		if (type == e.getSource()) {
			setGraphType(type.getSelectedIndex());
			recalculate();
		} else if (button == e.getSource()) {
			if (graph.getElemenentarySize() != 1) {
				graph.removeGraph(this);
			}
		}
	}

	private String getExtendedText(int type) {
		return " " + typeLabelString[type] + " = ";
	}

	private void setGraphType(int type) {
		label[0].setText(getExtendedText(type));

		tf[0].setText(startFunctionString[type]);
		previousType = type;

		if (isParametrical()) {
			label[1].setText(getExtendedText(3));
			tf[1].setText(startFunctionString[type + 1]);
		}

		up[1].setVisible(isParametrical());
		parameterPanel.setVisible(!isStandard());

		if (!isStandard()) {
			parameterMinMaxPanel.setName(VARIABLE_NAME[type]);
		}

		fireKeyPressed();
	}

	public void fireKeyPressed() {
		recalculate();
		graph.redraw();
	}

	void recalculate() {
		int i;
		double v;
		ok = parameterMinMaxPanel.ok();
		for (i = 0; i < (isParametrical() ? 2 : 1); i++) {
			JTextField t = tf[i];
			try {
				estimator[i].compile(tf[i].getText(), VARIABLE_NAME[previousType]);
				t.setForeground(Color.black);
			} catch (Exception e) {
				ok = false;
				t.setForeground(Color.RED);
			}
		}
		if (!isStandard()) {
			v = MinMaxPanel.parseValueSetColor(tf[2], true);
			// v==Double.NaN is not working
			if (Double.isNaN(v)) {
				ok = false;
			} else {
				steps = (int) v;
			}
		}

	}

	private class KeyL implements KeyListener {
		public void keyTyped(KeyEvent arg0) {
		}

		public void keyPressed(KeyEvent arg0) {
		}

		public void keyReleased(KeyEvent arg0) {
			fireKeyPressed();
		}
	}

	public boolean ok() {
		return ok;
	}

	public double calculate(int i, double v) throws Exception {// throws if invalid number of arguments
		return estimator[i].calculate(v);
	}

	public boolean isStandard() {
		return previousType == 0;
	}

	public boolean isPolar() {
		return previousType == 1;
	}

	public boolean isParametrical() {
		return previousType == 2;
	}

	public double getParameterMin() {
		return parameterMinMaxPanel.getMin();
	}

	public double getParameterMax() {
		return parameterMinMaxPanel.getMax();
	}

	public double getParameterStep() {
		return (getParameterMax() - getParameterMin()) / steps;
	}

	public void enableClose(boolean enable) {
		button.setEnabled(enable);
	}

	public Color getColor() {
		return color;
	}

	public void changeLanguage() {
		label[2].setText(graph.getLanguageString(GraphPanel.STRING_ENUM.TYPE) + " ");
		label[3].setText(graph.getLanguageString(GraphPanel.STRING_ENUM.STEPS) + " ");
		refillCombo();
	}

	private void refillCombo() {
		m_signals = false;
		int i = type.getSelectedIndex();
		if (i == -1) {
			i = 0;
		}
		type.removeAllItems();
		for (GraphPanel.STRING_ENUM e : GraphPanel.STRING_ENUM.TYPES) {
			type.addItem(graph.getLanguageString(e));
		}
		type.setSelectedIndex(i);
		m_signals = true;

	}
}
