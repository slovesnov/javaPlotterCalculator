/******************************************************
Copyright (c/c++) 2013-doomsday by Aleksey Slovesnov 
homepage http://slovesnov.users.sourceforge.net/?parser
email slovesnov@yandex.ru
All rights reserved.
******************************************************/

package graph;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GraphView extends JPanel implements MouseListener, MouseMotionListener {

	private GraphPanel graph;
	Dimension lastSize;
	private BufferedImage image;
	private Point point[] = new Point[2];

	GraphView(GraphPanel graph) {
		this.graph = graph;
		setLayout(new BorderLayout());
		// setMinimumSize(new Dimension(100,100));//at least 100 pixels heights
		lastSize = new Dimension(0, 0);
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	protected void paintComponent(Graphics _g) {
		if (!getSize().equals(lastSize)) {
			redrawImage();
		}
		Graphics2D g2 = (Graphics2D) _g;
		g2.drawImage(image, 0, 0, null);
		if (point[0] != null && point[1] != null) {
			Stroke drawingStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
					new float[] { 9 }, 0);
			g2.setStroke(drawingStroke);
			g2.drawRect(Math.min(point[0].x, point[1].x), Math.min(point[0].y, point[1].y),
					Math.abs(point[1].x - point[0].x), Math.abs(point[1].y - point[0].y));
		}
	}

	public void mouseClicked(MouseEvent arg0) {
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
		if (point[0] != null) {
			point[0] = null;
			repaint();
		}
		graph.mouseExited();
	}

	public void mousePressed(MouseEvent arg0) {
		if (arg0.getButton() == MouseEvent.BUTTON1) {
			point[0] = arg0.getPoint();
		} else if (arg0.getButton() == MouseEvent.BUTTON3) {
			graph.zoom(true);
		}
	}

	public void mouseReleased(MouseEvent arg0) {
		// System.out.println("mr");
		if (point[1] != null && point[0] != null && point[0].x != point[1].x && point[0].y != point[1].y) {
			double a[] = new double[2];
			int i, j;
			for (j = 0; j < 2; j++) {
				for (i = 0; i < 2; i++) {
					a[i] = j == 0 ? getX(point[i].x) : getY(point[i].y);
				}
				graph.axisLimits[j].setValues(Math.min(a[0], a[1]), Math.max(a[0], a[1]));
			}
			graph.redraw();
		}
		point[0] = null;
	}

	// screen->[minx,maxx]
	private double getX(int x) {
		double min = graph.axisLimits[0].getMin();
		double max = graph.axisLimits[0].getMax();
		return min + x * (max - min) / getSize().width;
	}

	// screen->[miny,maxy]
	private double getY(int y) {
		double min = graph.axisLimits[1].getMin();
		double max = graph.axisLimits[1].getMax();
		return max - y * (max - min) / getSize().height;
	}

	// [minx,maxx]->screen
	private int getScreenX(double x) {
		double min = graph.axisLimits[0].getMin();
		double max = graph.axisLimits[0].getMax();
		return (int) ((x - min) * getSize().width / (max - min));
	}

	// [miny,maxy]->screen
	private int getScreenY(double y) {
		double min = graph.axisLimits[1].getMin();
		double max = graph.axisLimits[1].getMax();
		return (int) ((max - y) * getSize().height / (max - min));
	}

	public void mouseDragged(MouseEvent arg0) {
		// System.out.println("md"+point[0]);
		if (point[0] != null) {
			point[1] = arg0.getPoint();
			repaint();
		}
	}

	public void mouseMoved(MouseEvent arg0) {
		Point point = arg0.getPoint();
		graph.updateMousePoint(getX(point.x), getY(point.y));
	}

	public void redrawImage() {
		int i, j;
		double t, r, x, y;

		if (lastSize.width == 0 && getSize().width != 0) {// first drawing update axis
			lastSize = getSize();
			graph.resetAxis();
			return;
		}
		lastSize = getSize();
		if (lastSize.width == 0 || lastSize.height == 0) {
			return;
		}

		image = new BufferedImage(getSize().width, getSize().height, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();
		Dimension d = getSize();

		g.setColor(graph.getBackgroundColor());
		g.fillRect(0, 0, d.width, d.height);

		if (!graph.ok()) {
			repaint();
			return;
		}

		g.setColor(Color.black);
//		g.drawOval(0, 0, d.width, d.height);		

		i = getScreenX(0);
		g.drawLine(i, 0, i, d.height);

		i = getScreenY(0);
		g.drawLine(0, i, d.width, i);

		for (ElementaryGraphPanel e : graph.elementary) {
			if (!e.ok()) {
				continue;
			}

			g.setColor(e.getColor());
			try {
				if (e.isStandard()) {
					for (i = 0; i < d.width; i++) {
						y = e.calculate(0, getX(i));
						j = getScreenY(y);
						if (!Double.isNaN(y)) {
							g.drawLine(i, j, i, j);
						}
					}
				} else if (e.isPolar() || e.isParametrical()) {
					for (t = e.getParameterMin(); t <= e.getParameterMax(); t += e.getParameterStep()) {
						if (e.isPolar()) {
							r = e.calculate(0, t);
							x = r * Math.cos(t);
							y = r * Math.sin(t);
						} else {
							x = e.calculate(0, t);
							y = e.calculate(1, t);
						}
						i = getScreenX(x);
						j = getScreenY(y);
						if (!Double.isNaN(x) && !Double.isNaN(y)) {
							g.drawLine(i, j, i, j);
						}
					}
				}
			} catch (Exception ex) {
				// on error of calculate function goes to the next graph
			}
		}
		repaint();
	}

	public RenderedImage getImage() {
		return image;
	}
}
