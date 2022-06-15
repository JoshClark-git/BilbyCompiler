package asmCodeGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import asmCodeGenerator.codeStorage.ASMCodeFragment;
import asmCodeGenerator.codeStorage.ASMOpcode;
import asmCodeGenerator.operators.SimpleCodeGenerator;
import asmCodeGenerator.runtime.MemoryManager;
import asmCodeGenerator.runtime.RunTime;
import lexicalAnalyzer.Keyword;
import lexicalAnalyzer.Lextant;
import lexicalAnalyzer.Punctuator;
import parseTree.*;
import parseTree.nodeTypes.BooleanConstantNode;
import parseTree.nodeTypes.BreakNode;
import parseTree.nodeTypes.CharConstantNode;
import parseTree.nodeTypes.ContinueNode;
import parseTree.nodeTypes.DeclarationForNode;
import parseTree.nodeTypes.ArrayExpressionNode;
import parseTree.nodeTypes.AssignmentNode;
import parseTree.nodeTypes.AssignmentForNode;
import parseTree.nodeTypes.BlockNode;
import parseTree.nodeTypes.DeclarationNode;
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
import parseTree.nodeTypes.ParameterNode;
import parseTree.nodeTypes.ParanthesisNode;
import parseTree.nodeTypes.PrintStatementNode;
import parseTree.nodeTypes.ProgramNode;
import parseTree.nodeTypes.ReturnNode;
import parseTree.nodeTypes.SpaceNode;
import parseTree.nodeTypes.StringConstantNode;
import parseTree.nodeTypes.TabNode;
import parseTree.nodeTypes.TypeNode;
import parseTree.nodeTypes.WhileNode;
import semanticAnalyzer.signatures.FunctionSignature;
import semanticAnalyzer.signatures.Promotion;
import semanticAnalyzer.signatures.PromotionSignature;
import semanticAnalyzer.types.PrimitiveType;
import semanticAnalyzer.types.Type;
import symbolTable.Binding;
import symbolTable.Scope;
import tokens.IntegerConstantToken;
import tokens.Token;

import static asmCodeGenerator.codeStorage.ASMCodeFragment.CodeType.*;
import static asmCodeGenerator.codeStorage.ASMOpcode.*;
import static semanticAnalyzer.types.PrimitiveType.INTEGER;


// do not call the code generator if any errors have occurred during analysis.
public class ASMCodeGenerator {
	ParseNode root;
	int updateSP = 0;
	
	public static ASMCodeFragment generate(ParseNode syntaxTree) {
		ASMCodeGenerator codeGenerator = new ASMCodeGenerator(syntaxTree);
		return codeGenerator.makeASM();
	}
	public ASMCodeGenerator(ParseNode root) {
		super();
		this.root = root;
	}
	public ASMCodeFragment updateFP(int offset) {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_ADDRESS);
		frag.add(PushD, RunTime.FRAME_POINTER);
		frag.add(LoadI);
		frag.add(PushI,offset);
		frag.add(Subtract);
		return frag;
	}
	public ASMCodeFragment makeASM() {
		ASMCodeFragment code = new ASMCodeFragment(GENERATES_VOID);
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_ADDRESS);
		frag.add(Memtop);
		
		code.add(DLabel, RunTime.FRAME_POINTER);
		code.add(DataI, 0);
		code.add(PushD, RunTime.FRAME_POINTER);
		code.append(frag);
		code.add(StoreI);
		
		
		code.append( RunTime.getEnvironment() );
		code.append( globalVariableBlockASM() );
		code.append( programASM() );
		code.append( MemoryManager.codeForAfterApplication() );
		return code;
	}
	private ASMCodeFragment globalVariableBlockASM() {
		assert root.hasScope();
		Scope scope = root.getScope();
		int globalBlockSize = scope.getAllocatedSize();
		
		ASMCodeFragment code = new ASMCodeFragment(GENERATES_VOID);
		code.add(DLabel, RunTime.GLOBAL_MEMORY_BLOCK);
		code.add(DataZ, globalBlockSize);
		
		return code;
	}
	private ASMCodeFragment programASM() {
		ASMCodeFragment code = new ASMCodeFragment(GENERATES_VOID);
		
		code.add(    Label, RunTime.MAIN_PROGRAM_LABEL);
		code.append(MemoryManager.codeForInitialization());
		code.append( programCode());
		code.add(    Halt );
		
		return code;
	}
	private ASMCodeFragment programCode() {
		CodeVisitor visitor = new CodeVisitor();
		root.accept(visitor);
		return visitor.removeRootCode(root);
	}


	protected class CodeVisitor extends ParseNodeVisitor.Default {
		private Map<ParseNode, ASMCodeFragment> codeMap;
		ASMCodeFragment code;
		
		public CodeVisitor() {
			codeMap = new HashMap<ParseNode, ASMCodeFragment>();
		}


		////////////////////////////////////////////////////////////////////
        // Make the field "code" refer to a new fragment of different sorts.
		private void newAddressCode(ParseNode node) {
			code = new ASMCodeFragment(GENERATES_ADDRESS);
			codeMap.put(node, code);
		}
		private void newValueCode(ParseNode node) {
			code = new ASMCodeFragment(GENERATES_VALUE);
			codeMap.put(node, code);
		}
		private void newVoidCode(ParseNode node) {
			code = new ASMCodeFragment(GENERATES_VOID);
			codeMap.put(node, code);
		}

	    ////////////////////////////////////////////////////////////////////
        // Get code from the map.
		private ASMCodeFragment getAndRemoveCode(ParseNode node) {
			ASMCodeFragment result = codeMap.get(node);
			codeMap.remove(node);
			return result;
		}
	    public  ASMCodeFragment removeRootCode(ParseNode tree) {
			return getAndRemoveCode(tree);
		}
	    
	    private ASMCodeFragment getCode(ParseNode node) {
			ASMCodeFragment result = codeMap.get(node);
			return result;
		}
	    
	    ASMCodeFragment getValueCode(ParseNode node) {
	    	ASMCodeFragment frag = getCode(node);
	    	makeFragmentValueCode(frag, node);
	    	return frag;
	    }
		ASMCodeFragment removeValueCode(ParseNode node) {
			ASMCodeFragment frag = getAndRemoveCode(node);
			makeFragmentValueCode(frag, node);
			return frag;
		}
		private ASMCodeFragment getAddressCode(ParseNode node) {
			ASMCodeFragment frag = getCode(node);
			assert frag.isAddress();
			return frag;
		}		
		private ASMCodeFragment removeAddressCode(ParseNode node) {
			ASMCodeFragment frag = getAndRemoveCode(node);
			//assert frag.isAddress();
			return frag;
		}		
		ASMCodeFragment removeVoidCode(ParseNode node) {
			ASMCodeFragment frag = getAndRemoveCode(node);
			//assert frag.isVoid();
			return frag;
		}
		
	    ////////////////////////////////////////////////////////////////////
        // convert code to value-generating code.
		private void makeFragmentValueCode(ASMCodeFragment code, ParseNode node) {
			assert !code.isVoid();
			if(code.isAddress()) {
				turnAddressIntoValue(code, node);
			}	
		}
		private void turnAddressIntoValue(ASMCodeFragment code, ParseNode node) {
			if(node.getType() == PrimitiveType.INTEGER) {
				code.add(LoadI);
			}
			else if(node.getType() == PrimitiveType.CHARACTER) {
				// Range Casting
				if(node.getToken().isLextant(Keyword.RANGE)) {
					return;
				}
				code.add(LoadC);
			}	
			else if(node.getType() == PrimitiveType.FLOATING) {
				code.add(LoadF);
			}	
			else if(node.getType() == PrimitiveType.BOOLEAN) {
				code.add(LoadC);
			}
			else if(node.getType() == PrimitiveType.STRING) {
				code.add(LoadI);
			}	
			else {
				//assert false : "node " + node;
				code.add(LoadI);
			}
			code.markAsValue();
		}
		
	    ////////////////////////////////////////////////////////////////////
        // ensures all types of ParseNode in given AST have at least a visitLeave	
		public void visitLeave(ParseNode node) {
			assert false : "node " + node + " not handled in ASMCodeGenerator";
		}
		
		
		
		///////////////////////////////////////////////////////////////////////////
		// constructs larger than statements
		public void visitLeave(ProgramNode node) {
			newVoidCode(node);
			for(ParseNode child : node.getChildren()) {	
				ASMCodeFragment childCode = removeVoidCode(child);
				code.append(childCode);
			}
		}
		
		public void visitLeave(FunctionNode node) {
			newVoidCode(node);
			Labeller labeller = new Labeller("beingCalled");
			String joinLabel = labeller.newLabel("join");
			code.add(Label, node.getToken().getLexeme());
			code.add(PushD, RunTime.STACK_POINTER);
			code.add(LoadI);
			code.add(PushD, RunTime.FRAME_POINTER);
			code.add(LoadI);
			code.add(Subtract);
			code.add(JumpFalse, joinLabel);
			
			code.add(PushD, RunTime.STACK_POINTER);
			code.append(updateSP(4));
			code.add(StoreI);
				
			code.add(PushD, RunTime.STACK_POINTER);
			code.add(LoadI);
			code.add(Exchange);
			code.add(StoreI);
			code.add(PushD, RunTime.STACK_POINTER);
			code.append(updateSP(node.child(3).getScope().getAllocatedSize() - node.getScope().getAllocatedSize()));
			code.add(StoreI);
			
			ASMCodeFragment block = removeValueCode(node.child(3));
			code.append(block);
			code.add(PushD, RunTime.FRAME_POINTER);
			code.add(LoadI);
			code.add(PushI, 8);
			code.add(Subtract);
			code.add(LoadI);
			code.add(Return);
			code.add(Label, joinLabel);	
			//System.out.println(code);
		}
		public void visitLeave(BlockNode node) {
			newVoidCode(node);
			for(ParseNode child : node.getChildren()) {
				ASMCodeFragment childCode = removeVoidCode(child);
				code.append(childCode);
			}
		}
		
		public void visitLeave(FunctionBlockNode node) {
			newValueCode(node);
			for(ParseNode child : node.getChildren()) {			
				ASMCodeFragment childCode = removeVoidCode(child);
				code.append(childCode);
				if(child instanceof ReturnNode) {
					break;
				}
			}
		}
		
		public void visitLeave(ReturnNode node) {
			Labeller labeller = new Labeller("beingCalled");
			String joinLabel = labeller.newLabel("join");
			newValueCode(node);
			if(node.nChildren() == 0) {
				return;
			}
			ASMCodeFragment childCode = removeValueCode(node.child(0));
			code.append(childCode);
			code.add(PushD, RunTime.FRAME_POINTER);
			code.add(LoadI);
			code.add(PushI, 8);
			code.add(Subtract);
			code.add(LoadI);
			
			code.add(Return);
			//childCode.add(Return);
			//childCode.add(Label, joinLabel);	
			//code.add(opcodeForStore(node.child(0).getType()));
			
		}
		public void visitLeave(ParameterListNode node) {
			
		}
		public void visitLeave(LoopBlockNode node) {
			int i = 0;
			Labeller labeller = new Labeller("LoopBlock");
			String joinLabel = labeller.newLabel("join");
			String continueLabel = labeller.newLabel("continue");
			String breakLabel = labeller.newLabel("break");
			newVoidCode(node);
			code.add(PushI, 1);
			for(ParseNode child : node.getChildren()) {
				ASMCodeFragment childCode = removeVoidCode(child);
				code.append(childCode);
				code.add(Duplicate);
				code.add(JumpNeg, continueLabel);
				code.add(Duplicate);
				code.add(JumpFalse, continueLabel);
				
			}
			
			code.add(Jump, joinLabel);
			code.add(Label, continueLabel);
			code.add(Exchange);
			code.add(Pop);
			code.add(Exchange);
			code.add(Pop);
			code.add(Jump, joinLabel);
			
			code.add(Label, joinLabel);
		}
		
		public void visitLeave(IfBlockNode node) {
			ParseNode parent = node.getParent();
			while(!(parent instanceof LoopBlockNode)) {
				if(parent instanceof ProgramNode) {
					break;
				}
				parent = parent.getParent();
			}
			if(parent instanceof LoopBlockNode) {
				loopIf(node);
			}
			else {
				normalIf(node);
			}
		}
		
		public void loopIf(ParseNode node) {
			int i = 0;
			Labeller labeller = new Labeller("LoopIf");
			String joinLabel = labeller.newLabel("join");
			String continueLabel = labeller.newLabel("continue");
			newVoidCode(node);
			code.add(PushI, 1);
			for(ParseNode child : node.getChildren()) {
				i = i + 1;
				ASMCodeFragment childCode = removeVoidCode(child);
				code.append(childCode);
				code.add(Duplicate);
				code.add(JumpNeg, continueLabel);
				code.add(Duplicate);
				code.add(JumpFalse, continueLabel);
			}
			code.add(Label, continueLabel);
			code.add(Jump, joinLabel);
			//code.add(PushI, i - 1);
			//code.add(Duplicate);
			//code.add(JumpFalse, joinLabel);
			
			code.add(Label, joinLabel);
		}
		public void normalIf(ParseNode node) {
			newVoidCode(node);
			for(ParseNode child : node.getChildren()) {
				ASMCodeFragment childCode = removeVoidCode(child);
				code.append(childCode);
			}
		}

		///////////////////////////////////////////////////////////////////////////
		// statements and declarations

		public void visitLeave(PrintStatementNode node) {
			newVoidCode(node);
			new PrintStatementGenerator(code, this).generate(node);	
		}
		public void visit(NewlineNode node) {
			newVoidCode(node);
			code.add(PushD, RunTime.NEWLINE_PRINT_FORMAT);
			code.add(Printf);
		}
		public void visit(SpaceNode node) {
			newVoidCode(node);
			code.add(PushD, RunTime.SPACE_PRINT_FORMAT);
			code.add(Printf);
		}
		public void visit(TabNode node) {
			newVoidCode(node);
			code.add(PushD, RunTime.TAB_PRINT_FORMAT);
			code.add(Printf);
		}
		
		public void visitLeave(ParanthesisNode node) {
			newValueCode(node);
			ASMCodeFragment childValue = removeValueCode(node.child(0));
			code.append(childValue);
			
		}
		public void visitLeave(DeclarationNode node) {
			if(node.getToken().isLextant(Keyword.FOR)) {
				return;
			}
			newVoidCode(node);
			ASMCodeFragment lvalue = removeAddressCode(node.child(0));	
			ASMCodeFragment rvalue = removeValueCode(node.child(1));

			code.append(lvalue);
			code.append(rvalue);
	
			Type type = node.getType().concreteType();
			
			//Range Casting
			if(node.getToken().isLextant(Keyword.RANGE)) {
				return;
			}
			code.add(opcodeForStore(type));
		}
		public void visitLeave(AssignmentNode node) {
			newAddressCode(node);
			ASMCodeFragment lvalue = null;
			ASMCodeFragment rvalue = null;
			
			if((node.child(0).getType().isArray()) && (node.child(1).getToken().isLextant(Punctuator.INDEXING_ASSIGN))) {
				lvalue = removeAddressCode(node.child(1));
				code.append(lvalue);
				return;
			}
			else {
				//visit((IdentifierNode) node.child(0));
				lvalue = removeAddressCode(node.child(0));
				rvalue = removeValueCode(node.child(1));
			}
			
			//Promotions
			if(node.child(0).getType() == PrimitiveType.FLOATING) {
				if((node.child(1).getType() == PrimitiveType.INTEGER) || (node.child(1).getType() == PrimitiveType.CHARACTER)) {
					ASMCodeFragment result = new ASMCodeFragment(ASMCodeFragment.CodeType.GENERATES_VALUE);
					result.add(ConvertF);
					rvalue.append(result);
				}
			}
			code.append(lvalue);
			code.append(rvalue);
			
			Type type = node.getType();
			if(node.child(1).getToken().isLextant(Punctuator.INDEXING_ASSIGN)) {
				return;
			}
			code.add(opcodeForStore(type));
		}
		public void visitLeave(AssignmentForNode node) {
			newAddressCode(node);
			ASMCodeFragment lvalue = null;
			ASMCodeFragment rvalue = null;
			
			if((node.child(0).getType().isArray()) && (node.child(1).getToken().isLextant(Punctuator.INDEXING_ASSIGN))) {
				lvalue = removeAddressCode(node.child(1));
				code.append(lvalue);
				return;
			}
			else {
				//visit((IdentifierNode) node.child(0));
				lvalue = removeAddressCode(node.child(0));
				rvalue = removeValueCode(node.child(1));
			}
			
			//Promotions
			if(node.child(0).getType() == PrimitiveType.FLOATING) {
				if((node.child(1).getType() == PrimitiveType.INTEGER) || (node.child(1).getType() == PrimitiveType.CHARACTER)) {
					ASMCodeFragment result = new ASMCodeFragment(ASMCodeFragment.CodeType.GENERATES_VALUE);
					result.add(ConvertF);
					rvalue.append(result);
				}
			}
			code.append(lvalue);
			code.append(rvalue);
			
			Type type = node.getType();
			if(node.child(1).getToken().isLextant(Punctuator.INDEXING_ASSIGN)) {
				return;
			}
			code.add(opcodeForStore(type));
		}

		
		public void visitLeave(TypeNode node) {
			newValueCode(node);
			if(node.getParent().getToken().isLextant(Punctuator.OPEN_SQUARE)) {
				if(node.isArray() || node.getType().equals(PrimitiveType.STRING)) {
					code.add(PushI, 4);
				}
				else {
					code.add(PushI,0);
				}
				code.add(PushI, node.getType().atomicType().getSize());
				
				
			}
			else {
				//code.add(PushI, 0);
			}
		}
		
		private ASMOpcode opcodeForStore(Type type) {
			if(type == PrimitiveType.INTEGER) {
				return StoreI;
			}
			if(type == PrimitiveType.CHARACTER) {
				return StoreC;
			}
			if(type == PrimitiveType.FLOATING) {
				return StoreF;
			}
			if(type == PrimitiveType.BOOLEAN) {
				return StoreC;
			}
			if(type == PrimitiveType.STRING) {
				return StoreI;
			}
			if(type == PrimitiveType.VOID) {
				return Nop;
			}
			//For Arrays (Assuming all types have been implemented)
			return StoreI;
			//assert false: "Type " + type + " unimplemented in opcodeForStore()";
			//return null;
		}


		///////////////////////////////////////////////////////////////////////////
		// expressions
		/*
		public void visitEnter(FunctionInvocationNode node) {
			
			code.add(PushD, RunTime.STACK_POINTER);
			code.append(updateSP(4));
			code.add(StoreI);
			
			code.add(PushD, RunTime.STACK_POINTER);
			code.add(LoadI);
			
			code.add(PushD, RunTime.FRAME_POINTER);
			code.add(LoadI);
			
			code.add(StoreI);
		}
		*/
		public void callFunctionSetup() {
			code.add(PushD, RunTime.FRAME_POINTER);
			code.add(LoadI);
			
			code.add(PushD, RunTime.STACK_POINTER);
			code.append(updateSP(4));
			code.add(StoreI);
		
			
			code.add(PushD, RunTime.STACK_POINTER);
			code.add(LoadI);
			code.add(Exchange);
			code.add(StoreI);
		
			
			/*
			code.add(PushD, RunTime.STACK_POINTER);
			code.append(updateSP(4));
			code.add(StoreI);
		
			
			code.add(PushD, RunTime.STACK_POINTER);
			code.add(LoadI);
			code.add(Exchange);
			code.add(StoreI);
			
			*/
			code.add(PushD, RunTime.FRAME_POINTER);
			code.append(getSP());
			code.add(StoreI);
			
		}
		public ASMCodeFragment callFunction(FunctionNode node) {
			ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VALUE);
			frag.add(Call, node.getToken().getLexeme());
			return frag;
		}
		
		public ASMCodeFragment postFunction(FunctionNode node) {
			ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);
			
			frag.add(PushD, RunTime.FRAME_POINTER);
			frag.append(restoreFP());
			frag.add(StoreI);
			
			
			frag.add(PushD, RunTime.STACK_POINTER);
			frag.append(updateSP(-node.child(3).getScope().getAllocatedSize()));
			frag.add(StoreI);
			
			frag.add(PushD, RunTime.STACK_POINTER);
			frag.append(updateSP(node.child(0).getType().getSize()));
			frag.add(StoreI);
			
			Labeller labeller = new Labeller("funcStoreReturn");
			String joinLabel = labeller.newLabel("join");
			
			frag.add(PushI, node.child(0).getType().getSize());
			frag.add(PushI, 0);
			frag.add(Subtract);
			frag.add(JumpFalse, joinLabel);
			
			
			frag.add(PushD, RunTime.STACK_POINTER);
			frag.add(LoadI);
	
			frag.add(Exchange);
			//frag.add(StoreI);
			
			String charLabel = labeller.newLabel("charLoad");
			String intLabel = labeller.newLabel("intLoad");
			String floatLabel = labeller.newLabel("floatLoad");
			String voidLabel = labeller.newLabel("voidLoad");
			frag.add(PushI, node.child(0).getType().getSize());
			frag.add(Duplicate);
			
			frag.add(PushI, 1);
			
			frag.add(Subtract);
			frag.add(JumpNeg, voidLabel);
			frag.add(Duplicate);
			
			frag.add(PushI, 1);
			
			frag.add(Subtract);
			frag.add(JumpFalse, charLabel);
			frag.add(Duplicate);
			
			frag.add(PushI, 4);
			
			frag.add(Subtract);
			frag.add(JumpFalse, intLabel);
			frag.add(Jump, floatLabel);
			
			frag.add(Label, voidLabel);
			frag.add(Pop);
			frag.add(Pop);
			frag.add(Jump, joinLabel);
			
			frag.add(Label, charLabel);
			frag.add(Pop);
			frag.add(StoreC);
			frag.add(Jump, joinLabel);
			
			frag.add(Label, intLabel);
			frag.add(Pop);
			frag.add(StoreI);
			frag.add(Jump, joinLabel);
			
			frag.add(Label, floatLabel);
			frag.add(Pop);
			frag.add(StoreF);
			frag.add(Jump, joinLabel);
			
			frag.add(Label, joinLabel);
			
			return frag;
		}
		
		private ASMCodeFragment restoreFP() {
			ASMCodeFragment frag = new ASMCodeFragment(GENERATES_ADDRESS);
			frag.add(PushD, RunTime.FRAME_POINTER);
			frag.add(LoadI);
			frag.add(PushI, 4);
			frag.add(Subtract);
			frag.add(LoadI);
			return frag;
		}
		
		public void visitLeave(FunctionInvocationNode node) {
			updateSP = 1;
			newValueCode(node);
			code.append(removeVoidCode(node.child(1)));
			
			FunctionNode funcNode = findFuncIdentifier(node,node.child(0).getToken().toString());
			
			//code.add(PushD, RunTime.FRAME_POINTER);
			//code.add(LoadI);


			callFunctionSetup();
			
			code.append(callFunction(funcNode));
			
			
			//code.add(Call, funcNode.getToken().getLexeme());
			
			
			code.append(postFunction(funcNode));
			
			// Remove Addr
			//code.add(Pop);
			code.add(PushD, RunTime.STACK_POINTER);
			code.add(LoadI);
			
			
			Labeller labeller = new Labeller("funcLoadReturn");
			String joinLabel = labeller.newLabel("join");
			String charLabel = labeller.newLabel("charLoad");
			String intLabel = labeller.newLabel("intLoad");
			String floatLabel = labeller.newLabel("floatLoad");
			String voidLabel = labeller.newLabel("voidLoad");
			code.add(PushI, funcNode.child(0).getType().getSize());
			code.add(Duplicate);
			
			code.add(PushI, 1);
			
			code.add(Subtract);
			code.add(JumpNeg, voidLabel);
			code.add(Duplicate);
			
			code.add(PushI, 1);
			
			code.add(Subtract);
			code.add(JumpFalse, charLabel);
			code.add(Duplicate);
			
			code.add(PushI, 4);
			
			code.add(Subtract);
			code.add(JumpFalse, intLabel);
			code.add(Jump, floatLabel);
			
			code.add(Label, voidLabel);
			code.add(Pop);
			code.add(Jump, joinLabel);
			
			code.add(Label, charLabel);
			code.add(Pop);
			code.add(LoadC);
			code.add(Jump, joinLabel);
			
			code.add(Label, intLabel);
			code.add(Pop);
			code.add(LoadI);
			code.add(Jump, joinLabel);
			
			code.add(Label, floatLabel);
			code.add(Pop);
			code.add(LoadF);
			code.add(Jump, joinLabel);
			
			code.add(Label, joinLabel);
			
			
			
			//code.add(Call, node.child(0).getToken().getLexeme());
			
		}
		
		public void visitLeave(ExpressionListNode node) {
			newVoidCode(node);
			//ASMCodeFragment frameSetup = removeVoidCode(node.getParent());
			//code.append(frameSetup);
			//ParseNode identifier = node.getParent().child(0);
			//ASMCodeFragment idCode = removeAddressCode(identifier);
			//code.append(idCode);
			//code.add(Duplicate);
			//setupStackReturn(code);
			for(ParseNode child:node.getChildren()) {
				/*
				code.add(PushD, RunTime.STACK_POINTER);
				code.add(LoadI);
				code.add(Pop);
				
				code.add(PushD, RunTime.FRAME_POINTER);
				code.add(LoadI);
				code.add(Pop);
				*/
				
				code.add(PushD, RunTime.STACK_POINTER);
				code.append(updateSP(child.getType().getSize()));
				code.add(StoreI);
				
				code.add(PushD, RunTime.STACK_POINTER);
				code.add(LoadI);
				
				ASMCodeFragment childCode = removeValueCode(child);
				code.append(childCode);
				code.add(opcodeForStore(child.getType()));
			}
		}
		private void setupStackReturn(ASMCodeFragment code) {
			code.add(PushD, RunTime.STACK_POINTER);
			code.append(updateSP(4));
			code.add(StoreI);
			
			code.add(PushD, RunTime.STACK_POINTER);
			code.add(LoadI);
			code.add(Exchange);
			code.add(StoreI);
		}
		public ASMCodeFragment updateSP(int offset) {
			ASMCodeFragment frag = new ASMCodeFragment(GENERATES_ADDRESS);
			frag.add(PushD, RunTime.STACK_POINTER);
			frag.add(LoadI);
			frag.add(PushI,offset);
			frag.add(Subtract);
			return frag;
		}
		public ASMCodeFragment getSP() {
			ASMCodeFragment frag = new ASMCodeFragment(GENERATES_ADDRESS);
			frag.add(PushD, RunTime.STACK_POINTER);
			frag.add(LoadI);
			frag.add(PushI, 4);
			frag.add(Add);
			return frag;
		}
		
		
		
		private FunctionNode findFuncIdentifier(ParseNode node, String token) {
			while(!(node instanceof ProgramNode)) {
				node = node.getParent();
			}
			for(ParseNode child : node.getChildren()) {
				if(child.getToken().toString().equals(token)) {
					return (FunctionNode) child;
				}
			}
			return null;
			
		}
		
		public void visitLeave(ArrayExpressionNode node) {
			newValueCode(node);
			Object variant = node.getPromotionSignature().get(0).getSignature().getVariant();
			SimpleCodeGenerator generator = (SimpleCodeGenerator)variant;
			List<ASMCodeFragment> args = new ArrayList<>();
			List<PromotionSignature> promotionSignatures = node.getPromotionSignature();
			List<Promotion> promotion = new ArrayList<>();
			for(int i = 0; i < promotionSignatures.size();i++) {
				promotion.add(promotionSignatures.get(i).getPromotions().get(1));
			}
			
			int i = 0;
			for (ParseNode child:node.getChildren()) {
				ASMCodeFragment arg = removeValueCode(child);
				if(i < promotion.size()) {
					arg.append(promotion.get(i).codeFor());
				}
				args.add(arg);
				i++;
			}
			ASMCodeFragment generated = generator.generate(node, args);
			code.appendWithCodeType(generated);
			
		}
		public void visitLeave(ForNode node) {
			newValueCode(node);
			Object variant = node.getPromotionSignature().getSignature().getVariant();
			
			SimpleCodeGenerator generator = (SimpleCodeGenerator)variant;
			List<ASMCodeFragment> args = new ArrayList<>();
			ASMCodeFragment arg0 = removeVoidCode(node.child(0));
			args.add(arg0);
			ASMCodeFragment arg3 = removeVoidCode(node.child(1));
			args.add(arg3);
			ASMCodeFragment arg = removeValueCode(node.child(2));
			args.add(arg);
			ASMCodeFragment arg1 = removeVoidCode(node.child(3));
			args.add(arg1);
			ASMCodeFragment arg4 = removeVoidCode(node.child(4));
			args.add(arg4);
			
			ASMCodeFragment generated = generator.generate(node, args);
			code.appendWithCodeType(generated);
		}
		public void visitLeave(OperatorNode node) {
			newValueCode(node);
			Object variant = node.getPromotionSignature().getSignature().getVariant();
			List<Promotion> promotion = node.getPromotionSignature().getPromotions();
			FunctionSignature signature = node.getPromotionSignature().getSignature();
			
			if((node.getToken().isLextant(Keyword.IF)) || (node.getToken().isLextant(Keyword.WHILE))) {
				SimpleCodeGenerator generator = (SimpleCodeGenerator)variant;
				List<ASMCodeFragment> args = new ArrayList<>();
				ASMCodeFragment arg = removeValueCode(node.child(0));
				args.add(arg);
				ASMCodeFragment arg1 = removeVoidCode(node.child(1));
				args.add(arg1);
				ASMCodeFragment generated = generator.generate(node, args);
				code.appendWithCodeType(generated);
				
				return;
			
			}
			else if(node.getToken().isLextant(Keyword.ELSE)) {
				SimpleCodeGenerator generator = (SimpleCodeGenerator)variant;
				List<ASMCodeFragment> args = new ArrayList<>();
				ASMCodeFragment arg = removeValueCode(node.child(0));
				args.add(arg);
				ASMCodeFragment arg1 = removeVoidCode(node.child(1));
				args.add(arg1);
				ASMCodeFragment arg2 = removeVoidCode(node.child(2));
				args.add(arg2);
				
				ASMCodeFragment generated = generator.generate(node, args);
				code.appendWithCodeType(generated);
				
				return;
				
			}
			
			else if(node.getToken().isLextant(Keyword.ALLOC)) {
				SimpleCodeGenerator generator = (SimpleCodeGenerator)variant;
				List<ASMCodeFragment> args = new ArrayList<>();
				ASMCodeFragment arg = removeValueCode(node.child(0));
				args.add(arg);
				ASMCodeFragment arg1 = removeValueCode(node.child(1));
				args.add(arg1);
				ASMCodeFragment arg2 = removeValueCode(node.child(0).child(0));
				args.add(arg2);
				ASMCodeFragment generated = generator.generate(node, args);
				code.appendWithCodeType(generated);
				
				return;
			}
			else if(node.getToken().isLextant(Punctuator.INDEXING)) {
				SimpleCodeGenerator generator = (SimpleCodeGenerator)variant;
				List<ASMCodeFragment> args = new ArrayList<>();
				if(node.nChildren() == 3) {
					ASMCodeFragment arg = removeValueCode(node.child(2));
					args.add(arg);
				}
				else {
					ASMCodeFragment arg = removeValueCode(node.child(0));
					args.add(arg);
				}
				ASMCodeFragment arg1 = removeValueCode(node.child(1));
				args.add(arg1);
				ASMCodeFragment generated = generator.generate(node, args);
				code.appendWithCodeType(generated);
				
				return;
			}
			else if(node.getToken().isLextant(Punctuator.INDEXING_ASSIGN)) {
				SimpleCodeGenerator generator = (SimpleCodeGenerator)variant;
				List<ASMCodeFragment> args = new ArrayList<>();
			
				ASMCodeFragment arg = removeValueCode(node.child(0));
				args.add(arg);
				ASMCodeFragment arg1 = removeValueCode(node.child(1));
				args.add(arg1);
				ASMCodeFragment arg2 = removeValueCode(node.child(2));
				arg2.append(promotion.get(1).codeFor());
				args.add(arg2);
				ASMCodeFragment generated = generator.generate(node, args);
				code.appendWithCodeType(generated);
				
				
				return;
			}
			//Range/Array Casting
			/*
			else if((node.getToken().isLextant(Keyword.CAST)) && ((node.child(0).getType().isRange()) || (node.child(0).getType().isArray()))) {
				System.out.println(variant);
				SimpleCodeGenerator generator = (SimpleCodeGenerator)variant;
				System.out.println(generator);
				List<ASMCodeFragment> args = new ArrayList<>();
				ASMCodeFragment arg = removeValueCode(node.child(0));
				//arg.append(promotion.get(0).codeFor());
				args.add(arg);
				ASMCodeFragment generated = generator.generate(node, args);
				code.appendWithCodeType(generated);
				
				return;
			}
			*/
			if (variant.getClass().getSimpleName().toString().equals("RangeCastRangeCodeGenerator")){
				SimpleCodeGenerator generator = (SimpleCodeGenerator)variant;
				List<ASMCodeFragment> args = new ArrayList<>();
				ASMCodeFragment arg = removeValueCode(node.child(0));
				arg.append(promotion.get(0).codeFor());
				args.add(arg);
				ASMCodeFragment generated = generator.generate(node, args);
				System.out.println(generated);
				code.appendWithCodeType(generated);
			}
			else if (variant instanceof ASMOpcode) {
				int i = 0;
				for (ParseNode child:node.getChildren()) {
					ASMCodeFragment arg = removeValueCode(child);
					arg.append(promotion.get(i).codeFor());
					code.append(arg);
					i++;
				}
				code.add((ASMOpcode)variant);
			}
			else if (variant instanceof SimpleCodeGenerator){
				SimpleCodeGenerator generator = (SimpleCodeGenerator)variant;
				List<ASMCodeFragment> args = new ArrayList<>();
				int i = 0;
				for (ParseNode child:node.getChildren()) {
					ASMCodeFragment arg = removeValueCode(child);
					arg.append(promotion.get(i).codeFor());
					args.add(arg);
					i++;
				}
				ASMCodeFragment generated = generator.generate(node, args);
				code.appendWithCodeType(generated);
			}
			
			else {
				throw new RuntimeException("Variant unimplemented in ASMCodeGenerator.OperatorNode");
			}
		}
		///////////////////////////////////////////////////////////////////////////
		// leaf nodes (ErrorNode not necessary)
		public void visit(BooleanConstantNode node) {
			newValueCode(node);
			code.add(PushI, node.getValue() ? 1 : 0);
		}
		public void visit(IdentifierNode node) {
			newAddressCode(node);
			Binding binding = node.getBinding();
			binding.generateAddress(code);
		}
		public void visit(CharConstantNode node) {
			newValueCode(node);
			code.add(PushI, (int)node.getValue());
		}		
		public void visit(IntegerConstantNode node) {
			newValueCode(node);
			
			code.add(PushI, node.getValue());
		}
		public void visit(TypeNode node) {
			newValueCode(node);
		}
		public void visit(BreakNode node) {
			newValueCode(node);
			code.add(PushI, 0);
		}
		public void visit(ContinueNode node) {
			newValueCode(node);
			code.add(PushI, -1);
		}
		
		public void visit(FloatingConstantNode node) {
			newValueCode(node);
			code.add(PushF, node.getValue());
		}
		
		
		public void visit(StringConstantNode node) {
			newValueCode(node);
			
			
			String label = new Labeller("stringConstant").newLabel("");
			
			code.add(DLabel, label);
			code.add(DataI, 3);
			code.add(DataI, 9);
			code.add(DataI, node.getValue().length());
			code.add(DataS, node.getValue());

			code.add(PushD, label);
		}
	}

}
