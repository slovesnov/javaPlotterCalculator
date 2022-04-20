/******************************************************
Copyright (c/c++) 2013-doomsday by Aleksey Slovesnov 
homepage http://slovesnov.users.sourceforge.net/?parser
email slovesnov@yandex.ru
All rights reserved.
******************************************************/

package demo;

import estimator.SlowExpressionEstimator;

public class SlowExpressionEstimatorTest {
	public static void main(String[] args) {
		String expression[] = { "sin(pi/4)", "1+2+" };
		for (String s : expression) {
			System.out.print("\"" + s + "\"=");
			try {
				System.out.println(SlowExpressionEstimator.estimate(s));
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
}
