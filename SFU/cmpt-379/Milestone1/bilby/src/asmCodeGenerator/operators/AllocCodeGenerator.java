package asmCodeGenerator.operators;

import static asmCodeGenerator.codeStorage.ASMOpcode.*;

import java.util.List;

import asmCodeGenerator.Labeller;
import asmCodeGenerator.codeStorage.ASMCodeFragment;
import asmCodeGenerator.codeStorage.ASMCodeFragment.CodeType;
import asmCodeGenerator.codeStorage.ASMOpcode;
import asmCodeGenerator.runtime.MemoryManager;
import asmCodeGenerator.runtime.RunTime;
import parseTree.ParseNode;
import parseTree.nodeTypes.TypeNode;
import semanticAnalyzer.types.PrimitiveType;

public class AllocCodeGenerator implements SimpleCodeGenerator {
	
	
	public AllocCodeGenerator() {
		super();
	}

	@Override
	public ASMCodeFragment generate(ParseNode node, List<ASMCodeFragment> args) {
		
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
		
		ParseNode grandParent = node.getParent().getParent();
		if(grandParent.getType().isArray()) {
			code.add(Pop);
		}
		
		int subTypeSize = node.child(0).getType().atomicType().getSize();
		
		int status = 0;
		
		TypeNode grandChild = (TypeNode)node.child(0).child(0);
		
		if(grandChild.isArray() || grandChild.getType().concreteType().equals(PrimitiveType.STRING)) {
			status = 2;
		}
		code.add(DLabel, lengthLabel);
		
		code.add(DataI, 0);
		
		code.add(PushD, lengthLabel);
		
		code.append(args.get(1));

		code.add(StoreI);
		
		/*
		
		code.add(DLabel, subTypeLabel);
		
		code.add(DataI, 0);
		
		code.add(PushD, subTypeLabel);
		
		code.append(args.get(2));

		code.add(StoreI);
		
		*/
		
		//Getting size;
		
		code.append(args.get(0));
		
		code.add(PushD, lengthLabel);
		
		code.add(LoadI);
		
		code.add(JumpNeg, exception);
		
		code.add(PushD, lengthLabel);
		
		code.add(LoadI);
		
		//code.add(PushD, subTypeLabel);
		
		//code.add(LoadI);
		
		code.add(PushI, subTypeSize);
		
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
		
		//code.add(PushD, subTypeLabel);
		
		//code.add(LoadI);
		
		code.add(PushI, subTypeSize);
		
		code.add(StoreI);
		
		code.add(Duplicate);
		
		code.add(PushI, 12);
		
		code.add(Add);
		
		code.add(PushD, lengthLabel);
		
		code.add(LoadI);
		
		code.add(StoreI);
		
		//End Record setup, now onto Setting up Array
		
		code.add(PushI, 16);
		
		code.add(Add);
		
		//code.add(PushD, subTypeLabel);
		
		//code.add(LoadI);
		
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
		
		code.add(PushD, lengthLabel);
		
		code.add(LoadI);
		
		code.add(Duplicate);
		
		code.add(JumpPos, charwhileLabel);
		
		code.add(Pop);
		
		code.add(Jump, joinLabel);
		
		code.add(Label, floatLabel);
		
		code.add(PushD, lengthLabel);
		
		code.add(LoadI);
		
		code.add(Duplicate);
		
		code.add(JumpPos, floatwhileLabel);
		
		code.add(Pop);
		
		code.add(Jump, joinLabel);
		
		code.add(Label, intLabel);
		
		code.add(PushD, lengthLabel);
		
		code.add(LoadI);
		
		code.add(Duplicate);
		
		code.add(JumpPos, intwhileLabel);
		
		code.add(Pop);
		
		code.add(Jump, joinLabel);
		
		code.add(Label, intwhileLabel);
		
		code.add(Exchange);
		
		code.add(Duplicate);
		
		code.add(PushI, 0);
		
		code.add(StoreI);
		
		code.add(PushI, 4);
		
		code.add(Add);
		
		code.add(Exchange);

		code.add(PushI, 1);
		
		code.add(Subtract);
		
		code.add(Duplicate);
		
		code.add(JumpPos, intwhileLabel);
		
		code.add(Pop);
		
		code.add(Jump, joinLabel);
		
		code.add(Label, floatwhileLabel);

		code.add(Exchange);
		
		code.add(Duplicate);
		
		code.add(PushF, 0.0);
		
		code.add(StoreF);
		
		code.add(PushI, 8);
		
		code.add(Add);
		
		code.add(Exchange);
		
		code.add(PushI, 1);
		
		code.add(Subtract);
		
		code.add(Duplicate);
		
		code.add(JumpPos, floatwhileLabel);
		
		code.add(Pop);
		
		code.add(Jump, joinLabel);
		
		code.add(Label, charwhileLabel);
		
		code.add(Exchange);
		
		code.add(Duplicate);
		
		code.add(PushI, 0);
		
		code.add(StoreC);
		
		code.add(PushI, 1);
		
		code.add(Add);
		
		code.add(Exchange);
		
		code.add(PushI, 1);
		
		code.add(Subtract);
		
		code.add(Duplicate);
		
		code.add(JumpPos, charwhileLabel);
		
		code.add(Pop);
		
		code.add(Jump, joinLabel);
		
		
		
		code.add(Label, joinLabel);
		
		code.add(Pop);
		
		return code;
	}


}
