package parseTree;

import parseTree.nodeTypes.BooleanConstantNode;
import parseTree.nodeTypes.BreakNode;
import parseTree.nodeTypes.CharConstantNode;
import parseTree.nodeTypes.ContinueNode;
import parseTree.nodeTypes.DeclarationForNode;
import parseTree.nodeTypes.ArrayConstantNode;
import parseTree.nodeTypes.ArrayExpressionNode;
import parseTree.nodeTypes.AssignmentForNode;
import parseTree.nodeTypes.AssignmentNode;
import parseTree.nodeTypes.BlockNode;
import parseTree.nodeTypes.DeclarationNode;
import parseTree.nodeTypes.ErrorNode;
import parseTree.nodeTypes.ExpressionListNode;
import parseTree.nodeTypes.FloatingConstantNode;
import parseTree.nodeTypes.ForNode;
import parseTree.nodeTypes.FunctionBlockNode;
import parseTree.nodeTypes.FunctionInvocationNode;
import parseTree.nodeTypes.FunctionNode;
import parseTree.nodeTypes.IdentifierNode;
import parseTree.nodeTypes.IfBlockNode;
import parseTree.nodeTypes.IfNode;
import parseTree.nodeTypes.IntegerConstantNode;
import parseTree.nodeTypes.LoopBlockNode;
import parseTree.nodeTypes.NewlineNode;
import parseTree.nodeTypes.OperatorNode;
import parseTree.nodeTypes.ParameterListNode;
import parseTree.nodeTypes.ParanthesisNode;
import parseTree.nodeTypes.PrintStatementNode;
import parseTree.nodeTypes.ProcedureBlockNode;
import parseTree.nodeTypes.ProgramNode;
import parseTree.nodeTypes.RangeConstantNode;
import parseTree.nodeTypes.ReturnNode;
import parseTree.nodeTypes.SpaceNode;
import parseTree.nodeTypes.StringConstantNode;
import parseTree.nodeTypes.TabNode;
import parseTree.nodeTypes.TypeNode;
import parseTree.nodeTypes.WhileNode;

// Visitor pattern with pre- and post-order visits
public interface ParseNodeVisitor {
	
	// non-leaf nodes: visitEnter and visitLeave
	void visitEnter(OperatorNode node);
	void visitLeave(OperatorNode node);
	
	void visitEnter(BlockNode node);
	void visitLeave(BlockNode node);
	
	void visitEnter(FunctionBlockNode node);
	void visitLeave(FunctionBlockNode node);
	
	void visitEnter(ProcedureBlockNode node);
	void visitLeave(ProcedureBlockNode node);
	
	void visitEnter(ParanthesisNode node);
	void visitLeave(ParanthesisNode node);
	
	void visitEnter(ParameterListNode node);
	void visitLeave(ParameterListNode node);
	
	void visitEnter(LoopBlockNode node);
	void visitLeave(LoopBlockNode node);
	
	void visitEnter(IfBlockNode node);
	void visitLeave(IfBlockNode node);

	void visitEnter(DeclarationNode node);
	void visitLeave(DeclarationNode node);
	
	void visitEnter(DeclarationForNode node);
	void visitLeave(DeclarationForNode node);
	
	void visitEnter(ArrayExpressionNode node);
	void visitLeave(ArrayExpressionNode node);
	
	void visitEnter(ExpressionListNode node);
	void visitLeave(ExpressionListNode node);
	
	void visitEnter(IfNode node);
	void visitLeave(IfNode node);
	
	void visitEnter(WhileNode node);
	void visitLeave(WhileNode node);
	
	void visitEnter(ForNode node);
	void visitLeave(ForNode node);
	
	void visitEnter(ReturnNode node);
	void visitLeave(ReturnNode node);
	
	void visitEnter(AssignmentNode node);
	void visitLeave(AssignmentNode node);
	
	void visitEnter(AssignmentForNode node);
	void visitLeave(AssignmentForNode node);
	
	void visitEnter(TypeNode node);
	void visitLeave(TypeNode node);
	
	void visitEnter(IdentifierNode node);

	
	void visitEnter(ParseNode node);
	void visitLeave(ParseNode node);
	
	void visitEnter(PrintStatementNode node);
	void visitLeave(PrintStatementNode node);
	
	void visitEnter(ProgramNode node);
	void visitLeave(ProgramNode node);
	
	void visitEnter(FunctionNode node);
	void visitLeave(FunctionNode node);
	
	void visitEnter(FunctionInvocationNode node);
	void visitLeave(FunctionInvocationNode node);


	// leaf nodes: visitLeaf only
	void visit(BooleanConstantNode node);
	void visit(ErrorNode node);
	void visit(IdentifierNode node);
	void visit(IntegerConstantNode node);
	void visit(FloatingConstantNode node);
	void visit(NewlineNode node);
	void visit(SpaceNode node);
	void visit(TabNode node);
	void visit(CharConstantNode node);
	void visit(StringConstantNode node);
	void visit(TypeNode node);
	void visit(BreakNode node);
	void visit(ContinueNode node);
	void visit(DeclarationForNode node);
	void visit(OperatorNode node);
	void visit(ArrayConstantNode node);

	
	public static class Default implements ParseNodeVisitor
	{
		public void defaultVisit(ParseNode node) {	}
		public void defaultVisitEnter(ParseNode node) {
			defaultVisit(node);
		}
		public void defaultVisitLeave(ParseNode node) {
			defaultVisit(node);
		}		
		public void defaultVisitForLeaf(ParseNode node) {
			defaultVisit(node);
		}
		
		public void visitEnter(OperatorNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(OperatorNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(ParameterListNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(ParameterListNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(ArrayExpressionNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(ArrayExpressionNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(ExpressionListNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(ExpressionListNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(IdentifierNode node) {
			defaultVisitEnter(node);
		}
		public void visitEnter(DeclarationNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(DeclarationNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(TypeNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(TypeNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(IfNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(IfNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(ParanthesisNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(ParanthesisNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(WhileNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(WhileNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(DeclarationForNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(DeclarationForNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(ForNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(ForNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(AssignmentNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(AssignmentNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(AssignmentForNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(AssignmentForNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(BlockNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(BlockNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(ReturnNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(ReturnNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(FunctionBlockNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(FunctionBlockNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(ProcedureBlockNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(ProcedureBlockNode node) {
			defaultVisitLeave(node);
		}	
		public void visitEnter(LoopBlockNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(LoopBlockNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(IfBlockNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(IfBlockNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(ParseNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(ParseNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(PrintStatementNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(PrintStatementNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(ProgramNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(ProgramNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(FunctionNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(FunctionNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(FunctionInvocationNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(FunctionInvocationNode node) {
			defaultVisitLeave(node);
		}
		
		

		public void visit(BooleanConstantNode node) {
			defaultVisitForLeaf(node);
		}
		public void visit(ErrorNode node) {
			defaultVisitForLeaf(node);
		}
		public void visit(BreakNode node) {
			defaultVisitForLeaf(node);
		}
		public void visit(IdentifierNode node) {
			defaultVisitForLeaf(node);
		}
		public void visit(IntegerConstantNode node) {
			defaultVisitForLeaf(node);
		}
		public void visit(FloatingConstantNode node) {
			defaultVisitForLeaf(node);
		}
		public void visit(CharConstantNode node) {
			defaultVisitForLeaf(node);
		}
		public void visit(StringConstantNode node) {
			defaultVisitForLeaf(node);
		}
		public void visit(RangeConstantNode node) {
			defaultVisitForLeaf(node);
		}
		public void visit(NewlineNode node) {
			defaultVisitForLeaf(node);
		}	
		public void visit(SpaceNode node) {
			defaultVisitForLeaf(node);
		}
		public void visit(TabNode node) {
			defaultVisitForLeaf(node);
		}
		public void visit(TypeNode node) {
			defaultVisitForLeaf(node);
		}
		public void visit(ContinueNode node) {
			defaultVisitForLeaf(node);
		}
		public void visit(DeclarationForNode node) {
			defaultVisitForLeaf(node);
		}
		public void visit(OperatorNode node) {
			defaultVisitForLeaf(node);
		}
		public void visit(ArrayConstantNode node) {
			defaultVisitForLeaf(node);
		}
	}


	void visit(RangeConstantNode rangeConstantNode);



}
