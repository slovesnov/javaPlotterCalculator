/******************************************************
Copyright (c/c++) 2013-doomsday by Aleksey Slovesnov 
homepage http://slovesnov.users.sourceforge.net/?parser
email slovesnov@yandex.ru
All rights reserved.
******************************************************/

package form;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import estimator.ExpressionEstimator;

@SuppressWarnings("serial")
public class TestPanel extends JPanel {
	private JTextField textField[] = new JTextField[4];
	private JLabel label[] = new JLabel[2];
	ExpressionEstimator e=new ExpressionEstimator();
	String caption[]= {"expression","variables","values"};
	//String values[]= {"Sin(pI/4)","x0+2*x1","x0 x1","1 2"};
	String values[]= {"0xa","x0+2*x1","x0 x1","1 2"};

	TestPanel(){
		setLayout(new BorderLayout());
		
		JPanel mainPanel = new JPanel(),p;
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		int i;
		for(i=0;i<4;i++) {
			textField[i]=new JTextField(values[i]);
			textField[i].addKeyListener(new KeyL(i==0?0:1));
			//textField[i].addActionListener(this);
		}
		
		for(i=0;i<2;i++) {
			label[i]=new JLabel();
			label[i].setPreferredSize(new Dimension(250,-1));
		}
		
		p=new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(new JLabel(caption[0]));
		p.add(textField[0]);
		p.add(label[0]);
		mainPanel.add(p);
		
		p=new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		
		for(i=1;i<4;i++) {
			p.add(new JLabel(caption[i-1]));
			p.add(textField[i]);
		}
		p.add(label[1]);
		mainPanel.add(p);
		
		
		for(i=0;i<2;i++) {
			recount(i);
		}
		
		add(mainPanel);
	}
	
	public void recount(int i) {
		String s = textField[i].getText();
		String[] v = {};
		double[] b = {};
		int j;
		
		try {
			if(i==1) {
				v=textField[3].getText().trim().split("\\s+");
				b=new double[v.length];
				j=0;
				for(String a: v ) {
					try {
						b[j++] = Double.parseDouble(a);
					} catch (NumberFormatException e) {
						throw new Exception("can't parse \"" + a + "\"");
					}
				}
				v=textField[2].getText().trim().split("\\s+");
			}
			e.compile(s,v);
			s=e.calculate(b)+"";
		} catch (Exception e) {
			s=e.getMessage();
		}
		label[i].setText(s);
	}
	
	private class KeyL implements KeyListener {
		int i;
		KeyL(int n){
			i=n;
		}
		public void keyTyped(KeyEvent arg0) {
		}

		public void keyPressed(KeyEvent arg0) {
		}

		public void keyReleased(KeyEvent arg0) {
			recount(i);
		}
	}
	

}
