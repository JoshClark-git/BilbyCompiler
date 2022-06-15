package lexicalAnalyzer;


import logging.BilbyLogger;

import inputHandler.InputHandler;
import inputHandler.LocatedChar;
import inputHandler.LocatedCharStream;
import inputHandler.PushbackCharStream;
import tokens.CharConstantToken;
import tokens.FloatingConstantToken;
import tokens.IdentifierToken;
import tokens.LextantToken;
import tokens.NullToken;
import tokens.StringConstantToken;
import tokens.IntegerConstantToken;
import tokens.Token;

import static lexicalAnalyzer.PunctuatorScanningAids.*;

public class LexicalAnalyzer extends ScannerImp implements Scanner {
	private static final char DECIMAL_POINT = '.';
	
	public static void wait(int ms)
	{
	    try
	    {
	        Thread.sleep(ms);
	    }
	    catch(InterruptedException ex)
	    {
	        Thread.currentThread().interrupt();
	    }
	}

	public static LexicalAnalyzer make(String filename) {
		InputHandler handler = InputHandler.fromFilename(filename);
		PushbackCharStream charStream = PushbackCharStream.make(handler);
		return new LexicalAnalyzer(charStream);
	}

	public LexicalAnalyzer(PushbackCharStream input) {
		super(input);
	}

	
	//////////////////////////////////////////////////////////////////////////////
	// Token-finding main dispatch	

	@Override
	protected Token findNextToken() {
		LocatedChar ch = nextNonWhitespaceChar();
		if(ch.isDigit()) {
			return scanNumber(ch);
		}
		else if(isHashSymbol(ch)) {
			return scanChar(ch);
		}
		else if(isComment(ch)) {
			return scanComment(ch);
		}
		else if(isIDStart(ch)) {
			return scanIdentifier(ch);
		}
		else if(isPunctuatorStart(ch)) {
			return PunctuatorScanner.scan(ch, input);
		}
		else if(isStringStart(ch)) {
			return scanString(ch);
		}
		else if(isEndOfInput(ch)) {
			return NullToken.make(ch);
		}
		else {
			lexicalError(ch);
			return findNextToken();
		}
	}


	private LocatedChar nextNonWhitespaceChar() {
		LocatedChar ch = input.next();
		while(ch.isWhitespace()) {
			ch = input.next();
		}
		return ch;
	}
	
	//////////////////////////////////////////////////////////////////////////////
	// Integer and Floating lexical analysis	

	private Token scanNumber(LocatedChar firstChar) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(firstChar.getCharacter());
		appendSubsequentDigits(buffer);
		//Check if number is float/range
		if(input.peek().getCharacter() == DECIMAL_POINT) {
			LocatedChar decimal_point = input.next();
			buffer.append(decimal_point.getCharacter());
			//check is val after decimal is another digit (float) or another decimal (range)
			if(input.peek().isDigit()) {
				appendSubsequentDigits(buffer);
				//Check if float has exponent val
				if(input.peek().getCharacter() == 'E') {
					LocatedChar exponent = input.next();
					buffer.append(exponent.getCharacter());
					if(input.peek().isDigit() || input.peek().getCharacter() == '-' || input.peek().getCharacter() == '+') {
						LocatedChar sign = input.next();
						buffer.append(sign.getCharacter());
						appendSubsequentDigits(buffer);
						return FloatingConstantToken.make(firstChar,buffer.toString());
					}
					else {
						lexicalError(firstChar,"malformed floating literal");
						return findNextToken();
					}
				}
				return FloatingConstantToken.make(firstChar,buffer.toString());
			}
			//This input is part of range of INTs
			else if(input.peek().getCharacter() == DECIMAL_POINT){
				buffer.deleteCharAt(buffer.indexOf("."));
				return IntegerConstantToken.make(firstChar, buffer.toString());
			}
			else {
				lexicalError(firstChar,"malformed floating literal");
				return findNextToken();
			}
		}
		
		return IntegerConstantToken.make(firstChar, buffer.toString());
	}


	private void appendSubsequentDigits(StringBuffer buffer) {
		LocatedChar c = input.next();
		while(c.isDigit()) {
			buffer.append(c.getCharacter());
			c = input.next();
		}
		input.pushback(c);
	}
	
	
	//////////////////////////////////////////////////////////////////////////////
	// Identifier and keyword lexical analysis	
	
	//characterLiteral -> #a |## [ 0..9# ] |#[ 0..7 ]
			// a is any printable ascii character (encoding is decimal 32 to 126)

	private Token scanChar(LocatedChar firstChar) {
		LocatedChar c = input.next();
		StringBuffer buffer = new StringBuffer();
		if(isHashSymbol(c)) {
			return doubleHash(c,firstChar);
		}
		//Char as base 8 ASCII
		while(0 <= Character.getNumericValue(c.getCharacter()) && Character.getNumericValue(c.getCharacter())  <= 7) {
			buffer.append(c.getCharacter());
			c = input.next();
		}
		input.pushback(c);
		//Char is printable ascii
		if (!buffer.isEmpty()) {
			return charPrintableASCII(buffer, c, firstChar);
		}
		buffer.append(input.next().getCharacter());
		String lexeme = buffer.toString();
		return CharConstantToken.make(firstChar, lexeme);
	}
	
	// Double Hash syntax -  ## [0..9#]
	private Token doubleHash(LocatedChar c, LocatedChar firstChar) {
		c = input.next();
		if(!(input.peek().isDigit()) && (c.isDigit() || c.getCharacter() == '#')) {
			//System.out.println(input.peek().getCharacter());
			String str = Character.toString(c.getCharacter());
			return CharConstantToken.make(firstChar, str);
		}
		else {
			input.pushback(c);
			lexicalError(firstChar,"Char must be a single digit or # ");
			return findNextToken();
			
		}
	}
	// Printable ASCII - #a where a is ascii char between 32 - 126 in decimal
	private Token charPrintableASCII(StringBuffer buffer, LocatedChar c, LocatedChar firstChar) {
		String lexeme = buffer.toString();
		if (!(Character.getNumericValue(input.peek().getCharacter()) < 8)) {
			lexicalError(firstChar,"ASCII Code is not in Octal ");
			return findNextToken();
		}
		else if(lexeme.length() > 3) {
			lexicalError(firstChar,"ASCII Code has > 3 digits ");
			return findNextToken();
		}
		else {
			int ASCIIint = Integer.parseInt(lexeme,8);
			if ((ASCIIint > 127)) {
				lexicalError(firstChar,"ASCII Code value does not map to ASCII (too large) ");
				return findNextToken();
			}
			char ch = (char) ASCIIint;
			String str = Character.toString(ch);
			return CharConstantToken.make(firstChar, str);
		}
	}
	
	private Token scanComment(LocatedChar firstChar) {
		int lineNumber = firstChar.getLocation().getLineNumber();
		while (input.peek().getCharacter() != '%' && input.peek().getLocation().getLineNumber() == lineNumber) {
			input.next();
		}
		return findNextToken();
		
	}

	private Token scanIdentifier(LocatedChar firstChar) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(firstChar.getCharacter());
		appendSubsequentChars(buffer);

		String lexeme = buffer.toString();
		if(Keyword.isAKeyword(lexeme)) {
			return LextantToken.make(firstChar, lexeme, Keyword.forLexeme(lexeme));
		}
		else {
			return IdentifierToken.make(firstChar, lexeme);
		}
	}
	private void appendSubsequentChars(StringBuffer buffer) {
		LocatedChar c = input.next();
		while(isIDStart(c) || c.isDigit()) {
			buffer.append(c.getCharacter());
			c = input.next();
		}
		input.pushback(c);
	}
	
	private Token scanString(LocatedChar firstChar) {
		int lineNumber = firstChar.getLocation().getLineNumber();
		LocatedChar c = input.next();
		StringBuffer buffer = new StringBuffer();
		while (c.getCharacter() != '"' && c.getLocation().getLineNumber() == lineNumber) {
			buffer.append(c.getCharacter());
			c = input.next();
		}
		if (c.getLocation().getLineNumber() != lineNumber) {
			lexicalError(firstChar,"String must end with end quote  ");
			return findNextToken();
		}
		String str = buffer.toString();
		return StringConstantToken.make(firstChar, str);
	}
	
	/*
	//////////////////////////////////////////////////////////////////////////////
	// Punctuator lexical analysis	
	// old method left in to show a simple scanning method.
	// current method is the algorithm object PunctuatorScanner.java

	@SuppressWarnings("unused")
	private Token oldScanPunctuator(LocatedChar ch) {
		
		switch(ch.getCharacter()) {
		case '*':
			return LextantToken.make(ch, "*", Punctuator.MULTIPLY);
		case '+':
			return LextantToken.make(ch, "+", Punctuator.ADD);
		case '>':
			return LextantToken.make(ch, ">", Punctuator.GREATER);
		case ':':
			if(ch.getCharacter()=='=') {
				return LextantToken.make(ch, ":=", Punctuator.ASSIGN);
			}
			else {
				lexicalError(ch);
				return(NullToken.make(ch));
			}
		case ',':
			return LextantToken.make(ch, ",", Punctuator.PRINT_SEPARATOR);
		case ';':
			return LextantToken.make(ch, ";", Punctuator.TERMINATOR);
		default:
			lexicalError(ch);
			return(NullToken.make(ch));
		}
	}

	*/

	//////////////////////////////////////////////////////////////////////////////
	// Character-classification routines specific to bilby scanning.	

	private boolean isPunctuatorStart(LocatedChar lc) {
		char c = lc.getCharacter();
		//System.out.println(isPunctuatorStartingCharacter(c));
		return isPunctuatorStartingCharacter(c);
	}

	private boolean isEndOfInput(LocatedChar lc) {
		return lc == LocatedCharStream.FLAG_END_OF_INPUT;
	}
	
	private boolean isHashSymbol(LocatedChar lc ) {
		//System.out.println(lc.getCharacter());
		return lc.getCharacter() == '#';
	}
	
	private boolean isIDStart(LocatedChar lc ) {
		return (lc.getCharacter() >= 'a' && lc.getCharacter() <= 'z') ||
		           (lc.getCharacter() >= 'A' && lc.getCharacter() <= 'Z') ||
		           (lc.getCharacter() == '_' ||  lc.getCharacter() == '@');
	}
	
	private boolean isComment(LocatedChar lc) {
		return lc.getCharacter() == '%';
	}
	private boolean isStringStart(LocatedChar lc) {
		return lc.getCharacter() == '"';
	}
	
	//////////////////////////////////////////////////////////////////////////////
	// Error-reporting	

	private void lexicalError(LocatedChar ch) {
		BilbyLogger log = BilbyLogger.getLogger("compiler.lexicalAnalyzer");
		log.severe("Lexical error: invalid character " + ch);
	}
	
	private void lexicalError(LocatedChar ch, String message) {
		BilbyLogger log = BilbyLogger.getLogger("compiler.lexicalAnalyzer");
		log.severe("Lexical error: " + message + "at " + ch.getLocation());
		
	}
	
}

