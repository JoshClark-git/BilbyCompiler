package parseTree.nodeTypes;

import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import semanticAnalyzer.signatures.FunctionSignature;
import semanticAnalyzer.signatures.PromotionSignature;

import java.util.List;

import lexicalAnalyzer.Lextant;
import tokens.LextantToken;
import tokens.Token;

public class OperatorNode extends ParseNode {
	private PromotionSignature promotionSignature;

	public OperatorNode(Token token) {
		super(token);
		assert(token instanceof LextantToken);
	}

	public OperatorNode(ParseNode node) {
		super(node);
	}
	
	
	////////////////////////////////////////////////////////////
	// attributes
	
	public Lextant getOperator() {
		return lextantToken().getLextant();
	}
	public LextantToken lextantToken() {
		return (LextantToken)token;
	}
	
	public PromotionSignature getPromotionSignature() {
		return promotionSignature;
	}

	public void setPromotionSignature(PromotionSignature promotionSignature) {
		this.promotionSignature = promotionSignature;
	}



	
	////////////////////////////////////////////////////////////
	// convenience factory

	public static ParseNode withChildren(Token token, ParseNode ...children) {
		OperatorNode node = new OperatorNode(token);
		for(ParseNode child: children) {
			node.appendChild(child);
		}
		return node;
	}
	public static ParseNode withArrayChildren(Token token, List<ParseNode> nodes) {   
		OperatorNode node = new OperatorNode(token);
		for(ParseNode child: nodes) {
			node.appendChild(child);
		}
		return node;
	}
	
	///////////////////////////////////////////////////////////
	// boilerplate for visitors
			
	public void accept(ParseNodeVisitor visitor) {
		visitor.visitEnter(this);
		visitChildren(visitor);
		visitor.visit(this);
		visitor.visitLeave(this);
	}

	public static ParseNode withChildren(Token listToken, List<ParseNode> nodes) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}

