package tokens;

import inputHandler.Locator;
import logging.BilbyLogger;

public class IntegerConstantToken extends TokenImp {
	protected int value;
	
	protected IntegerConstantToken(Locator locator, String lexeme) {
		super(locator, lexeme);
	}
	protected void setValue(int value) {
		this.value = value;
	}
	public int getValue() {
		return value;
	}
	
	public static IntegerConstantToken make(Locator locator, String lexeme) {
		IntegerConstantToken result = new IntegerConstantToken(locator, lexeme);
		try {
			int value = Integer.parseInt(lexeme);
			result.setValue(value);
			return result;
		} catch (RuntimeException ex) {
				NumberFormatException();
				result.setValue(Integer.MAX_VALUE);
		}
		return result;
	}
	@Override
	protected String rawString() {
		return "IntegerConstant, " + value;
	}
	private static void NumberFormatException() {
		BilbyLogger log = BilbyLogger.getLogger("compiler.Runtime");
		log.severe("Runtime error: Integer value too large");
		
	}
}
