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

public class RangeIntADDCodeGenerator implements SimpleCodeGenerator {
	
	
	public RangeIntADDCodeGenerator() {
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
		String additiveLabel  = labeller.newLabel("char");
		
		ASMCodeFragment code = new ASMCodeFragment(CodeType.GENERATES_VALUE);
		
		code.add(DLabel, additiveLabel);
		
		code.add(DataI, 0);
		
		code.add(PushD, additiveLabel);
		
		code.append(args.get(1));

		code.add(StoreI);
		
		code.append(args.get(0));
		
		code.add(Duplicate);
		
		code.add(Duplicate);
		
		code.add(Duplicate);
		
		code.add(Duplicate);
		
		code.add(LoadI);
		
		code.add(PushD, additiveLabel);
		
		code.add(LoadI);
		
		code.add(Add);
		
		code.add(StoreI);
		
		code.add(PushI, 4);
		
		code.add(Add);
		
		code.add(LoadI);
		
		code.add(PushD, additiveLabel);
		
		code.add(LoadI);
		
		code.add(Add);
		
		code.add(Exchange);
		
		code.add(PushI, 4);
		
		code.add(Add);
		
		code.add(Exchange);
		
		code.add(StoreI);
		
		
		
		
		
		
		return code;
		
		
	}


}
