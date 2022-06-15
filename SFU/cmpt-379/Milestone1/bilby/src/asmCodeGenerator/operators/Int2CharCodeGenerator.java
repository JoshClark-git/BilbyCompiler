package asmCodeGenerator.operators;

import static asmCodeGenerator.codeStorage.ASMOpcode.*;

import java.util.List;

import asmCodeGenerator.Labeller;
import asmCodeGenerator.codeStorage.ASMCodeFragment;
import asmCodeGenerator.codeStorage.ASMCodeFragment.CodeType;
import asmCodeGenerator.runtime.RunTime;
import asmCodeGenerator.codeStorage.ASMOpcode;
import parseTree.ParseNode;
import semanticAnalyzer.types.PrimitiveType;

public class Int2CharCodeGenerator implements SimpleCodeGenerator {
	
	
	public Int2CharCodeGenerator() {
		super();
	}

	@Override
	public ASMCodeFragment generate(ParseNode node, List<ASMCodeFragment> args) {
		
		Labeller labeller = new Labeller("compare");
		
		String whileLabel = labeller.newLabel("while");
		
		
		String startLabel = labeller.newLabel("start");
		String subLabel   = labeller.newLabel("sub");
		String castingLabel   = labeller.newLabel("cast");
		String trueLabel  = labeller.newLabel("true");
		String falseLabel = labeller.newLabel("false");
		String endLabel = labeller.newLabel("end");
		String joinLabel  = labeller.newLabel("join");
		String loopLabel  = labeller.newLabel("loop");
		ASMCodeFragment code = new ASMCodeFragment(CodeType.GENERATES_VALUE);
		code.append(args.get(0));
		
		code.add(Label, loopLabel);
		code.add(PushI, 127);
		code.add(BTAnd);
		return code;
		
	
	}


}
