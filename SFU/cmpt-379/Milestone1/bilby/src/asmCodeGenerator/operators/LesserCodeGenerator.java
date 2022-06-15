package asmCodeGenerator.operators;

import static asmCodeGenerator.codeStorage.ASMOpcode.Jump;
import static asmCodeGenerator.codeStorage.ASMOpcode.JumpPos;
import static asmCodeGenerator.codeStorage.ASMOpcode.Label;
import static asmCodeGenerator.codeStorage.ASMOpcode.PushI;

import java.util.List;

import asmCodeGenerator.Labeller;
import asmCodeGenerator.codeStorage.ASMCodeFragment;
import asmCodeGenerator.codeStorage.ASMCodeFragment.CodeType;
import asmCodeGenerator.codeStorage.ASMOpcode;
import parseTree.ParseNode;

public class LesserCodeGenerator implements SimpleCodeGenerator {
	private ASMOpcode subtractOpcode;
	private ASMOpcode jumpNegOpcode;
	
	
	public LesserCodeGenerator(ASMOpcode subtractOpcode, ASMOpcode jumpNegOpcode) {
		super();
		this.subtractOpcode = subtractOpcode;
		this.jumpNegOpcode = jumpNegOpcode;
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
		
		for(ASMCodeFragment fragment : args) {
			code.append(fragment);
		}
										// [num1 num2]
		
		code.add(Label, subLabel); 		// [.. 'sub]
		code.add(subtractOpcode);
		
		code.add(jumpNegOpcode, trueLabel);
		code.add(Jump, falseLabel);
		
		code.add(Label, trueLabel);
		code.add(PushI, 1);
		code.add(Jump, joinLabel);
		code.add(Label, falseLabel);
		code.add(PushI, 0);
		code.add(Jump, joinLabel);
		code.add(Label, joinLabel);	
		
		
		return code;
	}

}
