package parseTree.nodeTypes;

import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import tokens.FloatingConstantToken;
import tokens.Token;

public class FloatingConstantNode extends ParseNode {
	public FloatingConstantNode(Token token) {
		super(token);
		assert(token instanceof FloatingConstantToken);
	}
	public FloatingConstantNode(ParseNode node) {
		super(node);
	}

////////////////////////////////////////////////////////////
// attributes
	
	public double getValue() {
		return floatingToken().getValue();
	}

	public FloatingConstantToken floatingToken() {
		return (FloatingConstantToken)token;
	}	

///////////////////////////////////////////////////////////
// accept a visitor
	
	public void accept(ParseNodeVisitor visitor) {
		visitor.visit(this);
	}

}
