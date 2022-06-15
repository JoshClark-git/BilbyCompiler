package asmCodeGenerator.operators;

import static asmCodeGenerator.codeStorage.ASMOpcode.*;

import java.util.List;

import asmCodeGenerator.Labeller;
import asmCodeGenerator.codeStorage.ASMCodeFragment;
import asmCodeGenerator.codeStorage.ASMCodeFragment.CodeType;
import asmCodeGenerator.codeStorage.ASMOpcode;
import asmCodeGenerator.runtime.MemoryManager;
import asmCodeGenerator.runtime.RunTime;
import lexicalAnalyzer.Keyword;
import parseTree.ParseNode;
import parseTree.nodeTypes.TypeNode;
import semanticAnalyzer.types.PrimitiveType;
import tokens.Token;

public class ArrayExpressionCodeGenerator implements SimpleCodeGenerator {
	
	
	public ArrayExpressionCodeGenerator() {
		super();
	}

	@Override
	public ASMCodeFragment generate(ParseNode node, List<ASMCodeFragment> args) {
		
		int i = 0;
		
		Labeller labeller = new Labeller("compare");
		
		String exception = RunTime.NEGATIVE_ARRAY_ALLOCATION_ERROR;
		
		String startLabel = labeller.newLabel("start");
		String trueLabel  = labeller.newLabel("true");
		String falseLabel = labeller.newLabel("false");
		String joinLabel  = labeller.newLabel("join");
		String whileLabel  = labeller.newLabel("while");
		String lengthLabel  = labeller.newLabel("length");
		String subTypeLabel  = labeller.newLabel("subType");
		String intwhileLabel  = labeller.newLabel("intwhile");
		String intLabel  = labeller.newLabel("int");
		String floatLabel  = labeller.newLabel("float");
		String floatwhileLabel  = labeller.newLabel("floatwhile");
		String charwhileLabel  = labeller.newLabel("charwhile");
		String charLabel  = labeller.newLabel("charw");
		
		ASMCodeFragment code = new ASMCodeFragment(CodeType.GENERATES_VALUE);
		
		int numChildren = node.nChildren();
		
		ParseNode lengthNode = node.child(numChildren-2);
		
		String lengthString = lengthNode.getToken().getLexeme();
		
		int length = Integer.parseInt(lengthString);
		
		Token subTypeToken = node.child(numChildren-1).getToken();
		
		String subType = subTypeToken.toString();
		
		int subTypeSize = node.getType().atomicType().getSize();
		
		
		int status = 0;
		
		if((node.getType().atomicType() == PrimitiveType.STRING) || (node.child(0).getType().isArray())) {
			status = 2;
		}
		
		code.add(PushI, subTypeSize);
		
		code.add(PushI, length);
		
		code.add(Multiply);
		
		code.add(PushI, 16);
		
		code.add(Add);
		
		code.add(Call, MemoryManager.MEM_MANAGER_ALLOCATE);
		
		code.add(Duplicate);
		
		// Setup Record
		
		code.add(Duplicate);
		
		code.add(PushI, 7);
		
		code.add(StoreI);
		
		code.add(Duplicate);
		
		code.add(PushI, 4);
		
		code.add(Add);
		
		code.add(PushI, status);
		
		code.add(StoreI);
		
		code.add(Duplicate);
		
		code.add(PushI, 8);
		
		code.add(Add);
		
		code.add(PushI, subTypeSize);
		
		code.add(StoreI);
		
		code.add(Duplicate);
		
		code.add(PushI, 12);
		
		code.add(Add);
		
		code.add(PushI, length);
		
		code.add(StoreI);
		
		code.add(PushI, 16);
		
		code.add(Add);
		
		//End Record setup, now onto Setting up Array
		
		//StoreI vs StoreF vs StoreC;
		
		code.add(PushI, subTypeSize);
		
		code.add(PushI, 1);
		
		code.add(Subtract);
		
		code.add(JumpFalse, charLabel);
		
		code.add(PushI, subTypeSize);
		
		code.add(PushI, 8);
		
		code.add(Subtract);
		
		code.add(JumpNeg, intLabel);
		
		code.add(Jump, floatLabel);
		
		code.add(Label, charLabel);
		
		while((subTypeSize == 1) && (i < length)){
			code.add(Duplicate);
			
			if(node.child(i).getToken().isLextant(Keyword.ALLOC)) {
				code.add(Duplicate);
			}
			
			
			code.append(args.get(i));
			
			code.add(StoreC);
			
			code.add(PushI, subTypeSize);
			
			code.add(Add);
			
			i = i + 1;
		}
		
		code.add(Jump, joinLabel);
		
		code.add(Label, intLabel);
		
		while((subTypeSize == 4) && (i < length)){
			
			code.add(Duplicate);
			
			if(node.child(i).getToken().isLextant(Keyword.ALLOC)) {
				code.add(Duplicate);
			}
			
			code.append(args.get(i));
			
			code.add(StoreI);
			
			code.add(PushI, subTypeSize);
			
			code.add(Add);
			
			i = i + 1;
		}
		
		code.add(Jump, joinLabel);
		
		code.add(Label, floatLabel);
		
		while((subTypeSize == 8) && (i < length)){
			code.add(Duplicate);
			
			if(node.child(i).getToken().isLextant(Keyword.ALLOC)) {
				code.add(Duplicate);
			}
			
			
			code.append(args.get(i));
			
			code.add(StoreF);
			
			code.add(PushI, subTypeSize);
			
			code.add(Add);
			
			i = i + 1;
		}
		
		code.add(Jump, joinLabel);
		
		code.add(Label, joinLabel);
		
		code.add(Pop);
		
		return code;
		
		
	}


}
