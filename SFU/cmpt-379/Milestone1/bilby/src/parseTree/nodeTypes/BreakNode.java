package parseTree.nodeTypes;

import lexicalAnalyzer.Keyword;
import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import tokens.CharConstantToken;
import tokens.Token;

public class BreakNode extends ParseNode {
	public BreakNode(Token token) {
		super(token);
		assert(token.isLextant(Keyword.BREAK));
	}
	public BreakNode(ParseNode node) {
		super(node);
	}

////////////////////////////////////////////////////////////
// attributes

///////////////////////////////////////////////////////////
// accept a visitor
	
	public void accept(ParseNodeVisitor visitor) {
		visitor.visit(this);
	}

}
