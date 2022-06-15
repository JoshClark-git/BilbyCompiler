package asmCodeGenerator.operators;

import static asmCodeGenerator.codeStorage.ASMOpcode.*;

import java.util.List;

import asmCodeGenerator.Labeller;
import asmCodeGenerator.codeStorage.ASMCodeFragment;
import asmCodeGenerator.codeStorage.ASMCodeFragment.CodeType;
import logging.BilbyLogger;
import asmCodeGenerator.codeStorage.ASMOpcode;
import asmCodeGenerator.runtime.RunTime;
import parseTree.ParseNode;
import semanticAnalyzer.types.PrimitiveType;

public class IntegerDivideCodeGenerator implements SimpleCodeGenerator {
	
	
	public IntegerDivideCodeGenerator() {
		super();
	}


	@Override
	public ASMCodeFragment generate(ParseNode node, List<ASMCodeFragment> args) {
		
		
		
		Labeller labeller = new Labeller("compare");
		
		
		String exception = RunTime.INTEGER_DIVIDE_BY_ZERO_RUNTIME_ERROR;
		
		String normalDivide = labeller.newLabel("divide");

		String joinLabel  = labeller.newLabel("join");
		
		ASMCodeFragment code = new ASMCodeFragment(CodeType.GENERATES_VALUE);
		for(ASMCodeFragment fragment : args) {
			code.append(fragment);
		}
		
		//code.add(PStack);
		code.add(Duplicate);
		//code.add(PStack);
		code.add(JumpFalse, exception);
		code.add(Jump,normalDivide);

		
		code.add(Label, normalDivide);
		code.add(Divide);
		code.add(Jump,joinLabel);
		
		code.add(Label, joinLabel);
		
		return code;
	}


}
