/******************************************************
Copyright (c/c++) 2013-doomsday by Aleksey Slovesnov 
homepage http://slovesnov.users.sourceforge.net/?parser
email slovesnov@yandex.ru
All rights reserved.
******************************************************/

package estimator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionEstimator {

	private enum OPERATOR {
		/*
		 * note OPERATOR enums is case sensitive (cause use OPERATOR.valueof())! use
		 * only capital characters,names equal to parsing string PLUS should be first in
		 * enum. Common operators should go in a row. Order is important. POW, ATAN2,
		 * MIN, MAX should go in a row see parse3 function PLUS-POW should go in row
		 * getToken() POW-SQRT should go in row from parse3() finally PLUS should be
		 * first, PLUS-SQRT should goes in row
		 */
		PLUS, MINUS, MULTIPLY, DIVIDE, LEFT_BRACKET, RIGHT_BRACKET, LEFT_SQUARE_BRACKET, RIGHT_SQUARE_BRACKET,
		LEFT_CURLY_BRACKET, RIGHT_CURLY_BRACKET, COMMA, POW, ATAN2, MIN, MAX, SIN, COS, TAN, COT, SEC, CSC, ASIN, ACOS,
		ATAN, ACOT, ASEC, ACSC, SINH, COSH, TANH, COTH, SECH, CSCH, ASINH, ACOSH, ATANH, ACOTH, ASECH, ACSCH, RANDOM,
		CEIL, FLOOR, ROUND, ABS, SIGN, EXP, LOG, SQRT, X, NUMBER, UNARY_MINUS, END
	}

	private enum CONSTANT_NAME {
		PI, E, SQRT2, SQRT1_2, LN2, LN10, LOG2E, LOG10E // Constants should go in a row. Order is important
	}

	private static final double CONSTANT_VALUE[] = { Math.PI, Math.E, Math.sqrt(2), Math.sqrt(.5), Math.log(2),
			Math.log(10), 1. / Math.log(2), 1. / Math.log(10) };

	private Node root = null;
	private byte[] expression;
	private double tokenValue;
	private OPERATOR operator;
	private int position;
	private double[] argument;
	private int arguments;

	private class Node {
		OPERATOR operator;
		double value;
		Node left, right;

		private void init(OPERATOR operator, double value, Node left) {
			this.operator = operator;
			this.value = value;
			this.left = left;
		}

		Node(OPERATOR operator, Node left) {
			init(operator, 0, left);
		}

		Node(OPERATOR operator) {
			init(operator, 0, null);
		}

		Node(OPERATOR operator, double value) {
			init(operator, value, null);
		}

		double calculate() throws Exception {
			double l = left == null ? 0 : left.calculate();
			double r = right == null ? 0 : right.calculate();
			switch (operator) {

			case NUMBER:
				return value;

			case PLUS:
				return l + r;

			case MINUS:
				return l - r;

			case MULTIPLY:
				return l * r;

			case DIVIDE:
				return l / r;

			case UNARY_MINUS:
				return -l;

			case SIN:
				return Math.sin(l);

			case COS:
				return Math.cos(l);

			case TAN:
				return Math.tan(l);

			case COT:
				return 1 / Math.tan(l);

			case SEC:
				return 1 / Math.cos(l);

			case CSC:
				return 1 / Math.sin(l);

			case ASIN:
				return Math.asin(l);

			case ACOS:
				return Math.acos(l);

			case ATAN:
				return Math.atan(l);

			case ACOT:
				return Math.PI / 2 - Math.atan(l);

			case ASEC:
				return Math.acos(1 / l);

			case ACSC:
				return Math.asin(1 / l);

			case SINH:
				return Math.sinh(l);

			case COSH:
				return Math.cosh(l);

			case TANH:
				return Math.tanh(l);

			case COTH:
				return 1 / Math.tanh(l);

			case SECH:
				return 1 / Math.cosh(l);

			case CSCH:
				return 1 / Math.sinh(l);

			// functions Math.asinh, Math.acosh, Math.atanh do not exists
			case ASINH:
				return Math.log(l + Math.sqrt(l * l + 1));

			case ACOSH:
				return Math.log(l + Math.sqrt(l * l - 1));

			case ATANH:
				return Math.log((1 + l) / (1 - l)) / 2;

			case ACOTH:
				return Math.log((l + 1) / (l - 1)) / 2;

			case ASECH:
				return Math.log((1 + Math.sqrt(1 - l * l)) / l);

			case ACSCH:
				return Math.log(1 / l + Math.sqrt(1 + l * l) / Math.abs(l));

			case RANDOM:
				return Math.random();

			case CEIL:
				return Math.ceil(l);

			case FLOOR:
				return Math.floor(l);

			case ROUND:
				return Math.round(l);

			case ABS:
				return Math.abs(l);

			case SIGN:
				return Math.signum(l);

			case EXP:
				return Math.exp(l);

			case LOG:
				return Math.log(l);

			case SQRT:
				return Math.sqrt(l);

			case POW:
				return Math.pow(l, r);

			case ATAN2:
				return Math.atan2(l, r);

			case MIN:
				return Math.min(l, r);

			case MAX:
				return Math.max(l, r);

			case X:
				return argument[(int) value];

			default:
				throw new Exception("Node.calculate error");
			}
		}
	}

	private boolean isLetter() {
		return Character.isLetter(expression[position]);
	}

	private boolean isDigit() {
		return Character.isDigit(expression[position]);
	}

	private boolean isPoint() {
		return expression[position] == '.';
	}

	private boolean isDigitOrPoint() {
		return isDigit() || isPoint();
	}

	private boolean isFunctionSymbol() {
		byte c = expression[position];
		return Character.isLetterOrDigit(c) || c == '_';
	}

	private void getToken() throws Exception {
		int i, j, k, sh;
		String s;

		if (position == expression.length - 1) {
			operator = OPERATOR.END;
		} else if ((i = "+-*/()[]{},^".indexOf(expression[position])) != -1) {
			position++;
			operator = OPERATOR.values()[i];
		} else if (isLetter()) {
			for (i = position++; isFunctionSymbol(); position++)
				;
			s = new String(expression, i, position - i);

			if (s.charAt(0) == 'X' && s.length() > 1 && Character.isDigit(s.charAt(1))) {
				i = Integer.parseInt(s.substring(1));
				if (i < 0) {
					throw new Exception("index of 'x' should be nonnegative integer number");
				}
				if (arguments < i + 1) {
					arguments = i + 1;
				}
				operator = OPERATOR.X;
				tokenValue = i;
			} else {
				try {
					operator = OPERATOR.valueOf(s);
					i = operator.ordinal();
					if (i < OPERATOR.POW.ordinal() || i > OPERATOR.SQRT.ordinal()) {
						throw new IllegalArgumentException();
					}
				} catch (IllegalArgumentException _ex) {
					try {
						tokenValue = CONSTANT_VALUE[CONSTANT_NAME.valueOf(s).ordinal()];
						operator = OPERATOR.NUMBER;
					} catch (IllegalArgumentException ex) {
						throw new Exception("unknown keyword \"" + s + "\"");
					}
				}
			}
		} else if (isDigitOrPoint()) {
			if (expression[position] == '0' && position + 1 < expression.length
					&& (j = "BOX".indexOf(expression[position + 1])) != -1) {
				sh = new int[] { 1, 3, 4 }[j];
				j = 1 << sh;
				position++;
				k = -1;
				for (i = position++; isFunctionSymbol() || isPoint(); position++) {
					if (expression[position] == '_') {
						throw new Exception("invalid number");
					}
					if (isPoint()) {
						if (k != -1) {
							throw new Exception("invalid number");
						}
						k = position;
					}
				}
				s = new String(expression, i + 1, (k == -1 ? position : k) - i - 1);
				boolean f = s.isEmpty();
				tokenValue = f ? 0 : Long.parseUnsignedLong(s, j);
				if (k == -1) {
					if (f) {
						throw new Exception("invalid number");
					}
				} else {
					k++;
					s = new String(expression, k, position - k);
					if (f && s.isEmpty()) {
						throw new Exception("invalid number");
					}
					tokenValue += s.isEmpty() ? 0 : Long.parseUnsignedLong(s, j) / (double) (1l << (sh * s.length()));
				}

			} else {
				for (i = position++; isDigitOrPoint() || expression[position] == 'E'
						|| expression[position - 1] == 'E' && "+-".indexOf(expression[position]) != -1; position++)
					;

				tokenValue = Double.parseDouble(new String(expression, i, position - i));
			}
			operator = OPERATOR.NUMBER;
		} else {
			throw new Exception("unknown symbol");
		}

	}

	static final OPERATOR A[][] = { { OPERATOR.PLUS, OPERATOR.MINUS }, {}, { OPERATOR.MULTIPLY, OPERATOR.DIVIDE },
			{ OPERATOR.POW, OPERATOR.POW } };

	private Node parse() throws Exception {
		return parse(0);
	}

	private Node parse(int n) throws Exception {
		Node node;
		if (n == 1) {
			if (operator == OPERATOR.MINUS) {
				getToken();
				return new Node(OPERATOR.UNARY_MINUS, parse(n + 1));
			} else {
				if (operator == OPERATOR.PLUS) {
					getToken();
				}
				return parse(n + 1);
			}
		}
		if (n == 4) {
			return parse4();
		}
		node = parse(n + 1);
		while (Arrays.asList(A[n]).contains(operator)) {
			node = new Node(operator, node);
			getToken();
			if (Arrays.asList(A[n]).contains(operator)) {// here A[0]
				throw new Exception("two operators in a row");
			}
			node.right = parse(n + 1);
		}
		return node;
	}

	private Node parse4() throws Exception {
		Node node;
		OPERATOR open;
		int arguments;

		if (operator.ordinal() >= OPERATOR.POW.ordinal() && operator.ordinal() <= OPERATOR.SQRT.ordinal()) {
			arguments = operator.ordinal() <= OPERATOR.MAX.ordinal() ? 2 : (operator == OPERATOR.RANDOM ? 0 : 1);
			node = new Node(operator);
			getToken();
			open = operator;
			if (operator != OPERATOR.LEFT_BRACKET && operator != OPERATOR.LEFT_SQUARE_BRACKET
					&& operator != OPERATOR.LEFT_CURLY_BRACKET) {
				throw new Exception("open bracket expected");
			}
			getToken();

			if (arguments > 0) {
				node.left = parse();

				if (arguments == 2) {
					if (operator != OPERATOR.COMMA) {
						throw new Exception("comma expected");
					}
					getToken();
					node.right = parse();
				}
			}
			checkBracketBalance(open);
		} else {
			switch (operator) {

			case X:
			case NUMBER:
				node = new Node(operator, tokenValue);
				break;

			case LEFT_BRACKET:
			case LEFT_SQUARE_BRACKET:
			case LEFT_CURLY_BRACKET:
				open = operator;
				getToken();
				node = parse();
				checkBracketBalance(open);
				break;

			default:
				throw new Exception("unexpected operator");
			}

		}
		getToken();
		return node;
	}

	private void checkBracketBalance(OPERATOR open) throws Exception {
		if (operator.ordinal() != open.ordinal() + 1) {
			throw new Exception("close bracket expected or another type of close bracket");
		}
	}

	// if pass array then java splits to parameters
	public double calculate(double... x) throws Exception {
		this.argument = x;
		return calculate();
	}

	public double calculate() throws Exception {
		if (root == null) {
			throw new Exception("using of calculate() without compile()");
		}
		int length = argument == null ? 0 : argument.length;
		if (length != arguments) {
			throw new Exception("invalid number of expression arguments");
		}
		return root.calculate();
	}

	/**
	 * 
	 * @return number of expression arguments
	 */
	public int getArguments() {
		return arguments;
	}

	public ExpressionEstimator() {
	}

	public ExpressionEstimator(String e) throws Exception {
		compile(e);
	}

	public ExpressionEstimator(String e, String... s) throws Exception {
		compile(e, s);
	}

	public static double calculate(String s) throws Exception {
		ExpressionEstimator estimator = new ExpressionEstimator();
		estimator.compile(s);
		estimator.argument = null;// clear all arguments
		return estimator.calculate();
	}

	private static String bregex(String s) {
		return "\\b" + s + "\\b";
	}

	// if pass array then java splits to parameters
	public void compile(String expression, String... variables) throws Exception {
		int i;
		String s = expression.replaceAll("\\s+", "").replaceAll("\\*{2}", "^");
		String v[];
		final String r = "#";

		if (variables.length > 0) {
			v = Arrays.copyOf(variables, variables.length);
			Arrays.sort(v);
			i = 0;
			for (String a : v) {
				if (reservedWords.contains(a.toUpperCase())) {
					throw new Exception("reserved word \"" + a + "\" is used as variable");
				}
				// also check empty
				if (!Pattern.compile("^_*[A-Za-z]\\w*$").matcher(a).find()) {
					throw new Exception("invalid variable name \"" + a + "\"");
				}
				if (i > 0 && a.equals(v[i - 1])) {
					throw new Exception("repeated variable \"" + a + "\" in list");
				}
				i++;
			}

			if (s.indexOf(r) != -1) {
				throw new Exception(r + " found in string");
			}

			i = 0;
			for (String e : variables) {
				s = s.replaceAll(bregex(e), r + i);
				i++;
			}

			Matcher matcher = Pattern.compile("[xX]\\d*").matcher(s);
			if (matcher.find()) {
				throw new Exception("unknown variable \"" + matcher.group() + "\"");
			}

			s = s.replaceAll(r, "X");
		}

		position = 0;
		arguments = variables.length;
		s = s.toUpperCase();// for OPERATOR.valueof()
		this.expression = (s + '\0').getBytes();

		getToken();
		if (operator == OPERATOR.END) {
			throw new Exception("unexpected end of expression");
		}
		root = parse();
		if (operator != OPERATOR.END) {
			throw new Exception("end of expression expected");
		}
	}

	static List<String> reservedWords = new ArrayList<String>();
	static {
		for (int i = OPERATOR.POW.ordinal(); i <= OPERATOR.SQRT.ordinal(); i++) {
			reservedWords.add(OPERATOR.values()[i].toString());
		}

		for (CONSTANT_NAME s : CONSTANT_NAME.values()) {
			reservedWords.add(s.toString());
		}
	}
};
