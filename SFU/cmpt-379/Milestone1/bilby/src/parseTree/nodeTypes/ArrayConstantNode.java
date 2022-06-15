package parseTree.nodeTypes;

import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import tokens.CharConstantToken;
import tokens.Token;

public class ArrayConstantNode extends ParseNode {
	public ArrayConstantNode(Token token) {
		super(token);
	}
	public ArrayConstantNode(ParseNode node) {
		super(node);
	}

////////////////////////////////////////////////////////////
// attributes
	
	public Character getValue() {
		return charToken().getValue();
	}

	public CharConstantToken charToken() {
		return (CharConstantToken)token;
	}	

///////////////////////////////////////////////////////////
// accept a visitor
	
	public void accept(ParseNodeVisitor visitor) {
		visitor.visit(this);
	}

}
