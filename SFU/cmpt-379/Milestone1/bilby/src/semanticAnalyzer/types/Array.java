package semanticAnalyzer.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Array implements Type {

	private static final int REFERENCE_TYPE_SIZE = 4;
	Type subtype;

	//@Override
	
	public Array(Type SubType) {
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
		return "[" + getSubType() + "]";
	}
	@Override
	public boolean equivalent(Type otherType) {
		if(otherType instanceof Array) {
			Array otherArray = (Array)otherType;
			return subtype.equivalent(otherArray.getSubType());
		}
		return false;
	}

	public Collection <TypeVariable> getTypeVariables(){
		return subtype.getTypeVariables();
	}
	@Override
	public Type concreteType() {
		return new Array(subtype.concreteType());
	}
	public Type atomicType() {
		//return new Array(subtype.concreteType());
		return subtype.concreteType();
	}
	@Override
	public boolean isArray() {
		return true;
	}
	@Override
	public boolean isRange() {
		return false;
	}
}
