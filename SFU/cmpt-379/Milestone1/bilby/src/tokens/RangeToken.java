package tokens;

import inputHandler.Locator;

public class RangeToken extends TokenImp {
	Token bottom;
	Token top;
	protected RangeToken(Locator locator, String lexeme) {
		super(locator, lexeme.intern());
	}
	
	public static RangeToken make(Locator locator, String lexeme) {
		RangeToken result = new RangeToken(locator, lexeme);
		return result;
	}

	@Override
	protected String rawString() {
		return "identifier, " + getLexeme();
	}
}
