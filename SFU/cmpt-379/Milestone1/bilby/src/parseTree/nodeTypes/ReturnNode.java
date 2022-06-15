package parseTree.nodeTypes;

import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import lexicalAnalyzer.Keyword;
import lexicalAnalyzer.Lextant;
import tokens.LextantToken;
import tokens.Token;

public class ReturnNode extends ParseNode {

	public ReturnNode(Token token) {
		super(token);
		//assert(token.isLextant(Keyword.IMM, Keyword.MUT));
	}

	public ReturnNode(ParseNode node) {
		super(node);
	}
	
	
	////////////////////////////////////////////////////////////
	// attributes
	
	public Lextant getDeclarationType() {
		return lextantToken().getLextant();
	}
	public LextantToken lextantToken() {
		return (LextantToken)token;
	}	
	
	
	////////////////////////////////////////////////////////////
	// convenience factory
	
	public static ReturnNode withChildren(Token token, ParseNode returnNode) {
		ReturnNode node = new ReturnNode(token);
		node.appendChild(returnNode);
		return node;
	}
	public static ReturnNode withChildren(Token token) {
		ReturnNode node = new ReturnNode(token);
		return node;
	}
	
	
	///////////////////////////////////////////////////////////
	// boilerplate for visitors
			
	public void accept(ParseNodeVisitor visitor) {
		visitor.visitEnter(this);
		visitChildren(visitor);
		visitor.visitLeave(this);
	}
}
