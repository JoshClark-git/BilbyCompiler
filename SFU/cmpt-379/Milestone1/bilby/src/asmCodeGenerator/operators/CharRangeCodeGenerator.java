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

public class CharRangeCodeGenerator implements SimpleCodeGenerator {
	
	
	public CharRangeCodeGenerator() {
		super();
	}

	@Override
	public ASMCodeFragment generate(ParseNode node, List<ASMCodeFragment> args) {
		
		Labeller labeller = new Labeller("compare");
		
		String exception = RunTime.RANGE_ERROR_CREATION;
		
		String lowLabel  = labeller.newLabel("low");
		String highLabel  = labeller.newLabel("high");
		
		ASMCodeFragment code = new ASMCodeFragment(CodeType.GENERATES_VALUE);
		
		code.add(PushI, 2);
		
		code.add(Call, MemoryManager.MEM_MANAGER_ALLOCATE);
		
		code.add(Duplicate);
		
		code.add(Duplicate);
		
		//Check valid Range
		
		code.append(args.get(0));
		
		code.add(StoreI);
		
		code.add(PushI, 1);
		
		code.add(Add);
		
		code.append(args.get(1));
		
		code.add(StoreI);
		
		code.add(Duplicate);
		
		code.add(Duplicate);
		
		code.add(LoadC);
		
		code.add(Exchange);
		
		code.add(PushI, 1);
		
		code.add(Add);
		
		code.add(LoadC);
		
		code.add(Subtract);
		
		code.add(JumpPos, exception);
		
		return code;
		
		
	}


}
