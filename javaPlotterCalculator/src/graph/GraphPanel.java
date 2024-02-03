/******************************************************
Copyright (c/c++) 2013-doomsday by Aleksey Slovesnov 
homepage http://slovesnov.users.sourceforge.net/?parser
email slovesnov@yandex.ru
All rights reserved.
******************************************************/

package graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import common.Helper;
import common.Language;
import common.LanguageComboBox;

@SuppressWarnings("serial")
public class GraphPanel extends JPanel implements ActionListener, Language {

	private static final String languageString[][] = {
			{ "plotter", "reset", "type", "standard", "polar (a is angle)", "parametrical", "steps" },
			{ "построитель графиков", "сброс", "тип", "стандартный", "пол€рный (a - угол)", "параметрический",
					"шагов" } };

	static enum STRING_ENUM {
		PLOTTER, RESET, TYPE, STANDARD, POLAR, PARAMETRICAL, STEPS;

		static final STRING_ENUM TYPES[] = { STANDARD, POLAR, PARAMETRICAL };
	};

	Vector<ElementaryGraphPanel> elementary = new Vector<>();
	private final static Color[] graphColor = { new Color(0, 0, 0), new Color(205, 92, 92), new Color(0, 128, 0),
			new Color(0, 0, 255), new Color(0x80, 0x80, 0), new Color(255, 165, 0) };

	private JPanel up = new JPanel();
	GraphView view = new GraphView(this);
	private JButton resetButton = new JButton();
	private JButton pictureButton[] = new JButton[4];

	private final static double MAXY = 5;
	MinMaxPanel axisLimits[] = new MinMaxPanel[2];
	private JPanel mainPanel;
	private final Dimension margin = new Dimension(3, 3);
	private String axisName[] = { "x", "y" };
	private JLabel info = new JLabel();

	public static final Color defaultBackgroundColor = UIManager.getColor("Panel.background");
	private Color backgroundColor;
	private static final String HOMEPAGE = "http://javadiagram.sourceforge.net";
	int language;
	private String config = "javaGraph.cfg";

	public GraphPanel() {
		int i;
		this.backgroundColor = defaultBackgroundColor;
		setLayout(new BorderLayout());

		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		// mainPanel.setBackground(backgroundColor);

		resetButton.addActionListener(this);

		up.setLayout(new BoxLayout(up, BoxLayout.X_AXIS));
		up.setMaximumSize(new Dimension(Integer.MAX_VALUE, resetButton.getPreferredSize().height));
		up.setBackground(backgroundColor);

		final String bname[] = { "plus", "viewmag+", "viewmag-", "help" };
		for (i = 0; i < 4; i++) {
			pictureButton[i] = new JButton(Helper.createImageIcon(bname[i] + ".png"));
			pictureButton[i].addActionListener(this);
			if (i < 3) {
				up.add(pictureButton[i]);
			}
		}
		up.add(resetButton);

		for (i = 0; i < 2; i++) {
			axisLimits[i] = new MinMaxPanel(this, axisName[i], null);
			up.add(axisLimits[i]);
		}
		up.add(Box.createRigidArea(margin));

		language = Helper.loadLanguageFromFile(config);// before LanguageComboBox creation

		info.setMinimumSize(new Dimension(180, -1));
		up.add(info);
		LanguageComboBox cl = new LanguageComboBox(this);
		up.add(cl);
		up.add(pictureButton[3]);

		reset();

		changeLanguage(language);

		add(new JScrollPane(mainPanel));
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	boolean ok() {
		return axisLimits[0].ok() && axisLimits[1].ok();
	}

	public ElementaryGraphPanel getElemenentary(int i) {
		return elementary.elementAt(i);
	}

	public int getElemenentarySize() {
		return elementary.size();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (resetButton == arg0.getSource()) {
			reset();
		} else {
			for (int i = 0; i < pictureButton.length; i++) {
				if (pictureButton[i] == arg0.getSource()) {
					if (i == 0) {
						addGraph();
					} else if (i == 3) {
						Helper.openWebpage(HOMEPAGE);
					} else {
						zoom(i == 2);
					}
					return;
				}
			}
		}

	}

	public void zoom(boolean increaseArea) {
		Double v[] = new Double[2];
		int i, j;
		MinMaxPanel m;
		double multiply = increaseArea ? 2 : .5;
		for (i = 0; i < 2; i++) {
			m = axisLimits[i];
			for (j = 0; j < 2; j++) {
				v[j] = (m.getMin() + m.getMax()) / 2 + (j == 0 ? -1 : +1) * multiply * (m.getMax() - m.getMin()) / 2;
			}
			m.setValues(v);
		}
		view.redrawImage();
	}

	private void reset() {
		elementary.clear();
		addGraph();
		view.lastSize.width = 0;
	}

	void resetAxis() {
		double k = view.lastSize.width;
		k /= view.lastSize.height;
		Double[] d = { -MAXY * k, MAXY * k };
		axisLimits[0].setValues(d);
		axisLimits[1].setValues(-MAXY, MAXY);
		redraw();
	}

	void redraw() {
		view.redrawImage();
		view.repaint();
	}

	public void removeGraph(ElementaryGraphPanel elementaryGraphPanel) {
		elementary.remove(elementaryGraphPanel);
		updateMainPanel();
	}

	public void addGraph() {
		int i;
		Vector<Integer[]> v = new Vector<>();
		for (i = 0; i < graphColor.length; i++) {
			v.add(new Integer[] { i, 0 });
		}
		for (ElementaryGraphPanel a : elementary) {
			v.get(Arrays.asList(graphColor).indexOf(a.color))[1]++;
		}
		Integer[] u = v.stream().min((a, b) -> (a[1] < b[1] || (a[0] < b[0] && a[1] == b[1])) ? -1 : 1).get();

		elementary.add(new ElementaryGraphPanel(this, graphColor[u[0]]));
		updateMainPanel();
	}

	private void updateMainPanel() {
		mainPanel.removeAll();
		mainPanel.add(up);
		for (ElementaryGraphPanel e : elementary) {
			mainPanel.add(e);
			e.enableClose(true);
		}

		mainPanel.add(view);

		if (elementary.size() == 1) {
			elementary.elementAt(0).enableClose(false);
		}

		revalidate();
		redraw();

	}

	public void updateMousePoint(double x, double y) {
		int i;
		String s = "";
		for (i = 0; i < 2; i++) {
			s += axisName[i] + " = " + MinMaxPanel.format(i == 0 ? x : y);
			if (i == 0) {
				s += " ";
			}
		}
		info.setText(s);
	}

	public void mouseExited() {
		info.setText("");
	}

	@Override
	public void changeLanguage(int language) {
		this.language = language;
		JFrame f = (JFrame) SwingUtilities.windowForComponent(this);
		if (f != null) {
			f.setTitle(getTitle());
		}

		resetButton.setText(getLanguageString(STRING_ENUM.RESET));
		for (ElementaryGraphPanel g : elementary) {
			g.changeLanguage();
		}
	}

	public String getTitle() {
		return getLanguageString(STRING_ENUM.PLOTTER);
	}

	String getLanguageString(STRING_ENUM e) {
		return languageString[language][e.ordinal()];
	}

	@Override
	public int getLanguage() {
		return language;
	}

	public void saveConfig() {
		Helper.saveLanguageToFile(config, language);
	}

}
