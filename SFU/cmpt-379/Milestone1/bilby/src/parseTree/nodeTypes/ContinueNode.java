package parseTree.nodeTypes;

import lexicalAnalyzer.Keyword;
import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import tokens.CharConstantToken;
import tokens.Token;

public class ContinueNode extends ParseNode {
	public ContinueNode(Token token) {
		super(token);
		assert(token.isLextant(Keyword.CONTINUE));
	}
	public ContinueNode(ParseNode node) {
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
