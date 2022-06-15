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

public class HighCodeGenerator implements SimpleCodeGenerator {
	
	
	public HighCodeGenerator() {
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
		
		int subTypeSize = node.getType().atomicType().getSize();
		
		code.append(args.get(0));
		
		code.add(PushI, subTypeSize);
		
		code.add(Add);
		
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
		
		code.add(LoadC);
		
		code.add(Jump, joinLabel);
		
		code.add(Label, intLabel);
		
		code.add(LoadI);
		
		code.add(Jump, joinLabel);
		
		code.add(Label, floatLabel);
		
		code.add(LoadF);
		
		code.add(Jump, joinLabel);
		
		code.add(Label, joinLabel);
		
		return code;
		
		
	}


}
