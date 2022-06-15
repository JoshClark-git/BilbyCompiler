package tokens;

import inputHandler.Locator;

public class BooleanConstantToken extends TokenImp {
	protected Character value;
	
	protected BooleanConstantToken(Locator locator, String lexeme) {
		super(locator, lexeme);
	}
	protected void setValue(Character value) {
		this.value = value;
	}
	public Character getValue() {
		return value;
	}
	
	public static BooleanConstantToken make(Locator locator, String lexeme) {
		BooleanConstantToken result = new BooleanConstantToken(locator, lexeme);
		result.setValue(lexeme.charAt(0));
		return result;
	}
	
	@Override
	protected String rawString() {
		return "CharConstant, " + value;
	}
}
