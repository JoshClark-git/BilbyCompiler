package asmCodeGenerator.operators;

import static asmCodeGenerator.codeStorage.ASMOpcode.*;

import java.util.List;

import asmCodeGenerator.Labeller;
import asmCodeGenerator.codeStorage.ASMCodeFragment;
import asmCodeGenerator.codeStorage.ASMCodeFragment.CodeType;
import asmCodeGenerator.codeStorage.ASMOpcode;
import parseTree.ParseNode;
import semanticAnalyzer.types.PrimitiveType;

public class StringEqualCodeGenerator implements SimpleCodeGenerator {
	private ASMOpcode subtractOpcode;
	private ASMOpcode jumpTrueOpcode;
	
	
	public StringEqualCodeGenerator(ASMOpcode subtractOpcode, ASMOpcode jumpTrueOpcode) {
		super();
		this.subtractOpcode = subtractOpcode;
		this.jumpTrueOpcode = jumpTrueOpcode;
	}

	@Override
	public ASMCodeFragment generate(ParseNode node, List<ASMCodeFragment> args) {
		
		Labeller labeller = new Labeller("compare");
		
		String whileLabel = labeller.newLabel("while");
		
		String startLabel = labeller.newLabel("start");
		String subLabel   = labeller.newLabel("sub");
		String trueLabel  = labeller.newLabel("true");
		String falseLabel = labeller.newLabel("false");
		String joinLabel  = labeller.newLabel("join");
		ASMCodeFragment code = new ASMCodeFragment(CodeType.GENERATES_VALUE);
		code.add(PStack);
		for(ASMCodeFragment fragment : args) {
			code.append(fragment);
			//code.add(PushI, 12);
			//code.add(Add);
			//code.add(LoadC);
			code.add(PStack);
		}
		
		code.add(Jump, subLabel);
		
		code.add(Label, subLabel);
		code.add(subtractOpcode);
		
		code.add(jumpTrueOpcode, trueLabel);
		code.add(Jump, falseLabel);
		
		code.add(Label, trueLabel);
		code.add(PushI, 1);
		code.add(Jump, joinLabel);
		
		code.add(Label, falseLabel);
		code.add(PushI, 0);
		code.add(Jump, joinLabel);
		code.add(Label, joinLabel);
		code.add(PStack);
		
		return code;
	
	}
	
	private void convertToCStringIfString(ParseNode node,ASMCodeFragment code) {
		if(node.getType() != PrimitiveType.STRING) {
			return;
		}
		code.add(PushI, 12);
		code.add(Add);
	}

}
