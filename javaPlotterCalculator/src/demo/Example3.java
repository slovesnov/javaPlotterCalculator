/******************************************************
Copyright (c/c++) 2013-doomsday by Aleksey Slovesnov 
homepage http://slovesnov.users.sourceforge.net/?parser
email slovesnov@yandex.ru
All rights reserved.
******************************************************/

package demo;

import estimator.ExpressionEstimator;

public class Example3 {

	public static void main(String[] args) throws Exception {
		String s = "";
		ExpressionEstimator e = new ExpressionEstimator();

		e.compile("x0+2*sin(pi*x1)");
		s += e.calculate(1, .25) + "\n";
		double v[] = { 2, 1. / 6 };
		s += e.calculate(v) + "\n";
		e.compile("x0+2*X1");// case insensitive variable names
		s += e.calculate(1, .25) + "\n";

		e = new ExpressionEstimator("x0+2*x1");
		s += e.calculate(1, .25) + "\n";

		e = new ExpressionEstimator();
		e.compile("a+2*b", "a", "b");// or e.compile("a+2*b", new String[] { "a", "b" });
		s += e.calculate(1, .25) + "\n";

		// e = new ExpressionEstimator("a+2*A", "a", "A");//case sensitive variable
		// names
		// or e = new ExpressionEstimator("a+2*A", new String[] { "a", "A" });
		s += e.calculate(1, .25) + "\n";

		System.out.println(s);
	}

}