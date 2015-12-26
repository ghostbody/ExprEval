package parser.Lexer;

import java.util.ArrayList;

public class Main {
	public static void main(String[] args) {
		String str = "max(1,2)";
		Lexer x = new Lexer(str);
		x.scanner();
		ArrayList<Token> res = x.getAllTokens();
		for(Token t : res) {
			System.out.println(t.toString());
		}
	}
}
