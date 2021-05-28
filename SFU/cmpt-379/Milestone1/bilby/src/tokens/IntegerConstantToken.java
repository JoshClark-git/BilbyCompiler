package tokens;

import inputHandler.Locator;

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
		result.setValue(Integer.parseInt(lexeme));
		return result;
	}
	
	@Override
	protected String rawString() {
		return "IntegerConstant, " + value;
	}
}
