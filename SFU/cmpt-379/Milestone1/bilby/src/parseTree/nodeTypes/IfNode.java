package parseTree.nodeTypes;

import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import semanticAnalyzer.signatures.FunctionSignature;
import tokens.Token;

public class IfNode extends ParseNode {
	private FunctionSignature signature;

	public IfNode(Token token) {
		super(token);
	}
	public IfNode(ParseNode node) {
		super(node);
	}
	
	////////////////////////////////////////////////////////////
	// no attributes

	
	///////////////////////////////////////////////////////////
	// boilerplate for visitors
	
	public void accept(ParseNodeVisitor visitor) {
		visitor.visitEnter(this);
		visitChildren(visitor);
		visitor.visitLeave(this);
	}
	public void setSignature(FunctionSignature signature) {
		this.signature = signature;
		
	}
	public FunctionSignature getSignature() {
		return signature;
	}
}
