package semanticAnalyzer.types;

import java.util.Collection;
import java.util.List;

public interface Type {
	/** returns the size of an instance of this type, in bytes.
	 * 
	 * @return number of bytes per instance
	 */
	public int getSize(); 
	
	/** Yields a printable string for information about this type.
	 * use this rather than toString() if you want an abbreviated string.
	 * In particular, this yields an empty string for PrimitiveType.NO_TYPE.
	 * 
	 * @return string representation of type.
	 */
	public String infoString();

	public boolean equivalent(Type otherType);

	public Collection<TypeVariable> getTypeVariables();

	public Type concreteType();
	
	public Type atomicType();
	
	public boolean isArray();
	
	public boolean isRange();
}
