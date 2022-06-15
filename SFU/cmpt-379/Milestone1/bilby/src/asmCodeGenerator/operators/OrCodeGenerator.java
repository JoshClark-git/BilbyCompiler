package asmCodeGenerator.operators;

import static asmCodeGenerator.codeStorage.ASMOpcode.*;

import java.util.List;

import asmCodeGenerator.Labeller;
import asmCodeGenerator.codeStorage.ASMCodeFragment;
import asmCodeGenerator.codeStorage.ASMCodeFragment.CodeType;
import asmCodeGenerator.codeStorage.ASMOpcode;
import parseTree.ParseNode;
import semanticAnalyzer.types.PrimitiveType;

public class OrCodeGenerator implements SimpleCodeGenerator {
	private ASMOpcode subtractOpcode;
	private ASMOpcode jumpTrueOpcode;
	
	
	public OrCodeGenerator(ASMOpcode subtractOpcode, ASMOpcode jumpTrueOpcode) {
		super();
		this.subtractOpcode = subtractOpcode;
		this.jumpTrueOpcode = jumpTrueOpcode;
	}

	@Override
	public ASMCodeFragment generate(ParseNode node, List<ASMCodeFragment> args) {
		
		Labeller labeller = new Labeller("compare");
		
		String firstTrueLabel  = labeller.newLabel("1true");
		String secondTrueLabel  = labeller.newLabel("2true");
		String falseLabel = labeller.newLabel("false");
		String joinLabel  = labeller.newLabel("join");
		
		ASMCodeFragment code = new ASMCodeFragment(CodeType.GENERATES_VALUE);
		
		code.append(args.get(0));
		
		code.add(JumpTrue, firstTrueLabel);
		
		code.append(args.get(1));
		
		code.add(JumpTrue, secondTrueLabel);
		
		code.add(Jump,falseLabel);
		
	
		
		code.add(Label, firstTrueLabel);
		code.add(PushI, 1);
		code.add(Jump, joinLabel);
		code.add(Label, secondTrueLabel);
		code.add(PushI, 1);
		code.add(Jump, joinLabel);
		code.add(Label, falseLabel);
		code.add(PushI, 0);
		code.add(Jump, joinLabel);
		code.add(Label, joinLabel);
		return code;
	}


}
