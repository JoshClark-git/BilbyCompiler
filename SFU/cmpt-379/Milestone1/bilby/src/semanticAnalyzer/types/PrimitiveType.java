package semanticAnalyzer.types;

import java.util.ArrayList;
import java.util.Collection;

public enum PrimitiveType implements Type {
	BOOLEAN(1),
	CHARACTER(1),
	INTEGER(4),
	STRING(4),
	FLOATING(8),
	VOID(0),
	ERROR(0),			// use as a value when a syntax error has occurred
	NO_TYPE(0, "");		// use as a value when no type has been assigned.
	
	private int sizeInBytes;
	private String infoString;
	
	private PrimitiveType(int size) {
		this.sizeInBytes = size;
		this.infoString = toString();
	}
	private PrimitiveType(int size, String infoString) {
		this.sizeInBytes = size;
		this.infoString = infoString;
	}
	public int getSize() {
		return sizeInBytes;
	}
	public String infoString() {
		return infoString;
	}
	public boolean equivalent(Type otherType) {
		return this.equals(otherType);
	}
	@Override
	public Collection<TypeVariable> getTypeVariables() {
		return new ArrayList<TypeVariable>();
	}
	@Override
	public Type concreteType() {
		return this;
	}
	@Override
	public Type atomicType() {
		return this;
	}
	@Override
	public boolean isArray() {
		return false;
	}
	@Override
	public boolean isRange() {
		return false;
	}
}
