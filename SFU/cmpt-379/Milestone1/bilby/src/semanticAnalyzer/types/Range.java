package semanticAnalyzer.types;

import java.util.Collection;

public class Range implements Type {

	private static final int REFERENCE_TYPE_SIZE = 4;
	Type subtype;

	//@Override
	
	public Range(Type SubType) {
		this.subtype = SubType;
	}
	public int getSize() {
		return REFERENCE_TYPE_SIZE;
	}
	
	public Type getSubType() {
		return subtype;
	}

	@Override
	public String infoString() {
		return "<" + getSubType() + ">";
	}
	@Override
	public boolean equivalent(Type otherType) {
		if(otherType instanceof Range) {
			Range otherArray = (Range)otherType;
			return subtype.equivalent(otherArray.getSubType());
		}
		return false;
	}

	public Collection <TypeVariable> getTypeVariables(){
		return subtype.getTypeVariables();
	}
	@Override
	public Type concreteType() {
		return new Range(subtype.concreteType());
	}
	public Type atomicType() {
		return subtype.concreteType();
	}
	@Override
	public boolean isArray() {
		return false;
	}
	public boolean isRange() {
		return true;
	}
	
}
