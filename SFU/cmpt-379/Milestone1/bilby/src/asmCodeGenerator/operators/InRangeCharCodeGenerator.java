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
import semanticAnalyzer.types.Type;

public class InRangeCharCodeGenerator implements SimpleCodeGenerator {
	
	
	public InRangeCharCodeGenerator() {
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
		String charLabel  = labeller.newLabel("char");
		String floatwhileLabel  = labeller.newLabel("floatwhile");
		String charwhileLabel  = labeller.newLabel("charwhile");
		String valueLabel  = labeller.newLabel("intValue");
		String floatvalueLabel  = labeller.newLabel("floatValue");
		
		ASMCodeFragment code = new ASMCodeFragment(CodeType.GENERATES_VALUE);
		
		code.add(DLabel, valueLabel);
		
		code.add(DataC, 0);
		
		code.add(PushD, valueLabel);
		
		code.append(args.get(0));

		code.add(StoreC);
		
		int subTypeSize = node.child(0).getType().atomicType().getSize();
		
		code.append(args.get(1));
		
		code.add(Duplicate);
		
		code.add(LoadC);
		
		code.add(PushD, valueLabel);
		
		code.add(LoadC);
		
		code.add(Subtract);
		
		code.add(JumpPos, falseLabel);
		
		code.add(PushI, 1);
		
		code.add(Add);
		
		code.add(LoadC);
		
		code.add(PushD, valueLabel);
		
		code.add(LoadC);
		
		code.add(Subtract);
		
		code.add(JumpNeg, falseLabel);
		
		code.add(PushI, 1);
		
		code.add(Jump, joinLabel);
		
		code.add(Label, falseLabel);
		
		code.add(PushI, 0);
		
		code.add(Jump, joinLabel);
		
		code.add(Label, joinLabel);
		
		return code;
	}


}
