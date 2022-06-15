package parseTree.nodeTypes;

import lexicalAnalyzer.Keyword;
import lexicalAnalyzer.Punctuator;
import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import semanticAnalyzer.types.Array;
import semanticAnalyzer.types.PrimitiveType;
import semanticAnalyzer.types.Range;
import semanticAnalyzer.types.Type;
import semanticAnalyzer.types.TypeVariable;
import tokens.CharConstantToken;
import tokens.FloatingConstantToken;
import tokens.IntegerConstantToken;
import tokens.StringConstantToken;
import tokens.Token;

public class TypeNode extends ParseNode {

	public TypeNode(ParseNode node) {
		super(node);
	}
	public TypeNode(Token token) {
		super(token);
	}
	
	
	///////////////////////////////////////////////////////////
	// boilerplate for visitors
	
	public void typeFromToken() {
		if(token.isLextant(Keyword.FLOAT)) {
			this.setType(PrimitiveType.FLOATING);
		}
		else if(token.isLextant(Keyword.BOOL)) {
			this.setType(PrimitiveType.BOOLEAN);
		}
		else if(token.isLextant(Keyword.INT)) {
			this.setType(PrimitiveType.INTEGER);
		}
		else if(token.isLextant(Keyword.CHAR)) {
			this.setType(PrimitiveType.CHARACTER);
		}
		else if(token.isLextant(Keyword.STRING)) {
			this.setType(PrimitiveType.STRING);
		}
		else if(token.isLextant(Keyword.NULL_KEYWORD)) {
			this.setType(PrimitiveType.ERROR);
		}
	}
			
	public void accept(ParseNodeVisitor visitor) {
		visitor.visitEnter(this);
		visitChildren(visitor);
		visitor.visit(this);
		visitor.visitLeave(this);
	}
	
	public static ParseNode make(Token typeToken) {
		return new TypeNode(typeToken);
	}
	
	public static ParseNode withChild(Token token, ParseNode child) {
		TypeNode node = new TypeNode(token);
		node.appendChild(child);
		return node;
	}
	public boolean isArray() {
		if(this.getToken().isLextant(Punctuator.OPEN_SQUARE)) {
			return true;
		}
		else {
			return false;
		}
		
	}
	public boolean isRange() {
		if(this.getToken().isLextant(Keyword.RANGE) || this.getToken().isLextant(Punctuator.LESSER)) {
			return true;
		}
		else {
			return false;
		}
		
	}
}
