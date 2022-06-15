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

public class FloatRangeCodeGenerator implements SimpleCodeGenerator {
	
	
	public FloatRangeCodeGenerator() {
		super();
	}

	@Override
	public ASMCodeFragment generate(ParseNode node, List<ASMCodeFragment> args) {
		
		Labeller labeller = new Labeller("compare");
		
		String exception = RunTime.RANGE_ERROR_CREATION;
		
		String lowLabel  = labeller.newLabel("low");
		String highLabel  = labeller.newLabel("high");
		
		ASMCodeFragment code = new ASMCodeFragment(CodeType.GENERATES_VALUE);
		
		int subTypeSize = node.getType().atomicType().getSize();
		
		code.add(PushI, subTypeSize);
		
		code.add(Duplicate);
		
		code.add(Add);
		
		code.add(Call, MemoryManager.MEM_MANAGER_ALLOCATE);
		
		code.add(Duplicate);
		
		code.add(Duplicate);
		
		//Check valid Range
		
		code.append(args.get(0));
		
		code.add(StoreF);
		
		code.add(PushI, subTypeSize);
		
		code.add(Add);
		
		code.append(args.get(1));
		
		code.add(StoreF);
		
		code.add(Duplicate);
		
		code.add(Duplicate);
		
		code.add(LoadF);
		
		code.add(Exchange);
		
		code.add(PushI, subTypeSize);
		
		code.add(Add);
		
		code.add(LoadF);
		
		code.add(FSubtract);
		
		code.add(JumpFPos, exception);
		
		
		
		return code;
		
		
	}


}
