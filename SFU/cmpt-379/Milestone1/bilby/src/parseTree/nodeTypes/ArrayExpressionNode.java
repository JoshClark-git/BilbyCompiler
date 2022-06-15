package parseTree.nodeTypes;

import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import semanticAnalyzer.signatures.FunctionSignature;
import semanticAnalyzer.signatures.PromotionSignature;

import java.util.List;

import lexicalAnalyzer.Lextant;
import tokens.LextantToken;
import tokens.Token;

public class ArrayExpressionNode extends ParseNode {
	private List<PromotionSignature> promotionSignature;

	public ArrayExpressionNode(Token token) {
		super(token);
		assert(token instanceof LextantToken);
	}

	public ArrayExpressionNode(ParseNode node) {
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
	
	public List<PromotionSignature> getPromotionSignature() {
		return promotionSignature;
	}

	public void setPromotionSignature(List<PromotionSignature> promotedSignaturesList) {
		this.promotionSignature = promotedSignaturesList;
		
	}
	



	
	////////////////////////////////////////////////////////////
	// convenience factory

	public static ParseNode withChildren(Token token, ParseNode ...children) {
		ArrayExpressionNode node = new ArrayExpressionNode(token);
		for(ParseNode child: children) {
			node.appendChild(child);
		}
		return node;
	}
	public static ParseNode withArrayChildren(Token token, List<ParseNode> nodes) {   
		ArrayExpressionNode node = new ArrayExpressionNode(token);
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
		visitor.visitLeave(this);
	}
	
}

