/******************************************************
Copyright (c/c++) 2013-doomsday by Aleksey Slovesnov 
homepage http://slovesnov.users.sourceforge.net/?parser
email slovesnov@yandex.ru
All rights reserved.
******************************************************/

package calculator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import common.LanguageComboBox;
import common.Helper;
import common.Language;
import estimator.ExpressionEstimator;

@SuppressWarnings("serial")
public class CalculatorPanel extends JPanel implements ActionListener,Language {

	public enum CalculatorLanguage {
		ENGLISH, RUSSIAN
	};

	private static final String languageString[][] = {
			{ "clear", "recount", "copy to memory", "add to buffer", "clear buffer", "expression", "result",
					"common functions", "constants", "trigonometric functions", "hyperbolic function",
					"rounding and additional functions", "memory", "buffer", "error", "scientific calculator" },
			{ "��������", "�����������", "���������� � ������", "�������� � �����", "�������� �����", "���������",
					"���������", "����� �������", "���������", "������������������ �������", "��������������� �������",
					"�������������� ������� � ������� ����������", "������", "�����", "������", "������� �����������" } };

	private static enum STRING_ENUM {
		CLEAR, RECOUNT, COPY_TO_MEMORY, ADD_TO_BUFFER, CLEAR_BUFFER, EXPRESSION, RESULT, COMMON_FUNCTIONS, CONSTANTS,
		TRIGONOMETRIC_FUNCTIONS, HYPERBOLIC_FUNCTIONS, ROUNDING_FUNCTIONS, MEMORY, BUFFER, ERROR, TITLE;

		static final STRING_ENUM GROUP_TITLE[] = { COMMON_FUNCTIONS, CONSTANTS, TRIGONOMETRIC_FUNCTIONS,
				HYPERBOLIC_FUNCTIONS, ROUNDING_FUNCTIONS, };

		static final STRING_ENUM BUTTON[] = { CLEAR, RECOUNT, COPY_TO_MEMORY, ADD_TO_BUFFER, CLEAR_BUFFER };

		static final STRING_ENUM LABEL[] = { EXPRESSION, RESULT, MEMORY, BUFFER, };
	};

	private int language;
	private String config="javaCalculator.cfg";

	private String getLanguageString(STRING_ENUM e) {
		return languageString[language][e.ordinal()];
	}

	private static final String lexer[][] = {
			{ "exp()", "log()", "pow(,)", "sqrt()", "abs()", "random()", "min(,)", "max(,)" },
			{ "pi", "e", "sqrt2", "sqrt1_2", "ln2", "ln10", "log2e", "log10e" },
			{ "sin()", "cos()", "tan()", "cot()", "sec()", "csc()", "asin()", "acos()", "atan()", "acot()", "asec()",
					"acsc()" },
			{ "sinh()", "cosh()", "tanh()", "coth()", "sech()", "csch()", "asinh()", "acosh()", "atanh()", "acoth()",
					"asech()", "acsch()" },
			{ "ceil()", "floor()", "round()", "atan2(,)" } };

	private JTextField textField[] = new JTextField[3];
	private JTextField addVariable[] = new JTextField[4];
	private JTextArea buffer = new JTextArea(7, 7);
	private static String addVariableName[] = { "a", "b", "c", "d" };
	private JButton bbutton[] = new JButton[STRING_ENUM.BUTTON.length];

	private String previous[] = new String[addVariableName.length + 1];
	private final Dimension margin = new Dimension(3, 3);
	private JLabel label[] = new JLabel[4];
	private JPanel groupPanel[] = new JPanel[STRING_ENUM.GROUP_TITLE.length];

	private JButton[][] buttons = new JButton[lexer.length][];
	private JButton aboutButton = new JButton(Helper.createImageIcon("help.png"));
	private Color backgroundColor;
	public static final Color defaultBackgroundColor = UIManager.getColor("Panel.background");
	public static final String HOMEPAGE = "http://sccalculator.sourceforge.net/";

	/*
	 * 
	 */
	public CalculatorPanel() {
		JPanel p, p1, p2, p3, mainPanel;
		int i, j;
		final int maxw = 100;
		Box box;

		this.language = Helper.loadLanguageFromFile(config);

		aboutButton.addActionListener(this);
		buffer.setEditable(false);

		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		this.backgroundColor = defaultBackgroundColor;
		setBackgroundColor(this);
		setBackgroundColor(mainPanel);

		i = 0;
		for (String _s1[] : lexer) {
			buttons[i] = new JButton[_s1.length];
			j = 0;
			for (String _s2 : _s1) {
				buttons[i][j] = new JButton(_s2);
				buttons[i][j].addActionListener(this);
				j++;
			}
			i++;
		}

		for (i = 0; i < previous.length; i++) {
			previous[i] = (i == 0 ? "" : "0");
		}

		for (i = 0; i < textField.length; i++) {
			textField[i] = new JTextField();
			if (i != 0) {
				textField[i].setEditable(false);
				textField[i].setBackground(UIManager.getColor("TextField.background"));
			}
		}

		for (i = 0; i < STRING_ENUM.BUTTON.length; i++) {
			bbutton[i] = new JButton();
			bbutton[i].addActionListener(this);
		}

		for (i = 0; i < STRING_ENUM.GROUP_TITLE.length; i++) {
			groupPanel[i] = new JPanel(
					new GridLayout(i + 1 == STRING_ENUM.GROUP_TITLE.length ? 1 : 2, 0, margin.width, margin.height));
			setBackgroundColor(groupPanel[i]);
		}

		for (i = 0; i < label.length; i++) {
			label[i] = new JLabel();
		}

		textField[0].addKeyListener(new KeyL());

		p1 = new JPanel(new GridLayout(0, 1, 0, 5));
		setBackgroundColor(p1);
		for (i = 0; i < 2; i++) {
			p = new JPanel();
			p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
			setBackgroundColor(p);

			box = Box.createHorizontalBox();
			box.setPreferredSize(new Dimension(maxw, label[i].getPreferredSize().height));
			setBackgroundColor(label[i]);
			box.add(label[i]);
			p.add(box);
			p.add(Box.createRigidArea(margin));

			p.add(createBox(textField[i]));

			for (j = i * 2; j < (i + 1) * 2 + i; j++) {
				p.add(Box.createRigidArea(margin));
				p.add(bbutton[j]);
			}
			p1.add(p);

			if (i == 0) {// add addVariable
				p = new JPanel();
				p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
				setBackgroundColor(p);
				for (j = 0; j < addVariable.length; j++) {
					addVariable[j] = new JTextField(previous[j + 1]);
					if (j != 0) {
						p.add(Box.createRigidArea(new Dimension(20, 0)));
					}
					p.add(new JLabel(addVariableName[j] + "="));
					p.add(createBox(addVariable[j]));
					addVariable[j].addKeyListener(new KeyL());
				}

				p1.add(p);
			}
		}
		mainPanel.add(p1);

		p = new JPanel(new GridLayout(2, 2));
		for (i = 0; i < 4; i++) {
			p2 = groupPanel[i];
			for (JButton b : buttons[i]) {
				p2.add(b);
			}
			p.add(p2);
		}
		mainPanel.add(p);

		p = new JPanel(new GridLayout(1, 2));
		// memory panel
		p3 = new JPanel();
		p3.setLayout(new BoxLayout(p3, BoxLayout.X_AXIS));
		setBackgroundColor(p3);
		box = Box.createHorizontalBox();
		box.setPreferredSize(new Dimension(maxw, label[0].getPreferredSize().height));
		box.add(label[2]);
		p3.add(box);
		p3.add(createBox(textField[2]));
		
		p.add(p3);
		i=4;
		p2 = groupPanel[i];
		for (JButton b : buttons[i]) {
			p2.add(b);
		}
		p.add(p2);
		mainPanel.add(p);

		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		setBackgroundColor(p);

		p2 = new JPanel();
		setBackgroundColor(p2);

		p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
		p2.setPreferredSize(new Dimension(maxw, buffer.getPreferredSize().height));
		p2.setMaximumSize(new Dimension(maxw, Integer.MAX_VALUE));

		for (i = 0; i < 3; i++) {
			p3 = new JPanel();
			p3.setLayout(new BoxLayout(p3, BoxLayout.X_AXIS));
			setBackgroundColor(p3);
			if (i == 0) {
				p3.add(aboutButton);
			} else if (i == 1) {
				p3.add(label[3]);
			} else {
				LanguageComboBox cl = new LanguageComboBox(this);
				box = Box.createHorizontalBox();
				box.setMaximumSize(cl.getPreferredSize());
				box.add(cl);
				p3.add(box);
			}
			p3.add(Box.createHorizontalGlue());
			p2.add(p3);
			if (i != 2) {
				p2.add(Box.createVerticalGlue());
			}
		}
		p.add(p2);
		p.add(new JScrollPane(buffer));

		mainPanel.add(Box.createRigidArea(margin));
		mainPanel.add(p);

		changeLanguage(this.language);
		add(mainPanel);

	}

	void setBackgroundColor(JComponent c) {
		if (backgroundColor != null) {
			c.setBackground(backgroundColor);
		}
	}

	private Box createBox(JComponent f) {
		Box box = Box.createHorizontalBox();
		box.setMaximumSize(new Dimension(Integer.MAX_VALUE, f.getPreferredSize().height));
		box.add(f);
		return box;
	}

	public void actionPerformed(ActionEvent arg0) {
		int i, j;
		String s;
		for (i = 0; i < buttons.length; i++) {
			for (JButton b : buttons[i]) {
				if (arg0.getSource() == b) {
					s = textField[0].getText();

					i = textField[0].getSelectionStart();
					j = textField[0].getSelectionEnd();
					// System.out.println(i+" "+j);
					textField[0].setText(
							s.substring(0, i) + getInsertionString(b.getText(), s.substring(i, j)) + s.substring(j));
					fireKeyPressed();
					return;
				}
			}
		}

		for (i = 0; i < bbutton.length; i++) {
			if (bbutton[i] == arg0.getSource()) {
				switch (STRING_ENUM.BUTTON[i]) {
				case CLEAR:
					textField[0].setText("");
					fireKeyPressed();// need to fire event
					break;

				case RECOUNT:
					recalculate();
					break;

				case COPY_TO_MEMORY:
					textField[2].setText(textField[1].getText());
					break;

				case ADD_TO_BUFFER:
					buffer.append(textField[0].getText() + "=" + textField[1].getText() + "\n");
					break;

				case CLEAR_BUFFER:
					buffer.setText("");
					break;

				default:
					break;

				}
				return;
			}
		}
		if (aboutButton == arg0.getSource()) {
			Helper.openWebpage(HOMEPAGE);
		}
	}

	private void fireKeyPressed() {
		int i;
		JTextField t[] = new JTextField[addVariable.length + 1];
		t[0] = textField[0];
		i = 1;
		for (JTextField f : addVariable) {
			t[i++] = f;
		}

		boolean recount = false;
		for (i = 0; i < t.length; i++) {
			if (!t[i].getText().equals(previous[i])) {
				previous[i] = t[i].getText();
				recount = true;
			}
		}

		if (recount) {
			recalculate();
		}
	}

	private void recalculate() {
		String s = textField[0].getText();
		int i;

		for (i = 0; i < addVariableName.length; i++) {
			Matcher m = getMatcher(addVariableName[i], s);
			s = m.replaceAll("(" + addVariable[i].getText() + ")");// use brackets!
		}

		Color color = Color.black;

		if (s.length() == 0) {
			s = "";
		} else {
			try {
				s = ExpressionEstimator.calculate(s) + "";
			} catch (Exception e) {
				color = Color.red;
				s = getLanguageString(STRING_ENUM.ERROR);
			}
		}
		textField[1].setText(s);

		for (i = 0; i < 2; i++) {
			textField[i].setForeground(color);
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

	public void changeLanguage(int language) {
		this.language = language;
	
		JFrame f = (JFrame) SwingUtilities.windowForComponent(this);
		if(f!=null) {
			f.setTitle(getTitle());
		}
		
		int i;
		for (i = 0; i < STRING_ENUM.BUTTON.length; i++) {
			bbutton[i].setText(getLanguageString(STRING_ENUM.BUTTON[i]));
		}

		for (i = 0; i < label.length; i++) {
			label[i].setText(getLanguageString(STRING_ENUM.LABEL[i]));
		}

		i = 0;
		for (JPanel p : groupPanel) {
			p.setBorder(BorderFactory.createTitledBorder(getLanguageString(STRING_ENUM.GROUP_TITLE[i])));
			i++;
		}

		recalculate();// error text change language

	}

	public int getLanguage() {
		return language;
	}

	public static Matcher getMatcher(String lexer, String s) {
		return Pattern.compile("(?<=^|\\W)" + lexer + "(?=\\W|$)", Pattern.CASE_INSENSITIVE).matcher(s);
	}

	/*
	 * "pow(,)"=2 "exp()"=1 "pi"=0
	 */
	static int getNumberOfArguments(String title) {
		int k = title.indexOf('(');
		if (k == -1) {
			return 0;
		} else {
			return title.indexOf(',') == -1 ? 1 : 2;
		}
	}

	/*
	 * "pow(,)" -> "pow" "log()" -> "log" "pi" -> pi
	 */
	static String getPureTitle(String title, int arguments) {
		return arguments == 0 ? title : title.substring(0, title.indexOf('('));
	}

	static String getInsertionString(String title, String substring) {
		final int arguments = getNumberOfArguments(title);
		final String pureTitle = getPureTitle(title, arguments);

		if (arguments == 0) {// no arguments
			return pureTitle + substring;
		} else {
			return pureTitle + "(" + substring + (arguments == 1 ? "" : ",") + ")";
		}
	}

	public String getTitle() {
		return getLanguageString(STRING_ENUM.TITLE);
	}

	public void saveConfig() {
		Helper.saveLanguageToFile(config,language);	
	}

}