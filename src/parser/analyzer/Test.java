package parser.analyzer;

import parser.Calculator;

public class Test {
	public static void main(String[] args) throws Exception {
		Calculator c = new Calculator();
		double x = c.calculate("1 + 2");
		System.out.println(x);
	}
}
