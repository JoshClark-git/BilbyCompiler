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
import semanticAnalyzer.types.PrimitiveType;

public class IndexAssignCodeGenerator implements SimpleCodeGenerator {
	
	
	public IndexAssignCodeGenerator() {
		super();
	}

	@Override
	public ASMCodeFragment generate(ParseNode node, List<ASMCodeFragment> args) {
		
		Labeller labeller = new Labeller("compare");
		
		String exception = RunTime.INDEX_OUTTA_BOUNDS_ERROR;
		
		String startLabel = labeller.newLabel("start");
		String subLabel   = labeller.newLabel("sub");
		String trueLabel  = labeller.newLabel("true");
		String falseLabel = labeller.newLabel("false");
		String joinLabel  = labeller.newLabel("join");
		String whileLabel  = labeller.newLabel("while");
		String subTypeLabel  = labeller.newLabel("subType");
		String intLabel  = labeller.newLabel("int");
		String charLabel  = labeller.newLabel("char");
		String floatLabel  = labeller.newLabel("float");
		String indexLabel = labeller.newLabel("index");
		
		ASMCodeFragment code = new ASMCodeFragment(CodeType.GENERATES_ADDRESS);
		
		int subTypeSize = node.child(0).getType().atomicType().getSize();
		
		//Array Append
		
		code.append(args.get(0));
		
		code.add(DLabel, indexLabel);
		
		code.add(DataI, 0);
		
		code.add(PushD, indexLabel);
		
		code.append(args.get(1));

		code.add(StoreI);
		
		//Getting Position.
		
		code.add(PushI, 12);
		
		code.add(Add);
		
		code.add(Duplicate);
		
		code.add(LoadI);
		
		code.add(PushI,1);
		
		code.add(Subtract);
		
		//Check proper index
		
		code.add(PushD, indexLabel);
		
		code.add(LoadI);
		
		code.add(Subtract);
		
		code.add(JumpNeg, exception);
		
		//Proper Elements Index
		
		code.add(PushI, 4);
		
		code.add(Add);
		
		code.add(PushD, indexLabel);
		
		code.add(LoadI);
		
		//Proper Array Index
		
		code.add(PushI, subTypeSize);
		
		code.add(Multiply);
		
		code.add(Add);
		
		code.add(Duplicate);
		
		code.append(args.get(2));
		//StoreI vs StoreF vs StoreC
		
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
		
		code.add(StoreC);
		
		code.add(Jump, joinLabel);
		
		code.add(Label, intLabel);
		
		code.add(StoreI);
		
		code.add(Jump, joinLabel);
		
		code.add(Label, floatLabel);
		
		code.add(StoreF);
		
		code.add(Jump, joinLabel);
		
		code.add(Label, joinLabel);
		
		return code;
		
	}


}
