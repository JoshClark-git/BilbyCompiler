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

public class IndexCodeGenerator implements SimpleCodeGenerator {
	
	
	public IndexCodeGenerator() {
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
		String indexLabel  = labeller.newLabel("index");
		
		ASMCodeFragment code = new ASMCodeFragment(CodeType.GENERATES_VALUE);
		
		code.add(DLabel, indexLabel);
		
		code.add(DataI, 0);
		
		code.add(PushD, indexLabel);
		
		code.append(args.get(1));

		code.add(StoreI);
		
		//Array Append
		code.append(args.get(0));
		
		code.add(Duplicate);
		
		//Check index
		
		code.add(PushI, 12);
		
		code.add(Add);
		
		code.add(LoadI);
		
		code.add(PushI,1);
		
		code.add(Subtract);
		
		//Check proper index
		
		code.add(PushD, indexLabel);
		
		code.add(LoadI);
		
		code.add(Subtract);
		
		code.add(JumpNeg, exception);
		
		//Getting Position.
		
		code.add(PushI, 16);
		
		code.add(Add);
		
		//Pos Number
		
		code.add(PushD, indexLabel);
		
		code.add(LoadI);
		
		//Array T size
		
		code.add(PushI, node.child(0).getType().atomicType().getSize());
		
		code.add(Multiply);
		
		code.add(Add);
		
		//Done Getting correct position, now decided between LoadC, LoadI, LoadF
		
		code.add(PushI, node.child(0).getType().atomicType().getSize());
		
		code.add(PushI, 1);
		
		code.add(Subtract);
		
		code.add(JumpFalse, charLabel);
		
		code.add(PushI, node.child(0).getType().atomicType().getSize());
		
		code.add(PushI, 8);
		
		code.add(Subtract);
		
		code.add(JumpNeg, intLabel);
		
		code.add(Jump, floatLabel);
		
		code.add(Label, charLabel);
		
		code.add(LoadC);
		
		code.add(Jump, joinLabel);
		
		code.add(Label, intLabel);
		
		code.add(LoadI);
		
		code.add(Jump, joinLabel);
		
		code.add(Label, floatLabel);
		
		code.add(LoadF);
		
		code.add(Jump, joinLabel);
		
		
		code.add(Label, joinLabel);
		
		return code;
	}


}
