package parseTree.nodeTypes;

import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import logging.BilbyLogger;
import symbolTable.Binding;
import symbolTable.Scope;
import tokens.IdentifierToken;
import tokens.Token;

public class IdentifierNode extends ParseNode {
	private Binding binding;
	private Scope declarationScope;
	private boolean mutable;

	public IdentifierNode(Token token) {
		super(token);
		assert(token instanceof IdentifierToken);
		this.binding = null;
		this.mutable = false;
	}
	public IdentifierNode(ParseNode node) {
		super(node);
		if(node instanceof IdentifierNode) {
			this.binding = ((IdentifierNode)node).binding;
		}
		else {
			this.binding = null;
		}
	}
	
////////////////////////////////////////////////////////////
// attributes
	
	public IdentifierToken identifierToken() {
		return (IdentifierToken)token;
	}
	
	public boolean mutable() {
		return mutable;
	}
	
	public void setMutability(boolean Value) {
		this.mutable = Value;
	}

	public void setBinding(Binding binding) {
		this.binding = binding;
	}
	public Binding getBinding() {
		return binding;
	}
	
////////////////////////////////////////////////////////////
// Speciality functions

	public Binding findVariableBinding() {
		String identifier = token.getLexeme();

		for(ParseNode current : pathToRoot()) {
			if(current.containsBindingOf(identifier)) {
				declarationScope = current.getScope();
				return current.bindingOf(identifier);
			}
		}
		useBeforeDefineError();
		return Binding.nullInstance();
	}
	
	private ParseNode ChildContainsBinding(ParseNode parent, String toFind) {
		System.out.println("In ChildLooker");
		System.out.println(toFind);
		for(ParseNode child : parent.getChildren()) {
			System.out.println(child);
			if(child.containsBindingOf(toFind)) {
				return child;
			}
		}
		useBeforeDefineError();
		return null;
		
	}

	public Scope getDeclarationScope() {
		findVariableBinding();
		return declarationScope;
	}
	public void useBeforeDefineError() {
		BilbyLogger log = BilbyLogger.getLogger("compiler.semanticAnalyzer.identifierNode");
		Token token = getToken();
		log.severe("identifier " + token.getLexeme() + " used before defined at " + token.getLocation());
	}
	
///////////////////////////////////////////////////////////
// accept a visitor
		
	public void accept(ParseNodeVisitor visitor) {
		visitor.visitEnter(this);
		visitor.visit(this);
	}
}
