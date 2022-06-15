package tokens;

import inputHandler.LocatedChar;
import inputHandler.Locator;
import logging.BilbyLogger;

public class FloatingConstantToken extends TokenImp {
	protected double value;
	
	protected FloatingConstantToken(Locator locator, String lexeme) {
		super(locator, lexeme);
	}
	protected void setValue(double value) {
		if (value == Double.POSITIVE_INFINITY || value == Double.NEGATIVE_INFINITY) {
			NumberFormatException();
			this.value = Double.POSITIVE_INFINITY;
		}
		else {
			this.value = value;
		}
	}
	public double getValue() {
		return value;
	}
	
	public static FloatingConstantToken make(Locator locator, String lexeme) {
		FloatingConstantToken result = new FloatingConstantToken(locator, lexeme);
		if (lexeme.contains(Character.toString('E'))) {
			String[] parts = lexeme.split("E");
			String mantissa = parts[0];
			String exponand = parts[1];
			if (exponand.contains(Character.toString('-'))){
				exponand = exponand.substring(1);
				result.setValue(Double.parseDouble(mantissa) *  Math.pow(10, -(Double.parseDouble(exponand))));
			}
			else {
				result.setValue(Double.parseDouble(mantissa) *  Math.pow(10, (Double.parseDouble(exponand))));
			}
			return result;
		}
		else {
			result.setValue(Double.parseDouble(lexeme));
			return result;
		}
	}
	
	@Override
	protected String rawString() {
		return "FloatingConstant, " + value;
	}
	
	private void NumberFormatException() {
		BilbyLogger log = BilbyLogger.getLogger("compiler.Runtime");
		log.severe("Runtime error: Floating value uncomputable");
		
	}
}
