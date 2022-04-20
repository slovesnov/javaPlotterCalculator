/******************************************************
Copyright (c/c++) 2013-doomsday by Aleksey Slovesnov 
homepage http://slovesnov.users.sourceforge.net/?parser
email slovesnov@yandex.ru
All rights reserved.
******************************************************/

package demo;

import estimator.ExpressionEstimator;

public class Example1 {

	public static void main(String[] args) {
		double v;
		try {
			v = ExpressionEstimator.calculate("sin(pi/4)");
			System.out.println(v);
			v = ExpressionEstimator.calculate("pow( sin(pi/10)*4+1 , 2)");
			System.out.println(v);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}

	}

}