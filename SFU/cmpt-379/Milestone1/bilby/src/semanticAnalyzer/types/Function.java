package semanticAnalyzer.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import parseTree.ParseNode;
import parseTree.nodeTypes.ParameterNode;

public class Function implements Type {

	private static final int REFERENCE_TYPE_SIZE = 4;
	Type subtype;
	List<ParseNode> paramNodes;

	//@Override
	
	public Function(List<ParseNode> nodes) {
		this.paramNodes = nodes;
	}
	public int getSize() {
		return REFERENCE_TYPE_SIZE;
	}
	
	public Type getSubType() {
		return subtype;
	}
	public List<ParseNode> getParamNodes() {
		return paramNodes;
	}
	public List<Type> getParamTypes() {
		ArrayList<Type> returnList = new ArrayList<Type>();
		for (ParseNode paramNode : getParamNodes()) {
			if(paramNode instanceof ParameterNode) {
				returnList.add(paramNode.child(0).getType());
			}
			else {
				returnList.add(paramNode.getType());
			}
		}
		return returnList;
	}

	@Override
	public String infoString() {
		String info = "(";
		List<Type> types = getParamTypes();
		int i = 0;
		while(i < (types.size() - 1)) {
			info = info + types.get(i).infoString() + ",";
			i = i + 1;
		}
		info = info.substring(0, info.length() - 1);
		info = info + ")";
		info = info + " -> ";
		info = info + types.get(types.size() -1 ).infoString();
		return info;
	}
	@Override
	public boolean equivalent(Type otherType) {
		if(otherType instanceof Function) {
			Function otherArray = (Function)otherType;
			return subtype.equivalent(otherArray.getSubType());
		}
		return false;
	}

	public Collection <TypeVariable> getTypeVariables(){
		return subtype.getTypeVariables();
	}
	@Override
	public Type atomicType() {
		//return new Array(subtype.concreteType());
		return subtype.concreteType();
	}
	@Override
	public boolean isArray() {
		return false;
	}
	@Override
	public boolean isRange() {
		return false;
	}
	@Override
	public Type concreteType() {
		return null;
	}
}
