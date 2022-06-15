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

public class InRangeCodeGenerator implements SimpleCodeGenerator {
	
	
	public InRangeCodeGenerator() {
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
		String valueLabel  = labeller.newLabel("value");
		String dataInt        = labeller.newLabel("dataI");
		String dataFloat        = labeller.newLabel("dataF");
		String pushD        = labeller.newLabel("pushD");
		String storeInt        = labeller.newLabel("storInt");
		String storeFloat        = labeller.newLabel("storeFloat");
		
		ASMCodeFragment code = new ASMCodeFragment(CodeType.GENERATES_VALUE);
		
		int subTypeSize = node.child(1).getType().atomicType().getSize();
		
		code.add(DLabel, valueLabel);
		
		code.add(PushI, subTypeSize);
		
		code.add(PushI, 8);
		
		code.add(Subtract);
		
		code.add(JumpNeg, dataInt);
		
		code.add(Jump, dataFloat);
		
		code.add(Label, pushD);
		
		//code.add(DataF, 0.0);
		
		code.add(PushD, valueLabel);
		
		code.append(args.get(0));
		
		code.add(PushI, subTypeSize);
		
		code.add(PushI, 8);
		
		code.add(Subtract);
		
		code.add(JumpNeg, storeInt);
		
		code.add(Jump, storeFloat);
		
		code.add(Label, dataInt);
		
		code.add(DataI, 0);
		
		code.add(Jump, pushD);
		
		code.add(Label, dataFloat);
		
		code.add(DataF, 0.0);
		
		code.add(Jump, pushD);
		
		code.add(Label, storeInt);
		
		code.add(StoreI);
		
		code.add(Jump, startLabel);
		
		code.add(Label, storeFloat);
		
		code.add(StoreF);
		
		code.add(Jump, startLabel);
		
		code.add(Label, startLabel);
		
		code.append(args.get(1));
		
		code.add(Duplicate);
		
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
		
		code.add(PushD, valueLabel);
		
		code.add(LoadC);
		
		code.add(Subtract);
		
		code.add(JumpPos, falseLabel);
		
		code.add(PushI, subTypeSize);
		
		code.add(Add);
		
		code.add(LoadC);
		
		code.add(PushD, valueLabel);
		
		code.add(LoadC);
		
		code.add(Subtract);
		
		code.add(JumpNeg, falseLabel);
		
		code.add(PushI, 1);
		
		code.add(Jump, joinLabel);
		
		
		
		
		code.add(Label, floatLabel);
		
		code.add(LoadF);
		
		code.add(PushD, valueLabel);
		
		code.add(LoadF);
		
		code.add(FSubtract);
		
		code.add(JumpFPos, falseLabel);
		
		code.add(PushI, subTypeSize);
		
		code.add(Add);
		
		code.add(LoadF);
		
		code.add(PushD, valueLabel);
		
		code.add(LoadF);
		
		code.add(FSubtract);
		
		code.add(JumpFNeg, falseLabel);
		
		code.add(PushI, 1);
		
		code.add(Jump, joinLabel);
		
		
		
		code.add(Label, intLabel);
		
		code.add(LoadI);
		
		code.add(PushD, valueLabel);
		
		code.add(LoadI);
		
		code.add(Subtract);
		
		code.add(JumpPos, falseLabel);
		
		code.add(PushI, subTypeSize);
		
		code.add(Add);
		
		code.add(LoadI);
		
		code.add(PushD, valueLabel);
		
		code.add(LoadI);
		
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
