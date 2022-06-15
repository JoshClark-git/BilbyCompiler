package lexicalAnalyzer;

import inputHandler.TextLocation;
import tokens.LextantToken;
import tokens.Token;


public enum Keyword implements Lextant {
	IMM("imm"),
	MUT("mut"),
	PRINT("print"),
	TRUE("true"),
	FALSE("false"),
	MAIN("main"),
	BOOL("bool"),
	CHAR("char"),
	CAST("as"),
	STRING("string"),
	INT("int"),
	FLOAT("float"),
	VOID("void"),
	ALLOC("alloc"),
	IF("if"),
	ELSE("else"),
	WHILE("while"),
	FOR("for"),
	NULL_KEYWORD(""),
	LENGTH("length"),
	LOW("low"),
	HIGH("high"),
	IN("in"),
	RANGE("range"),
	ARRAY("array"),
	BREAK("break"),
	CONTINUE("continue"),
	FUNC("func"),
	RETURN("return"),
	CALL("call"),
	IFELSE("");

	private String lexeme;
	private Token prototype;
	
	
	private Keyword(String lexeme) {
		this.lexeme = lexeme;
		this.prototype = LextantToken.make(TextLocation.nullInstance(), lexeme, this);
	}
	public String getLexeme() {
		return lexeme;
	}
	public Token prototype() {
		return prototype;
	}
	
	public static Keyword forLexeme(String lexeme) {
		for(Keyword keyword: values()) {
			if(keyword.lexeme.equals(lexeme)) {
				return keyword;
			}
		}
		return NULL_KEYWORD;
	}
	public static boolean isAKeyword(String lexeme) {
		return forLexeme(lexeme) != NULL_KEYWORD;
	}
}
