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

public class RangeFloatADDCodeGenerator implements SimpleCodeGenerator {
	
	
	public RangeFloatADDCodeGenerator() {
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
		
		code.add(DataF, 0.0);
		
		code.add(PushD, additiveLabel);
		
		code.append(args.get(1));

		code.add(StoreF);
		
		code.append(args.get(0));
		
		code.add(Duplicate);
		
		code.add(Duplicate);
		
		code.add(Duplicate);
		
		code.add(Duplicate);
		
		code.add(LoadF);
		
		code.add(PushD, additiveLabel);
		
		code.add(LoadF);
		
		code.add(FAdd);
		
		code.add(StoreF);
		
		code.add(PushI, 8);
		
		code.add(Add);
		
		code.add(LoadF);
		
		code.add(PushD, additiveLabel);
		
		code.add(LoadF);
		
		code.add(FAdd);
		
		code.add(Exchange);
		
		code.add(PushI, 8);
		
		code.add(Add);
		
		code.add(Exchange);
		
		code.add(StoreF);
		
		
		
		
		
		
		return code;
		
		
	}


}
