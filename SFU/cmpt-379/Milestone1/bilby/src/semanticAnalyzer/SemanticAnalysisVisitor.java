package semanticAnalyzer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import lexicalAnalyzer.Keyword;
import lexicalAnalyzer.Lextant;
import lexicalAnalyzer.Punctuator;
import logging.BilbyLogger;
import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import parseTree.nodeTypes.ArrayConstantNode;
import parseTree.nodeTypes.ArrayExpressionNode;
import parseTree.nodeTypes.AssignmentForNode;
import parseTree.nodeTypes.AssignmentNode;
import parseTree.nodeTypes.BooleanConstantNode;
import parseTree.nodeTypes.BreakNode;
import parseTree.nodeTypes.CharConstantNode;
import parseTree.nodeTypes.ContinueNode;
import parseTree.nodeTypes.DeclarationForNode;
import parseTree.nodeTypes.BlockNode;
import parseTree.nodeTypes.DeclarationNode;
import parseTree.nodeTypes.ErrorNode;
import parseTree.nodeTypes.FloatingConstantNode;
import parseTree.nodeTypes.ForNode;
import parseTree.nodeTypes.FunctionBlockNode;
import parseTree.nodeTypes.FunctionInvocationNode;
import parseTree.nodeTypes.FunctionNode;
import parseTree.nodeTypes.IdentifierNode;
import parseTree.nodeTypes.IfBlockNode;
import parseTree.nodeTypes.IntegerConstantNode;
import parseTree.nodeTypes.LoopBlockNode;
import parseTree.nodeTypes.NewlineNode;
import parseTree.nodeTypes.OperatorNode;
import parseTree.nodeTypes.ParameterListNode;
import parseTree.nodeTypes.ParameterNode;
import parseTree.nodeTypes.ParanthesisNode;
import parseTree.nodeTypes.PrintStatementNode;
import parseTree.nodeTypes.ProgramNode;
import parseTree.nodeTypes.SpaceNode;
import parseTree.nodeTypes.StringConstantNode;
import parseTree.nodeTypes.RangeConstantNode;
import parseTree.nodeTypes.ReturnNode;
import parseTree.nodeTypes.TabNode;
import parseTree.nodeTypes.TypeNode;
import semanticAnalyzer.signatures.FunctionSignatures;
import semanticAnalyzer.signatures.PromotionSignature;
import semanticAnalyzer.types.Array;
import semanticAnalyzer.types.Function;
import semanticAnalyzer.types.PrimitiveType;
import semanticAnalyzer.types.Range;
import semanticAnalyzer.types.Type;
import symbolTable.Binding;
import symbolTable.ParameterMemoryAllocator;
import symbolTable.Scope;
import tokens.LextantToken;
import tokens.Token;

class SemanticAnalysisVisitor extends ParseNodeVisitor.Default {
	@Override
	public void visitLeave(ParseNode node) {
		throw new RuntimeException("Node class unimplemented in SemanticAnalysisVisitor: " + node.getClass());
	}

	///////////////////////////////////////////////////////////////////////////
	// constructs larger than statements
	@Override
	public void visitEnter(ProgramNode node) {
		enterProgramScope(node);
	}

	public void visitLeave(ProgramNode node) {
		leaveScope(node);
	}

	public void visitEnter(BlockNode node) {
		enterSubscope(node);
	}

	public void visitLeave(BlockNode node) {
		leaveScope(node);
	}
	
	public void visitEnter(FunctionBlockNode node) {
		enterProcedureScope(node);
		ParameterMemoryAllocator paramMemory = (ParameterMemoryAllocator) node.getScope().getAllocationStrategy();
		paramMemory.allocateFP(8);
	}

	public void visitLeave(FunctionBlockNode node) {
		leaveScope(node);
	}
	
	public void visitLeave(ReturnNode node) {
		ParseNode parent = node.getParent();
		while(!(parent instanceof FunctionNode || parent instanceof ProgramNode)) {
			parent = parent.getParent();
		}
		assert (parent instanceof FunctionNode);
	}

	public void visitEnter(IfBlockNode node) {
		enterSubscope(node);
	}

	public void visitLeave(IfBlockNode node) {
		leaveScope(node);
	}

	public void visitEnter(LoopBlockNode node) {
		enterSubscope(node);
	}

	public void visitLeave(LoopBlockNode node) {
		leaveScope(node);
	}

	public void visitEnter(FunctionNode node) {
		// enterParameterScope(node);
	}

	public void visitLeave(FunctionNode node) {
		/*
		ParseNode paramList = node.child(2);
		List<Type> paramTypes = new ArrayList<Type>();
		for (ParseNode child : paramList.getChildren()) {
			Type childType = child.child(0).getType();
			paramTypes.add(childType);
		}
		Type returnType = node.child(0).getType();
		paramTypes.add(returnType);
		Type functionType = new Function(paramTypes);
		IdentifierNode identifier = (IdentifierNode) node.child(1);
		identifier.setType(functionType);
		addBinding(identifier, functionType);
		System.out.println(identifier.getBinding());
		*/
		// Check return type matches
		Type returnType = node.child(0).getType();
		List<Boolean> returnChecker = checkReturnType(node.child(3), returnType, new ArrayList<Boolean>());
		if (returnChecker.contains(false)) {
			logError("return type does not match function declaration type");
		}
		if (returnChecker.isEmpty() && returnType != PrimitiveType.VOID) {
			logError("function needs return statement");
		}
		leaveScope(node);

	}

	public List<Boolean> checkReturnType(ParseNode node, Type returnType, List<Boolean> returns) {
		if (node instanceof ReturnNode) {
			if (node.nChildren() == 0) {
				returns.add(PrimitiveType.VOID == returnType);
				return returns;
			} else {
				Type atomicNodeType = node.child(0).getType();
				while(((atomicNodeType.isArray() || atomicNodeType.isRange())) && (returnType.isArray() || returnType.isRange())) {
					atomicNodeType = atomicNodeType.atomicType();
					returnType = returnType.atomicType();	
				}
				returns.add(atomicNodeType == returnType);
				return returns;
			}
		}
		for (ParseNode child : node.getChildren()) {
			returns = checkReturnType(child, returnType, returns);
		}
		return returns;

	}

	public void visitEnter(ParameterListNode node) {
		enterParameterScope(node.getParent());
	}

	public void visitLeave(ParameterListNode node) {
	}

	public void visitEnter(ForNode node) {
		enterSubscope(node);
	}

	///////////////////////////////////////////////////////////////////////////
	// helper methods for scoping.
	private void enterProgramScope(ParseNode node) {
		Scope scope = Scope.createProgramScope();
		node.setScope(scope);
	}

	@SuppressWarnings("unused")
	private void enterSubscope(ParseNode node) {
		Scope baseScope = node.getLocalScope();
		Scope scope = baseScope.createSubscope();
		node.setScope(scope);
	}
	private void enterProcedureScope(ParseNode node) {
		Scope baseScope = node.getLocalScope();
		Scope scope = baseScope.createProcedureScope();
		node.setScope(scope);
	}

	private void enterParameterScope(ParseNode node) {
		Scope baseScope = node.getLocalScope();
		Scope scope = baseScope.createParameterScope();
		node.setScope(scope);
	}

	private void leaveScope(ParseNode node) {
		node.getScope().leave();
	}

	///////////////////////////////////////////////////////////////////////////
	// statements and declarations
	@Override
	public void visitLeave(PrintStatementNode node) {
	}

	public void visitLeave(DeclarationForNode node) {
	}

	// @Override
	/*
	 * public void visit(DeclarationNode node) { if(!(node.getParent() instanceof
	 * ForNode)) { return; }
	 * 
	 * System.out.println("in declEnter"); System.out.println(node);
	 * if(!node.getType().equals(PrimitiveType.NO_TYPE)) {
	 * //System.out.println("already scanned"); return; } IdentifierNode identifier
	 * = (IdentifierNode) node.child(0);
	 * 
	 * ParseNode initializer = node.child(1);
	 * 
	 * Type declarationType = PrimitiveType.INTEGER; node.setType(declarationType);
	 * 
	 * identifier.setType(declarationType); if((initializer.nChildren() > 0) &&
	 * (identifier.equals(initializer.child(0)))){ return; } addBinding(identifier,
	 * declarationType); }
	 */
	public void visitEnter(DeclarationNode node) {
	}
	public void visitLeave(DeclarationNode node) {
		if (!node.getType().equals(PrimitiveType.NO_TYPE)) {
			return;
		}
		IdentifierNode identifier = (IdentifierNode) node.child(0);

		ParseNode initializer = node.child(1);
		Type declarationType = initializer.getType();
		if(declarationType.equals(PrimitiveType.VOID)) {
			logError("trying to assign to void function");
		}
		node.setType(declarationType);

		identifier.setType(declarationType);
		if ((initializer.nChildren() > 0) && (identifier.equals(initializer.child(0)))) {
			return;
		}
		addBinding(identifier, declarationType);
	}
	
	public void visitLeave(AssignmentForNode node) {
		
		IdentifierNode identifier = (IdentifierNode) node.child(0);
		identifier.setMutability(true);
		if ((!identifier.mutable()) && (!node.child(1).getToken().isLextant(Punctuator.INDEXING_ASSIGN))) {
			logError("Trying to assign to immutable type");
		}
		Type identifierType;
		if (node.child(1).getToken().isLextant(Punctuator.INDEXING_ASSIGN)) {
			identifierType = identifier.getType().atomicType();
		} else {
			identifierType = identifier.getType();
		}
		ParseNode initializer = node.child(1);
		Type declarationType = initializer.getType();
		int promotable = 0;
		if (identifierType == PrimitiveType.FLOATING) {
			promotable = 1;
		}
		if ((identifierType == PrimitiveType.INTEGER) && (declarationType == PrimitiveType.CHARACTER)) {
			promotable = 1;
		}

		Type whileDeclarationType = declarationType;
		Type whileIdentifierType = identifierType;

		while ((whileDeclarationType.isArray()) && (whileIdentifierType.isArray())) {
			whileDeclarationType = whileDeclarationType.atomicType();
			whileIdentifierType = whileIdentifierType.atomicType();
		}
		if ((initializer.nChildren() > 0) && (initializer.child(0).getToken().isLextant(Punctuator.INDEXING))) {
			whileIdentifierType = whileIdentifierType.atomicType();
			initializer = initializer.child(0);
			while (initializer.nChildren() == 3) {
				whileIdentifierType = whileIdentifierType.atomicType();
				initializer = initializer.child(2);
			}
		}

		if ((whileIdentifierType != whileDeclarationType) && (promotable == 0)) {
			logError("Assignment Type does not match initial declaration");
		} else if (promotable == 1) {
			node.setType(identifierType);
		} else {
			node.setType(declarationType);
		}
		identifier.setMutability(false);
	}

	public void visitLeave(AssignmentNode node) {
		IdentifierNode identifier = (IdentifierNode) node.child(0);
		if ((!identifier.mutable()) && (!node.child(1).getToken().isLextant(Punctuator.INDEXING_ASSIGN))) {
			logError("Trying to assign to immutable type");
		}
		Type identifierType;
		if (node.child(1).getToken().isLextant(Punctuator.INDEXING_ASSIGN)) {
			identifierType = identifier.getType().atomicType();
		} else {
			identifierType = identifier.getType();
		}
		ParseNode initializer = node.child(1);
		Type declarationType = initializer.getType();
		if(declarationType.equals(PrimitiveType.VOID)) {
			logError("trying to assign to void function");
		}
		int promotable = 0;
		if (identifierType == PrimitiveType.FLOATING) {
			promotable = 1;
		}
		if ((identifierType == PrimitiveType.INTEGER) && (declarationType == PrimitiveType.CHARACTER)) {
			promotable = 1;
		}

		Type whileDeclarationType = declarationType;
		Type whileIdentifierType = identifierType;

		while ((whileDeclarationType.isArray()) && (whileIdentifierType.isArray())) {
			whileDeclarationType = whileDeclarationType.atomicType();
			whileIdentifierType = whileIdentifierType.atomicType();
		}
		if ((initializer.nChildren() > 0) && (initializer.child(0).getToken().isLextant(Punctuator.INDEXING))) {
			whileIdentifierType = whileIdentifierType.atomicType();
			initializer = initializer.child(0);
			while (initializer.nChildren() == 3) {
				whileIdentifierType = whileIdentifierType.atomicType();
				initializer = initializer.child(2);
			}
		}

		if ((whileIdentifierType != whileDeclarationType) && (promotable == 0)) {
			logError("Assignment Type does not match initial declaration");
		} else if (promotable == 1) {
			node.setType(identifierType);
		} else {
			node.setType(declarationType);
		}
	}

	public void visit(TypeNode node) {
		if (node.isArray()) {
			Type subtype = node.child(0).getType();
			Type arrayType = new Array(subtype);
			node.setType(arrayType);
			return;
		}
		else if (node.isRange()) {
			Type subtype = node.child(0).getType();
			Type rangeType = new Range(subtype);
			node.setType(rangeType);
			return;
		}
		else {
			if (node.getToken().isLextant(Keyword.VOID)) {
				if (!(node.getParent() instanceof FunctionNode)) {
					logError("only function nodes can have type void");
				} else {
					node.setType(PrimitiveType.VOID);
					return;
				}
			}
			node.typeFromToken();
		}
	}

	///////////////////////////////////////////////////////////////////////////
	// expressions
	@Override
	public void visitLeave(ArrayExpressionNode node) {
		int children = node.nChildren();
		Type subType = node.child(children-1).getType();
		//ParseNode arrayType = node.child(children-1);
		//arrayType.setType(subType);
		/*
		if(subType == PrimitiveType.ERROR) {
			subType = node.child(0).getType();
		}
		if ((node.child(0).getType().isArray() || (node.child(0).getType().isRange()))) {
			subType = node.child(0).getType();
		}
		*/
		Lextant operator = operatorFor(node);

		node.setType(new Array(subType));

		FunctionSignatures signatures = FunctionSignatures.signaturesOf(operator);
		List<Type> childTypes = new ArrayList<>();
		List<PromotionSignature> promotedSignaturesList = new ArrayList<>();
		List<PromotionSignature> promotedSignatures = new ArrayList<>();
		for (int i = 0; i < children - 2; i++) {
			childTypes = new ArrayList<>();
			childTypes.add(node.getType());
			childTypes.add(node.child(i).getType());
			promotedSignatures = signatures.leastLevelPromotions(childTypes);
			promotedSignaturesList.add(promotedSignatures.get(0));
		}

		node.setPromotionSignature(promotedSignaturesList);
	}

	public void visitLeave(ForNode node) {
		// enterSubscope(node);
		List<Type> childTypes = new ArrayList<>();
		childTypes.add(node.child(2).getType());
		Lextant operator = operatorFor(node);

		FunctionSignatures signatures = FunctionSignatures.signaturesOf(operator);

		List<PromotionSignature> promotedSignatures = signatures.leastLevelPromotions(childTypes);

		if (promotedSignatures.isEmpty()) {
			typeCheckError(node, childTypes);
			node.setType(PrimitiveType.ERROR);
		} else if (promotedSignatures.size() > 1) {
			multipleInterpretationError(node, childTypes);
			node.setType(PrimitiveType.ERROR);
		} else {
			PromotionSignature promotedSignature = promotedSignatures.get(0);
			node.setType(promotedSignature.resultType());
			node.setPromotionSignature(promotedSignature);
		}
		leaveScope(node);

	}

	/*
	 * public void visit(OperatorNode node) { if((!(node.getParent().getParent()
	 * instanceof DeclarationForNode))) { return; }
	 * System.out.println("in opEnter"); System.out.println(node); List<Type>
	 * childTypes = childTypes(node); Lextant operator = operatorFor(node);
	 * 
	 * FunctionSignatures signatures = FunctionSignatures.signaturesOf(operator);
	 * 
	 * List <PromotionSignature> promotedSignatures =
	 * signatures.leastLevelPromotions(childTypes);
	 * 
	 * if(promotedSignatures.isEmpty()) { typeCheckError(node, childTypes);
	 * node.setType(PrimitiveType.ERROR); } else if(promotedSignatures.size() > 1) {
	 * multipleInterpretationError(node, childTypes);
	 * node.setType(PrimitiveType.ERROR); } else { PromotionSignature
	 * promotedSignature = promotedSignatures.get(0);
	 * node.setType(promotedSignature.resultType());
	 * node.setPromotionSignature(promotedSignature); }
	 * 
	 * }
	 */
	public void visitLeave(FunctionInvocationNode node) {
		//System.out.println(node);
		Function funcType = (Function) node.child(0).getType();
		//Function funcType = (Function) node.child(0).getType();
		List<ParseNode> paramNodes = funcType.getParamNodes();
		List<Type> types = funcType.getParamTypes();
		Type returnType = types.get(types.size() - 1);
		node.setType(returnType);
		// Check that params for func call match required params;
		types = types.subList(0, types.size() - 1);
		paramNodes = paramNodes.subList(0, paramNodes.size() - 1);
		ParseNode exprList = node.child(1);
		if (types.size() != exprList.nChildren()) {
			logError("func got incorrect params");
		}
		int i = 0;
		for (ParseNode paramNode : paramNodes) {
			Type type = paramNode.child(0).getType();
			Type tempExprList = exprList.child(i).getType();
			while(((type.isArray() || type.isRange())) && (tempExprList.isArray() || tempExprList.isRange())) {
				type = type.atomicType();
				tempExprList = tempExprList.atomicType();	
			}
			if (type != tempExprList) {
				logError("func got incorrect parameter types");
			}
			i = i + 1;

		}
	}

	public void visitLeave(ParanthesisNode node) {
		node.setType(node.child(0).getType());
	}
	
	
	public void visitLeave(OperatorNode node) {
		// System.out.println("in op");
		// System.out.println(node);
		if (!node.getType().equals(PrimitiveType.NO_TYPE)) {
			// System.out.println("already done");
			return;
		}
		List<Type> childTypes = childTypes(node);
		Lextant operator = operatorFor(node);

		FunctionSignatures signatures = FunctionSignatures.signaturesOf(operator);

		List<PromotionSignature> promotedSignatures = signatures.leastLevelPromotions(childTypes);

		if (promotedSignatures.isEmpty()) {
			typeCheckError(node, childTypes);
			node.setType(PrimitiveType.ERROR);
		} else if (promotedSignatures.size() > 1) {
			multipleInterpretationError(node, childTypes);
			node.setType(PrimitiveType.ERROR);
		}
		
		else if(node.getToken().isLextant(Keyword.LOW)) {
			PromotionSignature promotedSignature = promotedSignatures.get(0);
			Type subtype = node.child(0).getType();
			node.setType(subtype.atomicType());
			node.setPromotionSignature(promotedSignature);
		}
		else if(node.getToken().isLextant(Keyword.HIGH)) {
			PromotionSignature promotedSignature = promotedSignatures.get(0);
			Type subtype = node.child(0).getType();
			node.setType(subtype.atomicType());
			node.setPromotionSignature(promotedSignature);
		}

		else if (node.getToken().isLextant(Keyword.ALLOC)) {
			PromotionSignature promotedSignature = promotedSignatures.get(0);
			Type subtype = node.child(0).getType();
			node.setType(subtype);
			node.setPromotionSignature(promotedSignature);
		} else if (node.getToken().isLextant(Punctuator.OPEN_SQUARE)) {
			PromotionSignature promotedSignature = promotedSignatures.get(0);
			Type subtype = node.child(0).getType();
			node.setType(subtype);
			node.setPromotionSignature(promotedSignature);
		}

		else if (node.getToken().isLextant(Punctuator.RANGE_CREATION)) {
			PromotionSignature promotedSignature = promotedSignatures.get(0);
			Type subtype = node.child(0).getType();
			if (node.child(0).getType().equals(PrimitiveType.CHARACTER)) {
				subtype = node.child(1).getType();
			} else if (node.child(0).getType().equals(PrimitiveType.FLOATING)) {
				subtype = node.child(0).getType();
			} else if (node.child(1).getType().equals(PrimitiveType.FLOATING)) {
				subtype = node.child(1).getType();
			}
			Type rangeType = new Range(subtype);
			node.setType(rangeType);
			node.setPromotionSignature(promotedSignature);
		} else if (((node.nChildren() > 1) && (((node.child(0).getToken().isLextant(Punctuator.RANGE_CREATION))
				&& (node.getToken().isLextant(Punctuator.ADD)))
				|| ((node.child(1).getToken().isLextant(Keyword.RANGE)) && (node.getToken().isLextant(Keyword.CAST))
						|| ((node.child(1).getToken().isLextant(Punctuator.RANGE_CREATION))
								&& (node.getToken().isLextant(Punctuator.ADD))))))) {
			PromotionSignature promotedSignature = promotedSignatures.get(0);
			Type subtype = PrimitiveType.NO_TYPE;
			if (node.child(1).getToken().isLextant(Punctuator.RANGE_CREATION)) {
				subtype = node.child(1).getType();
			} else {
				subtype = node.child(0).getType();
			}
			node.setType(subtype);
			node.setPromotionSignature(promotedSignature);
		}

		else if (node.getToken().isLextant(Punctuator.INDEXING_ASSIGN)) {
			PromotionSignature promotedSignature = promotedSignatures.get(0);
			Type subtype = childTypes.get(0).atomicType();
			node.setType(subtype);
			node.setPromotionSignature(promotedSignature);
		} else if (node.getToken().isLextant(Punctuator.INDEXING)) {
			PromotionSignature promotedSignature = promotedSignatures.get(0);
			Type subtype = childTypes.get(0).atomicType();
			node.setType(subtype);
			node.setPromotionSignature(promotedSignature);
		}

		else {
			PromotionSignature promotedSignature = promotedSignatures.get(0);
			//System.out.println(promotedSignatures.get(0).getPromotions());
			node.setType(promotedSignature.resultType());
			node.setPromotionSignature(promotedSignature);
			if(node.getToken().isLextant(Keyword.CAST) && (node.child(0).getType().isArray()) || node.child(0).getType().isRange()) {
				if(node.child(0).getType().isArray()){
					node.setType(new Array(node.getType()));
				}
				else{
					node.setType(new Range(node.getType()));
				}
			}
		}
	}

	private List<Type> childTypes(ParseNode node) {
		List<Type> childTypes;
		if (node.nChildren() == 1) {
			ParseNode child = node.child(0);
			childTypes = Arrays.asList(child.getType());
		} else if ((node.getToken().isLextant(Keyword.IF)) || (node.getToken().isLextant(Keyword.ELSE))
				|| (node.getToken().isLextant(Keyword.WHILE)) || (node.getToken().isLextant(Keyword.FOR))) {
			ParseNode statement = node.child(0);
			childTypes = Arrays.asList(statement.getType());
		} else if (node.getToken().isLextant(Punctuator.INDEXING_ASSIGN)) {
			ParseNode left = node.child(0);
			ParseNode temp = node;
			while (temp.getParent().getToken().isLextant(Punctuator.INDEXING_ASSIGN)) {
				left.setType(left.getType().atomicType());
				temp = temp.getParent();
			}
			// ParseNode left = node.child(0);
			ParseNode middle = node.child(1);
			ParseNode right = node.child(2);
			childTypes = Arrays.asList(left.getType(), right.getType());
			if (!middle.getType().equals(PrimitiveType.INTEGER)) {
				typeCheckError(node, childTypes);
				node.setType(PrimitiveType.ERROR);
			}
		} else if (node.getToken().isLextant(Keyword.CAST)) {
			ParseNode left = node.child(0);
			if (left.getType().isRange()) {
				if (!node.child(1).getToken().isLextant(Keyword.RANGE)) {
					logError("Range can only be cast to range");
				} else {
					node.child(1).setType(node.child(0).getType());
				}
			}
			if (left.getType().isArray()) {
				if (!node.child(1).getToken().isLextant(Keyword.ARRAY)) {
					logError("array can only be cast to array");
				} else {
					node.child(1).setType(node.child(0).getType());
				}
			}
			ParseNode middle = node.child(1);
			if(middle instanceof ArrayConstantNode && !(left.getType().isArray())) {
				logError("can only cast an array to array");
			}
			if(middle instanceof RangeConstantNode && !(left.getType().isRange())) {
				logError("can only cast a range to range");
			}
			//System.out.println(node);
			childTypes = Arrays.asList(left.getType(), middle.getType());
		} else if ((node.getToken().isLextant(Punctuator.INDEXING) && (node.nChildren() == 3))) {
			ParseNode left = node.child(2);
			ParseNode right = node.child(1);
			childTypes = Arrays.asList(left.getType(), right.getType());
		} else {
			assert node.nChildren() == 2;
			ParseNode left = node.child(0);
			ParseNode right = node.child(1);
			childTypes = Arrays.asList(left.getType(), right.getType());
		}
		return childTypes;
	}

	private Lextant operatorFor(ParseNode node) {
		LextantToken token = (LextantToken) node.getToken();
		return token.getLextant();
	}

	///////////////////////////////////////////////////////////////////////////
	// simple leaf nodes
	@Override
	public void visit(BooleanConstantNode node) {
		node.setType(PrimitiveType.BOOLEAN);
	}

	@Override
	public void visit(ErrorNode node) {
		node.setType(PrimitiveType.ERROR);
	}

	@Override
	public void visit(IntegerConstantNode node) {
		node.setType(PrimitiveType.INTEGER);
	}

	public void visit(FloatingConstantNode node) {
		node.setType(PrimitiveType.FLOATING);
	}

	public void visit(CharConstantNode node) {
		node.setType(PrimitiveType.CHARACTER);
	}

	public void visit(StringConstantNode node) {
		node.setType(PrimitiveType.STRING);
	}

	public void visit(RangeConstantNode node) {
		node.setType(PrimitiveType.CHARACTER);
	}

	@Override
	public void visit(NewlineNode node) {
	}

	@Override
	public void visit(SpaceNode node) {
	}

	public void visit(TabNode node) {
	}

	// Continue/ break confirm that an ancestor is loopBlockNode

	public void visit(ContinueNode node) {
		ParseNode parent = node.getParent();
		while (!(parent instanceof LoopBlockNode)) {
			if (parent instanceof ProgramNode) {
				break;
			}
			parent = parent.getParent();
		}
		if (parent instanceof LoopBlockNode) {
		} else {
			logError("continue needs to be associated with a loop");
		}
	}

	public void visit(BreakNode node) {
		ParseNode parent = node.getParent();
		while (!(parent instanceof LoopBlockNode)) {
			if (parent instanceof ProgramNode) {
				logError("break needs to be associated with a loop");
			}
			parent = parent.getParent();
		}
	}

	///////////////////////////////////////////////////////////////////////////
	// IdentifierNodes, with helper methods
	@Override
	/*
	 * public void visitEnter(IdentifierNode node) {
	 * System.out.println("in visitEnter ID"); System.out.println(node.getParent());
	 * if(!(node.getParent().getToken().isLextant(Keyword.LOW))){ return; }
	 * System.out.println("passed visitEnter ID");
	 * System.out.println(node.getParent()); Binding binding =
	 * node.findVariableBinding(); node.setType(binding.getType());
	 * node.setBinding(binding); boolean mutable = false;
	 * if(node.getType().isArray()) { mutable = true; } else { mutable =
	 * findMutability(node); } node.setMutability(mutable); }
	 */
	/*
	public void visitEnter(IdentifierNode node) {
		System.out.println("visit enter ID");
		System.out.println(node.getParent());
	}
	*/
	public void visit(IdentifierNode node) {
		/*
		ParseNode newNode = forNodeLesserEqual(node);
		System.out.println(newNode);
		if(newNode instanceof OperatorNode) {
			node = (IdentifierNode) newNode.child(0);
		}
		*/
		
		if (!isBeingDeclared(node)) {
			// System.out.println(node.getParent());
			if (node.getParent() instanceof FunctionNode) {
				setFunctionIdentifierType(node);
				return;
			}
			if (node.getParent() instanceof ParameterNode) {
				node.setType(node.getParent().child(0).getType());
				addBinding(node, node.getType());
				node.setMutability(true);
				return;
			}

			if (node.getParent() instanceof FunctionInvocationNode) {
				IdentifierNode funcInit = findFuncDeclaration(node);
				node.setType(funcInit.getType());
				node.setBinding(funcInit.getBinding());
				return;
			}

			/*
			 * if(parent.getToken().isLextant(Punctuator.LESSEREQUAL) && (parent.getParent()
			 * instanceof ForNode)) {
			 * System.out.println(parent.getParent().getParent().child(0).child(1));
			 * visitLeave((OperatorNode)parent.getParent().getParent().child(0).child(1));
			 * visitLeave((DeclarationNode)parent.getParent().getParent().child(0)); }
			 */
			/*
			 * if(node.getParent().getParent() instanceof ForNode) {
			 * node.setType(PrimitiveType.INTEGER); addBinding(node, PrimitiveType.INTEGER);
			 * }
			 */
			Binding binding = node.findVariableBinding();
			/*
			 * if(binding.equals(binding.nullInstance())) {
			 * System.out.println("binding null");
			 * System.out.println(parent.getParent().getParent().child(0).child(1).child(0))
			 * ;
			 * visit((IdentifierNode)parent.getParent().getParent().child(0).child(1).child(
			 * 0));
			 * visitLeave((OperatorNode)parent.getParent().getParent().child(0).child(1));
			 * visitLeave((DeclarationNode)parent.getParent().getParent().child(0));
			 * node.setMutability(true); binding = node.findVariableBinding();
			 * System.out.println(binding); //return; }
			 */
			node.setType(binding.getType());
			node.setBinding(binding);
			boolean mutable = findMutability(node);
			// boolean mutable = false;
			// if(node.getType().isArray()) {
			// mutable = true;
			// }
			// else {
			// mutable = findMutability(node);
			// }
			node.setMutability(mutable);
		}
		// else parent DeclarationNode does the processing.
	}
	
	public void setFunctionIdentifierType(IdentifierNode node) {
		ParseNode parent = node.getParent();
		ParseNode paramList = parent.child(2);
		List<ParseNode> paramNodes = new ArrayList<ParseNode>();
		for (ParseNode child : paramList.getChildren()) {
			/*
			String token = child.child(0).getToken().getLexeme().toUpperCase();
			Type childType = null;
			if(child.child(0).getToken().getLexeme().equals("[")) {
				visit((TypeNode) child.child(0).child(0));
				Type subtype = child.child(0).getType();
				childType = new Array(subtype);
			}
			else if(child.child(0).getToken().getLexeme().equals("<")) {
				visit((TypeNode) child.child(0).child(0));
				Type subtype = child.child(0).getType();
				childType = new Range(subtype);
			}
			else {
				childType = typeFromToken(token);
			}
			*/
			paramNodes.add(child);
		}
		ParseNode returnNode = parent.child(0);
		paramNodes.add(returnNode);
		Type functionType = new Function(paramNodes);
		node.setType(functionType);
		addBinding(node, functionType);
	}
	public Type typeFromToken(String token) {
		if(token.equals(Keyword.INT.toString())) {
			return PrimitiveType.INTEGER;
		}
		if(token.equals(Keyword.CHAR.toString())) {
			return PrimitiveType.CHARACTER;
		}
		if(token.equals(Keyword.FLOAT.toString())) {
			return PrimitiveType.FLOATING;
		}
		if(token.equals(Keyword.BOOL.toString())) {
			return PrimitiveType.BOOLEAN;
		}
		if(token.equals(Keyword.STRING.toString())) {
			return PrimitiveType.STRING;
		}
		else {
			logError("parameter type not implemented");
			return PrimitiveType.ERROR;
		}
	}

	public boolean isInt(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	public boolean isFloat(String s) {
		try {
			Float.parseFloat(s);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	private IdentifierNode findFuncDeclaration(IdentifierNode node) {
		Token nodeToken = node.getToken();
		ParseNode current = node.getParent();
		while (!(current instanceof ProgramNode)) {
			current = current.getParent();
		}
		for (ParseNode child : current.getChildren()) {
			if (child.child(1).getToken().getLexeme().equals(nodeToken.getLexeme())) {
				return (IdentifierNode) child.child(1);
			}
		}
		return null;
	}

	private boolean isBeingDeclared(IdentifierNode node) {
		ParseNode parent = node.getParent();
		return ((parent instanceof DeclarationNode) && (node == parent.child(0)));
	}

	private void addBinding(IdentifierNode identifierNode, Type type) {
		Scope scope = identifierNode.getLocalScope();
		Binding binding = scope.createBinding(identifierNode, type);
		identifierNode.setBinding(binding);
	}

	private boolean findMutability(IdentifierNode node) {
		if (node.mutable()) {
			return true;
		}
		String identifier = node.getToken().getLexeme();

		for (ParseNode current : node.pathToRoot()) {
			if (current.containsBindingOf(identifier)) {
				for (ParseNode child : current.getChildren()) {
					if ((child instanceof DeclarationNode) && (child.child(0).getToken().getLexeme() == identifier)) {
						if (child.getToken().isLextant(Keyword.MUT)) {
							return true;
						}
					}
					/*
					 * else if(child instanceof ParameterListNode) { for(ParseNode grandChild :
					 * child.getChildren()) { if(grandChild.child(1).getToken().getLexeme() ==
					 * identifier) { return true; } } }
					 */
				}
			}
		}
		return false;
	}

	///////////////////////////////////////////////////////////////////////////
	// error logging/printing

	private void typeCheckError(ParseNode node, List<Type> operandTypes) {
		Token token = node.getToken();

		logError("operator " + token.getLexeme() + " not defined for types " + operandTypes + " at "
				+ token.getLocation());
	}

	private void logError(String message) {
		BilbyLogger log = BilbyLogger.getLogger("compiler.semanticAnalyzer");
		log.severe(message);
	}

	private void multipleInterpretationError(ParseNode node, List<Type> operandTypes) {
		Token token = node.getToken();

		logError("operator " + token.getLexeme() + " has multiple interpretations for types  " + operandTypes + " at "
				+ token.getLocation());
	}
}