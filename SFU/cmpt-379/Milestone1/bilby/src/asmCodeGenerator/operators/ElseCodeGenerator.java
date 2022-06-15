package asmCodeGenerator.operators;

import static asmCodeGenerator.codeStorage.ASMOpcode.*;

import java.util.List;

import asmCodeGenerator.Labeller;
import asmCodeGenerator.codeStorage.ASMCodeFragment;
import asmCodeGenerator.codeStorage.ASMCodeFragment.CodeType;
import asmCodeGenerator.codeStorage.ASMOpcode;
import parseTree.ParseNode;
import semanticAnalyzer.types.PrimitiveType;

public class ElseCodeGenerator implements SimpleCodeGenerator {
	
	
	public ElseCodeGenerator() {
		super();
	}

	@Override
	public ASMCodeFragment generate(ParseNode node, List<ASMCodeFragment> args) {
		
		Labeller labeller = new Labeller("compare");
		
		String startLabel = labeller.newLabel("start");
		String subLabel   = labeller.newLabel("sub");
		String trueLabel  = labeller.newLabel("true");
		String falseLabel = labeller.newLabel("false");
		String joinLabel  = labeller.newLabel("join");
		
		ASMCodeFragment code = new ASMCodeFragment(CodeType.GENERATES_VALUE);
		
		code.append(args.get(0));
		
		code.add(JumpFalse, falseLabel);
		
		code.add(Jump,trueLabel);
		
	
		
		code.add(Label, trueLabel);
		//code.add(PushI, 1);
		code.append(args.get(1));
		code.add(Jump, joinLabel);
		code.add(Label, falseLabel);
		//code.add(PushI, 0);
		code.append(args.get(2));
		code.add(Jump, joinLabel);
		code.add(Label, joinLabel);
		//code.add(PStack);
		
		return code;
	}


}
