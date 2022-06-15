package tokens;

import inputHandler.Locator;

public class StringConstantToken extends TokenImp {
	protected String value;
	
	protected StringConstantToken(Locator locator, String lexeme) {
		super(locator, lexeme);
	}
	protected void setValue(String value) {
		this.value = value;
	}
	public String getValue() {
		return value;
	}
	
	public static StringConstantToken make(Locator locator, String lexeme) {
		StringConstantToken result = new StringConstantToken(locator, lexeme);
		result.setValue(lexeme.toString());
		return result;
	}
	
	@Override
	protected String rawString() {
		return "StringConstant, " + value;
	}
}
