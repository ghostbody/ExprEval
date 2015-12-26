package parser.analyzer;

import parser.Lexer.Boolean;
import parser.Lexer.Bracket;
import parser.Lexer.Comma;
import parser.Lexer.Decimal;
import parser.Lexer.Dolla;
import parser.Lexer.Logic;
import parser.Lexer.Operator;
import parser.Lexer.Token;

public class OOPTable {
    public static final int S0 = 0; // shift
    public static final int R1 = 1; // reduce bracket ()
    public static final int R2 = 2; // reduce 1 operator
    public static final int R3 = 3; // reduce 2 operator
    public static final int R4 = 4; // reduce 3 operator
    public static final int A0 = 5; // accept state

    public static final int E1 = -1; // missing left bracket (
    public static final int E2 = -2; // missing right bracket )
    public static final int E3 = -3; // missing oprand
    public static final int E4 = -4; // 3 operator error
    public static final int E5 = -5; // type error
    public static final int E6 = -6; // function error
    public static final int E7 = -7; // syntax error
    public static final int E8 = -8; // unknown error

    public static final int SHIFT = 0;
    public static final int RBRACKET = 1;
    public static final int RUNARY = 2;
    public static final int RBOPER = 3;
    public static final int RTOPER = 4;
    public static final int ACCEPT = 5;
    public static final int ELBRACKET = -1;
    public static final int ERBRACKET = -2;
    public static final int EMOPRAND = -3;
    public static final int ETOPER = -4;
    public static final int ETYPE = -5;
    public static final int EFUNC = -6;
    public static final int ESYNT = -7;
    public static final int EUNK = -8;

    public static final int LeftBracket = 0;
    public static final int RightBracket = 1;
    public static final int func = 2;
    public static final int minus = 3;
    public static final int power = 4;
    public static final int muldiv = 5;
    public static final int plumin = 6;
    public static final int cmp = 7;
    public static final int not = 8;
    public static final int and = 9;
    public static final int or = 10;
    public static final int interrogation = 11;
    public static final int colon = 12;
    public static final int comma = 13;
    public static final int dollar = 14;
    
    public static final int decimal = 15;
    public static final int bool = 16;
    
    public static final int unknown = 17;
    
    public static int getType(Token t) {
		if(t instanceof Bracket) {
			if(((Bracket) t).value == '(') {
				return LeftBracket;
			} else {
				return RightBracket;
			}
		} else if (t instanceof parser.Lexer.Function) {
			return func;
		} else if (t instanceof Operator) { 
			switch (((Operator) t).value) {
			case '+': return plumin;
			case '-':
				if(t.operNum == 1) {
					return minus;
				} else {
					return plumin;
				}
			case '*': case '/':
				return muldiv;
			case '^':
				return power;
			case '?':
				return interrogation;
			case ':':
				return colon;
			default:
				break;
			}
		} else if (t instanceof Logic) {
			if(((Logic) t).value == ">" || ((Logic) t).value  == "<"
					|| ((Logic) t).value  == "=" || ((Logic) t).value == ">="
					|| ((Logic) t).value  == "<=" || ((Logic) t).value  == "<>") {
				return cmp;
			} else if (((Logic) t).value == "!") {
				return not;
			}  else if(((Logic) t).value == "&") {
				return and;
			} else if(((Logic) t).value == "|") {
				return or;
			}
			//
		} else if (t instanceof Decimal) {
			return decimal;
		} else if (t instanceof Boolean) {
			return bool;
		} else if (t instanceof Comma) {
			return comma;
		} else if (t instanceof Dolla) {
			return dollar;
		} 
		return unknown;
	}

    public static final int table[][] = {
		         /*(   )  func -   ^  md   pm cmp  !   &   |   ?   :   ,   $ */
		/*(*/    {S0, R1, S0, S0, S0, S0, S0, S0, S0, S0, S0, S0, E4, S0, E2},
		/*)*/    {R1, R1, R1, R1, R1, R1, R1, R1, R1, R1, R1, R1, R1, R1, R1},
		/*func*/ {S0, E6, E6, E6, E6, E6, E6, E6, E6, E6, E6, E6, E6, E6, E6},
		/*-*/    {S0, R2, S0, S0, S0, R2, R2, R2, E7, E5, E5, R2, R2, R2, R2},
		/*^*/    {S0, R1, S0, S0, S0, R3, R3, R3, E7, E5, E5, R3, R3, R3, R3},
		/*md*/   {S0, R1, S0, S0, S0, R3, R3, R3, E7, E5, E5, R3, R3, R3, R3},
		/*pm*/   {S0, R1, S0, S0, S0, S0, R3, R3, E7, E5, E5, R3, R3, R3, R3},
		/*cmp*/  {S0, R1, S0, S0, S0, S0, S0, E5, E7, R3, R3, R3, E4, E6, R3},
		/*!*/    {S0, R1, E5, E5, E5, E5, E5, S0, S0, R2, R2, R2, E4, E6, R2},
		/*&*/    {S0, R1, E5, E5, E5, E5, E5, S0, S0, R3, R3, R3, E4, E6, R3},
		/*|*/    {S0, R1, E5, E5, E5, E5, E5, S0, S0, S0, R3, R3, E4, E6, R3},
		/*?*/    {S0, E4, S0, S0, S0, S0, S0, S0, S0, S0, S0, S0, S0, E4, E4},
		/*:*/    {S0, R1, S0, S0, S0, S0, S0, E4, E4, E4, E4, S0, E4, E4, R4},
		/*,*/    {S0, R1, S0, S0, S0, S0, S0, E6, E6, E6, E6, S0, E4, S0, E6},
		/*$*/    {S0, E1, S0, S0, S0, S0, S0, S0, S0, S0, S0, S0, E4, E6, A0}
	};
}
