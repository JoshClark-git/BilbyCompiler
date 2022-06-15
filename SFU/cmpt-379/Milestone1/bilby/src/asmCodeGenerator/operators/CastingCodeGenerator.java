package asmCodeGenerator.operators;

import static asmCodeGenerator.codeStorage.ASMOpcode.*;

import java.util.List;

import asmCodeGenerator.Labeller;
import asmCodeGenerator.codeStorage.ASMCodeFragment;
import asmCodeGenerator.codeStorage.ASMCodeFragment.CodeType;
import asmCodeGenerator.codeStorage.ASMOpcode;
import parseTree.ParseNode;
import semanticAnalyzer.types.PrimitiveType;

public class CastingCodeGenerator implements SimpleCodeGenerator {
	private ASMOpcode castingOpcode;
	
	
	public CastingCodeGenerator(ASMOpcode castingOpcode) {
		super();
		this.castingOpcode = castingOpcode;
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
		String joinLabel  = labeller.newLabel("join");
		ASMCodeFragment code = new ASMCodeFragment(CodeType.GENERATES_VALUE);
		code.append(args.get(0));
		
		code.add(Jump, castingLabel);
		
		code.add(Label, castingLabel);
		code.add(castingOpcode);
		
		return code;
		
	
	}


}
