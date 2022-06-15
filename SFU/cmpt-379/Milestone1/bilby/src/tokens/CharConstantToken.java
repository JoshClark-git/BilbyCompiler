package tokens;

import inputHandler.Locator;

public class CharConstantToken extends TokenImp {
	protected Character value;
	
	protected CharConstantToken(Locator locator, String lexeme) {
		super(locator, lexeme);
	}
	protected void setValue(Character value) {
		this.value = value;
	}
	public Character getValue() {
		return value;
	}
	
	public static CharConstantToken make(Locator locator, String lexeme) {
		CharConstantToken result = new CharConstantToken(locator, lexeme);
		result.setValue(lexeme.charAt(0));
		return result;
	}
	
	@Override
	protected String rawString() {
		return "CharConstant, " + value;
	}
}
