package asmCodeGenerator;

import static asmCodeGenerator.codeStorage.ASMOpcode.*;
import parseTree.ParseNode;
import parseTree.nodeTypes.NewlineNode;
import parseTree.nodeTypes.PrintStatementNode;
import parseTree.nodeTypes.SpaceNode;
import parseTree.nodeTypes.TabNode;
import semanticAnalyzer.types.PrimitiveType;
import semanticAnalyzer.types.Type;
import asmCodeGenerator.ASMCodeGenerator.CodeVisitor;
import asmCodeGenerator.codeStorage.ASMCodeFragment;
import asmCodeGenerator.runtime.RunTime;
import lexicalAnalyzer.Punctuator;

public class PrintStatementGenerator {
	ASMCodeFragment code;
	ASMCodeGenerator.CodeVisitor visitor;
	
	
	public PrintStatementGenerator(ASMCodeFragment code, CodeVisitor visitor) {
		super();
		this.code = code;
		this.visitor = visitor;
	}


	public void generate(PrintStatementNode node) {
		for(ParseNode child : node.getChildren()) {
			if(child instanceof NewlineNode || child instanceof SpaceNode || child instanceof TabNode) {
				ASMCodeFragment childCode = visitor.removeVoidCode(child);
				code.append(childCode);
			}
			else {
				appendPrintCode(child);
			}
		}
	}

	private void appendPrintCode(ParseNode node) {
		if(node.getType().isArray()) {
			ASMCodeFragment arrayCode = visitor.removeValueCode(node);
			if(node.getType().atomicType().isArray()) {
				MultiDimensionArrayRecursion(arrayCode, node.getType());
			}
			else if(node.getType().atomicType().equals(PrimitiveType.FLOATING)) {
				FloatconvertToCArrayIfArray(arrayCode, node.getType());
			}
			else if(node.getType().atomicType().equals(PrimitiveType.INTEGER)) {
				IntconvertToCArrayIfArray(arrayCode, node.getType());
			}
			else if(node.getType().atomicType().equals(PrimitiveType.CHARACTER)) {
				CharconvertToCArrayIfArray(arrayCode, node.getType());
			}
			else {
				RangeConvertToCArrayIfArray(arrayCode, node.getType());
			}
			return;
		}
		if(node.getType().isRange()) {
			ASMCodeFragment rangeCode = visitor.removeValueCode(node);
			if(node.getType().atomicType().equals(PrimitiveType.FLOATING)) {
				FloatRangeConvertToCRange(rangeCode, node.getType());
			}
			else if(node.getType().atomicType().equals(PrimitiveType.INTEGER)) {
				IntRangeConvertToCRange(rangeCode, node.getType());
			}
			else {
				CharRangeConvertToCRange(rangeCode, node.getType());
			}
			return;
		}
		String format = printFormat(node.getType());
		
		code.append(visitor.removeValueCode(node));
		convertToStringIfBoolean(node.getType());
		convertToCStringIfString(node.getType());
		code.add(PushD, format);
		code.add(Printf);
	}
	
	private void appendPrintCode(ASMCodeFragment arrayCode, Type type) {
		if(type.isArray()) {
			if(type.atomicType().isArray()) {
				RecursiveMultiDimensionArrayRecursion(arrayCode, type);
			}
			else if(type.atomicType().equals(PrimitiveType.FLOATING)) {
				//FloatconvertToCArrayIfArray(arrayCode, type);
				RecursiveFloatconvertToCArrayIfArray(arrayCode, type);
			}
			else if(type.atomicType().equals(PrimitiveType.INTEGER)) {
				//IntconvertToCArrayIfArray(arrayCode, type);
				RecursiveIntconvertToCArrayIfArray(arrayCode, type);
			}
			else if(type.atomicType().equals(PrimitiveType.CHARACTER)) {
				//IntconvertToCArrayIfArray(arrayCode, type);
				RecursiveIntconvertToCArrayIfArray(arrayCode, type);
			}
			else {
				RecursiveRangeConvertToCArrayIfArray(arrayCode, type);
				code.add(PushI, 1);
			}
			return;
		}
		if(type.isRange()) {
			System.out.println("Is range");
			//ASMCodeFragment rangeCode = visitor.removeValueCode(node);
			if(type.atomicType().equals(PrimitiveType.FLOATING)) {
				RecursiveFloatRangeConvertToCRange(arrayCode, type);
			}
			else if(type.atomicType().equals(PrimitiveType.INTEGER)) {
				RecursiveIntRangeConvertToCRange(arrayCode, type);
			}
			else {
				RecursiveCharRangeConvertToCRange(arrayCode, type);
			}
			code.add(PushI, 1);
			return;
		}
		String format = printFormat(type);
		//System.out.println(arrayCode);
		
		//code.append(arrayCode);
		convertToStringIfBoolean(type);
		convertToCStringIfString(type);
		code.add(PushD, format);
		code.add(Printf);
	}
	
	private void convertToStringIfBoolean(Type type) {
		if(type != PrimitiveType.BOOLEAN) {
			return;
		}
		Labeller labeller = new Labeller("print-boolean");
		String trueLabel = labeller.newLabel("true");
		String endLabel = labeller.newLabel("join");

		code.add(JumpTrue, trueLabel);
		code.add(PushD, RunTime.BOOLEAN_FALSE_STRING);
		code.add(Jump, endLabel);
		code.add(Label, trueLabel);
		code.add(PushD, RunTime.BOOLEAN_TRUE_STRING);
		code.add(Label, endLabel);
	}
	
	private void convertToCStringIfString(Type type) {
		if(type != PrimitiveType.STRING) {
			return;
		}
		code.add(PushI, 12);
		code.add(Add);
	}
	
	private void MultiDimensionArrayRecursion(ASMCodeFragment arrayCode, Type type) {
		code.append(arrayCode);
		Labeller labeller = new Labeller("compare");
		
		String MultiIndexLabel = labeller.newLabel("MultiIndex");
		String lengthLabel = labeller.newLabel("length");
		String joinLabel = labeller.newLabel("join");
		String whileLabel = labeller.newLabel("while");
		
		code.add(PushI, 91);
		String format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		
		code.add(Duplicate);
		code.add(PushI,12);
		code.add(Add);
		code.add(LoadI);
		code.add(Duplicate);
		code.add(JumpFalse, joinLabel);
		code.add(PushI, 1);
		code.add(Subtract);
		code.add(Exchange);
		code.add(PushI, 16);
		code.add(Add);
		code.add(Duplicate);
		
		
		
		code.add(LoadI);
		
		//RecursiveIntconvertToCArrayIfArray(arrayCode,type.atomicType());
		appendPrintCode(arrayCode,type.atomicType());
		
		code.add(Pop);
		
		code.add(Pop);
		
		code.add(Exchange);
		
		code.add(Duplicate);
		
		code.add(JumpFalse,joinLabel);
		
		code.add(Exchange);
		
		//[lenghtLeft, prevAddr]
		
		code.add(Label, whileLabel);
		
		code.add(PushI, 44);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		code.add(PushI, 32);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		code.add(PushI, 4);
		
		code.add(Add);
		
		code.add(Duplicate);
		
		code.add(LoadI);
		
		//RecursiveIntconvertToCArrayIfArray(arrayCode,type.atomicType());
		appendPrintCode(arrayCode,type.atomicType());
		
		code.add(Pop);
		code.add(Pop);
		
		code.add(Exchange);
		
		code.add(PushI, 1);
		
		code.add(Subtract);
		
		code.add(Duplicate);
		
		code.add(JumpFalse, joinLabel);
		
		code.add(Exchange);
		
		code.add(Jump, whileLabel);
		
		
		
		
		code.add(Label,joinLabel);
		code.add(PushI, 93);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
	}
	
	private void RecursiveMultiDimensionArrayRecursion(ASMCodeFragment arrayCode, Type type) {
		//code.append(arrayCode);
		Labeller labeller = new Labeller("compare");
		
		String MultiIndexLabel = labeller.newLabel("MultiIndex");
		String lengthLabel = labeller.newLabel("length");
		String joinLabel = labeller.newLabel("join");
		String whileLabel = labeller.newLabel("while");
		
		code.add(PushI, 91);
		String format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		
		code.add(Duplicate);
		code.add(PushI,12);
		code.add(Add);
		code.add(LoadI);
		code.add(Duplicate);
		code.add(JumpFalse, joinLabel);
		code.add(PushI, 1);
		code.add(Subtract);
		code.add(Exchange);
		code.add(PushI, 16);
		code.add(Add);
		code.add(Duplicate);
		
		
		
		code.add(LoadI);
		
		//RecursiveIntconvertToCArrayIfArray(arrayCode,type.atomicType());
		appendPrintCode(arrayCode,type.atomicType());
		
		code.add(Pop);
		
		code.add(Pop);
		
		code.add(Exchange);
		
		code.add(Duplicate);
		
		code.add(JumpFalse,joinLabel);
		
		code.add(Exchange);
		
		//[lenghtLeft, prevAddr]
		
		code.add(Label, whileLabel);
		
		code.add(PushI, 44);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		code.add(PushI, 32);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		code.add(PushI, 4);
		
		code.add(Add);
		
		code.add(Duplicate);
		
		code.add(LoadI);

		appendPrintCode(arrayCode,type.atomicType());
		
		code.add(Pop);
		code.add(Pop);
		
		code.add(Exchange);
		
		code.add(PushI, 1);
		
		code.add(Subtract);
		
		code.add(Duplicate);
		
		code.add(JumpFalse, joinLabel);
		
		code.add(Exchange);
		
		code.add(Jump, whileLabel);
		
		
		
		
		code.add(Label,joinLabel);
		code.add(PushI, 93);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
	}
	
	private void CharconvertToCArrayIfArray(ASMCodeFragment arrayCode, Type type) {

		if(!(type.isArray())) {
			return;
		}
		int subTypeSize = type.atomicType().getSize();
		
		Labeller labeller = new Labeller("compare");
		
		String indexLabel = labeller.newLabel("index");
		String lengthLabel = labeller.newLabel("length");
		String joinLabel = labeller.newLabel("join");
		String whileLabel = labeller.newLabel("while");
		
		code.add(DLabel, indexLabel);
		
		code.add(DataI, 0);
		
		code.add(PushD, indexLabel);
		
		code.append(arrayCode);

		code.add(StoreI);
		
		
		code.add(PushI, 91);
		String format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		
		
		code.add(PushD, indexLabel);
		code.add(LoadI);
		code.add(PushI,12);
		code.add(Add);
		code.add(LoadI);
		code.add(JumpFalse, joinLabel);
		code.add(PushD, indexLabel);
		code.add(LoadI);
		code.add(PushI,16);
		code.add(Add);
		code.add(LoadC);
		format = printFormat(type.atomicType());
		code.add(PushD, format);
		code.add(Printf);
		code.add(PushD, indexLabel);
		code.add(LoadI);
		code.add(PushI,12);
		code.add(Add);
		code.add(LoadI);
		code.add(PushI, 1);
		code.add(Subtract);
		code.add(JumpFalse, joinLabel);
		
		
		code.add(PushI, 1);
		code.add(Duplicate);
		code.add(Jump, whileLabel);
		
		code.add(Label, whileLabel);
		
		code.add(PushI, 44);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		code.add(PushI, 32);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		code.add(PushI, subTypeSize);
		code.add(Multiply);
		code.add(PushD, indexLabel);
		code.add(LoadI);
		code.add(PushI,16);
		code.add(Add);
		code.add(Add);
		code.add(LoadC);
		format = printFormat(type.atomicType());
		code.add(PushD, format);
		code.add(Printf);
		code.add(PushI, 1);
		code.add(Add);
		code.add(Duplicate);
		code.add(PushD, indexLabel);
		code.add(LoadI);
		code.add(PushI,12);
		code.add(Add);
		code.add(LoadI);
		code.add(Subtract);
		code.add(JumpFalse,joinLabel);
		code.add(Duplicate);
		code.add(Jump, whileLabel);
		
		code.add(Label, joinLabel);
		code.add(PushI, 93);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
	}
	
	private void FloatconvertToCArrayIfArray(ASMCodeFragment arrayCode, Type type) {

		if(!(type.isArray())) {
			return;
		}
		int subTypeSize = type.atomicType().getSize();
		
		Labeller labeller = new Labeller("compare");
		
		String indexLabel = labeller.newLabel("index");
		String lengthLabel = labeller.newLabel("length");
		String joinLabel = labeller.newLabel("join");
		String whileLabel = labeller.newLabel("while");
		
		code.add(DLabel, indexLabel);
		
		code.add(DataI, 0);
		
		code.add(PushD, indexLabel);
		
		code.append(arrayCode);

		code.add(StoreI);
		
		
		code.add(PushI, 91);
		String format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		
		
		code.add(PushD, indexLabel);
		code.add(LoadI);
		code.add(PushI,12);
		code.add(Add);
		code.add(LoadI);
		code.add(JumpFalse, joinLabel);
		code.add(PushD, indexLabel);
		code.add(LoadI);
		code.add(PushI,16);
		code.add(Add);
		code.add(LoadF);
		format = printFormat(type.atomicType());
		code.add(PushD, format);
		code.add(Printf);
		code.add(PushD, indexLabel);
		code.add(LoadI);
		code.add(PushI,12);
		code.add(Add);
		code.add(LoadI);
		code.add(PushI, 1);
		code.add(Subtract);
		code.add(JumpFalse, joinLabel);
		
		
		code.add(PushI, 1);
		code.add(Duplicate);
		code.add(Jump, whileLabel);
		
		code.add(Label, whileLabel);
		
		code.add(PushI, 44);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		code.add(PushI, 32);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		code.add(PushI, subTypeSize);
		code.add(Multiply);
		code.add(PushD, indexLabel);
		code.add(LoadI);
		code.add(PushI,16);
		code.add(Add);
		code.add(Add);
		code.add(LoadF);
		format = printFormat(type.atomicType());
		code.add(PushD, format);
		code.add(Printf);
		code.add(PushI, 1);
		code.add(Add);
		code.add(Duplicate);
		code.add(PushD, indexLabel);
		code.add(LoadI);
		code.add(PushI,12);
		code.add(Add);
		code.add(LoadI);
		code.add(Subtract);
		code.add(JumpFalse,joinLabel);
		code.add(Duplicate);
		code.add(Jump, whileLabel);
		
		code.add(Label, joinLabel);
		code.add(PushI, 93);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);		
	}
	
	
	private void IntconvertToCArrayIfArray(ASMCodeFragment arrayCode, Type type) {
		if(!(type.isArray())) {
			return;
		}
		System.out.println("INT Array");
		int subTypeSize = type.atomicType().getSize();
		
		Labeller labeller = new Labeller("compare");
		
		String indexLabels = labeller.newLabel("index");
		String joinLabel = labeller.newLabel("join");
		String whileLabel = labeller.newLabel("while");
		
		
		code.add(DLabel, indexLabels);
		
		code.add(DataI, 0);
		
		code.add(PushD, indexLabels);
		
		code.append(arrayCode);
		
		//code.add(LoadI);

		code.add(StoreI);
		
		code.add(PushI, 91);
		String format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		code.add(PushD, indexLabels);
		code.add(LoadI);
		code.add(PushI,12);
		code.add(Add);
		code.add(LoadI);
		code.add(JumpFalse, joinLabel);
		code.add(PushD, indexLabels);
		code.add(LoadI);
		code.add(PushI,16);
		code.add(Add);
		code.add(LoadI);
		format = printFormat(type.atomicType());
		code.add(PushD, format);
		code.add(Printf);
		code.add(PushD, indexLabels);
		code.add(LoadI);
		code.add(PushI,12);
		code.add(Add);
		code.add(LoadI);
		code.add(PushI, 1);
		code.add(Subtract);
		code.add(JumpFalse, joinLabel);
		
		
		code.add(PushI, 1);
		code.add(Duplicate);
		code.add(Jump, whileLabel);
		
		code.add(Label, whileLabel);
		
		code.add(PushI, 44);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		code.add(PushI, 32);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		code.add(PushI, subTypeSize);
		code.add(Multiply);
		code.add(PushD, indexLabels);
		code.add(LoadI);
		code.add(PushI,16);
		code.add(Add);
		code.add(Add);
		code.add(LoadI);
		format = printFormat(type.atomicType());
		code.add(PushD, format);
		code.add(Printf);
		code.add(PushI, 1);
		code.add(Add);
		code.add(Duplicate);
		code.add(PushD, indexLabels);
		code.add(LoadI);
		code.add(PushI,12);
		code.add(Add);
		code.add(LoadI);
		code.add(Subtract);
		code.add(JumpFalse,joinLabel);
		code.add(Duplicate);
		code.add(Jump, whileLabel);
		
		code.add(Label, joinLabel);
		code.add(PushI, 93);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
	}
	
	
	private void RecursiveIntconvertToCArrayIfArray(ASMCodeFragment arrayCode, Type type) {
		if(!(type.isArray())) {
			return;
		}
		System.out.println("INT Array");
		int subTypeSize = type.atomicType().getSize();
		
		Labeller labeller = new Labeller("compare");
		
		String indexLabels = labeller.newLabel("index");
		String joinLabel = labeller.newLabel("join");
		String whileLabel = labeller.newLabel("while");
		
		//code.append(arrayCode);
		
		code.add(PushI,16);
		
		code.add(Add);
		
		code.add(Duplicate);
		
		code.add(Duplicate);
		
		code.add(PushI, 91);
		String format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		code.add(PushI, 4);
		code.add(Subtract);
		code.add(LoadI);
		
		code.add(Duplicate);
		
		code.add(JumpFalse, joinLabel);
		
		code.add(Exchange);
		
		code.add(LoadI);
		
		appendPrintCode(arrayCode,type.atomicType());
		
		code.add(PushI, 1);
		
		code.add(Subtract);
		
		code.add(Duplicate);
		
		code.add(JumpFalse, joinLabel);
		
		code.add(Jump, whileLabel);
		
		//[prevAddr, lengthLeft]
		
		code.add(Label, whileLabel);
		
		code.add(PushI, 44);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		code.add(PushI, 32);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		code.add(Exchange);
		
		code.add(PushI, subTypeSize);
		
		code.add(Add);
		
		code.add(Duplicate);
		
		code.add(LoadI);
		
		appendPrintCode(arrayCode,type.atomicType());
		
		code.add(Exchange);
		
		code.add(PushI, 1);
		
		code.add(Subtract);
		
		code.add(Duplicate);
		
		code.add(JumpFalse, joinLabel);
		
		
		code.add(Jump, whileLabel);		
		
		code.add(Label, joinLabel);
		code.add(PushI, 93);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
	}
	
	private void RangeConvertToCArrayIfArray(ASMCodeFragment arrayCode, Type type) {
		if(!(type.isArray())) {
			return;
		}
		
		//appendPrintCode(arrayCode,type.atomicType());
		
		System.out.println("INT Array");
		int subTypeSize = type.atomicType().getSize();
		
		Labeller labeller = new Labeller("compare");
		
		String indexLabels = labeller.newLabel("index");
		String joinLabel = labeller.newLabel("join");
		String whileLabel = labeller.newLabel("while");
		
		code.append(arrayCode);
		
		code.add(PushI, 16);
		
		code.add(Add);
	
		code.add(PushI, 91);
		String format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		code.add(LoadI);
		
		appendPrintCode(arrayCode,type.atomicType());
		
		
		code.add(PushI, 93);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
	}
	
	private void RecursiveRangeConvertToCArrayIfArray(ASMCodeFragment arrayCode, Type type) {
		if(!(type.isArray())) {
			return;
		}
		System.out.println("INT Array");
		int subTypeSize = type.atomicType().getSize();
		
		Labeller labeller = new Labeller("compare");
		
		String indexLabels = labeller.newLabel("index");
		String joinLabel = labeller.newLabel("join");
		String whileLabel = labeller.newLabel("while");
		
		//code.append(arrayCode);
		
		code.add(PushI, 16);
		
		code.add(Add);
	
		code.add(PushI, 91);
		String format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		code.add(LoadI);
		
		appendPrintCode(arrayCode,type.atomicType());
		
		
		code.add(PushI, 93);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
	}
	
	private void RecursiveFloatconvertToCArrayIfArray(ASMCodeFragment arrayCode, Type type) {
		if(!(type.isArray())) {
			return;
		}
		int subTypeSize = type.atomicType().getSize();
		
		Labeller labeller = new Labeller("compare");
		
		String indexLabels = labeller.newLabel("index");
		String joinLabel = labeller.newLabel("join");
		String whileLabel = labeller.newLabel("while");
		
		//code.append(arrayCode);
		
		code.add(PushI,16);
		
		code.add(Add);
		
		code.add(Duplicate);
		
		code.add(Duplicate);
		
		
		code.add(PushI, 91);
		String format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		code.add(PushI, 4);
		code.add(Subtract);
		code.add(LoadI);
		
		code.add(Duplicate);
		
		code.add(JumpFalse, joinLabel);
		
		code.add(Exchange);
		
		code.add(LoadF);
		
		appendPrintCode(arrayCode,type.atomicType());
		
		code.add(PushI, 1);
		
		code.add(Subtract);
		
		code.add(Duplicate);
		
		code.add(JumpFalse, joinLabel);
		
		code.add(Jump, whileLabel);
		
		//[prevAddr, lengthLeft]
		
		code.add(Label, whileLabel);
		
		code.add(PushI, 44);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		code.add(PushI, 32);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		code.add(Exchange);
		
		code.add(PushI, subTypeSize);
		
		code.add(Add);
		
		code.add(Duplicate);
		
		code.add(LoadF);
		
		appendPrintCode(arrayCode,type.atomicType());
		
		code.add(Exchange);
		
		code.add(PushI, 1);
		
		code.add(Subtract);
		
		code.add(Duplicate);
		
		code.add(JumpFalse, joinLabel);
		
		code.add(Jump, whileLabel);		
		
		code.add(Label, joinLabel);
		code.add(PushI, 93);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
	}
	
	private void RecursiveCharconvertToCArrayIfArray(ASMCodeFragment arrayCode, Type type) {
		if(!(type.isArray())) {
			return;
		}
		int subTypeSize = type.atomicType().getSize();
		
		Labeller labeller = new Labeller("compare");
		
		String indexLabels = labeller.newLabel("index");
		String joinLabel = labeller.newLabel("join");
		String whileLabel = labeller.newLabel("while");
		
		//code.append(arrayCode);
		
		code.add(PushI,16);
		
		code.add(Add);
		
		code.add(Duplicate);
		
		code.add(Duplicate);
		
		
		code.add(PushI, 91);
		String format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		code.add(PushI, 4);
		code.add(Subtract);
		code.add(LoadI);
		
		code.add(Duplicate);
		
		code.add(JumpFalse, joinLabel);
		
		code.add(Exchange);
		
		code.add(LoadC);
		
		appendPrintCode(arrayCode,type.atomicType());
		
		code.add(PushI, 1);
		
		code.add(Subtract);
		
		code.add(Duplicate);
		
		code.add(JumpFalse, joinLabel);
		
		code.add(Jump, whileLabel);
		
		//[prevAddr, lengthLeft]
		
		code.add(Label, whileLabel);
		
		code.add(PushI, 44);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		code.add(PushI, 32);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		code.add(Exchange);
		
		code.add(PushI, subTypeSize);
		
		code.add(Add);
		
		code.add(Duplicate);
		
		code.add(LoadC);
		
		appendPrintCode(arrayCode,type.atomicType());
		
		code.add(Exchange);
		
		code.add(PushI, 1);
		
		code.add(Subtract);
		
		code.add(Duplicate);
		
		code.add(JumpFalse, joinLabel);
		
		code.add(Jump, whileLabel);		
		
		code.add(Label, joinLabel);
		code.add(PushI, 93);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
	}
	
	
	
	
	
	private void IntRangeConvertToCRange(ASMCodeFragment rangeCode, Type type) {

		int subTypeSize = type.atomicType().getSize();
		code.add(PushI, 60);
		String format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		
		code.append(rangeCode);
		
		code.add(Duplicate);
		
		code.add(LoadI);
		format = printFormat(type.atomicType());
		code.add(PushD, format);
		code.add(Printf);
		
		code.add(PushI, 46);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		code.add(PushI, 46);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		
		
		code.add(PushI, subTypeSize);
		code.add(Add);
		code.add(LoadI);
		format = printFormat(type.atomicType());
		code.add(PushD, format);
		code.add(Printf);
		
		code.add(PushI, 62);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
	}
	
	private void RecursiveIntRangeConvertToCRange(ASMCodeFragment rangeCode, Type type) {
		int subTypeSize = type.atomicType().getSize();
		code.add(PushI, 60);
		String format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		code.add(Duplicate);
		
		code.add(LoadI);
		format = printFormat(type.atomicType());
		code.add(PushD, format);
		code.add(Printf);
		
		code.add(PushI, 46);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		code.add(PushI, 46);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		
		
		code.add(PushI, subTypeSize);
		code.add(Add);
		code.add(LoadI);
		format = printFormat(type.atomicType());
		code.add(PushD, format);
		code.add(Printf);
		
		code.add(PushI, 62);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
	}
	private void FloatRangeConvertToCRange(ASMCodeFragment rangeCode, Type type) {

		int subTypeSize = type.atomicType().getSize();
		code.add(PushI, 60);
		String format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		
		code.append(rangeCode);
		
		code.add(Duplicate);
		
		code.add(LoadF);
		format = printFormat(type.atomicType());
		code.add(PushD, format);
		code.add(Printf);
		
		code.add(PushI, 46);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		code.add(PushI, 46);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		
		
		code.add(PushI, subTypeSize);
		code.add(Add);
		code.add(LoadF);
		format = printFormat(type.atomicType());
		code.add(PushD, format);
		code.add(Printf);
		
		code.add(PushI, 62);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
	}
	
	private void RecursiveFloatRangeConvertToCRange(ASMCodeFragment rangeCode, Type type) {

		int subTypeSize = type.atomicType().getSize();
		code.add(PushI, 60);
		String format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		
		//code.append(rangeCode);
		
		code.add(Duplicate);
		
		code.add(LoadF);
		format = printFormat(type.atomicType());
		code.add(PushD, format);
		code.add(Printf);
		
		code.add(PushI, 46);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		code.add(PushI, 46);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		
		
		code.add(PushI, subTypeSize);
		code.add(Add);
		code.add(LoadF);
		format = printFormat(type.atomicType());
		code.add(PushD, format);
		code.add(Printf);
		
		code.add(PushI, 62);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
	}
	
	private void CharRangeConvertToCRange(ASMCodeFragment rangeCode, Type type) {

		int subTypeSize = type.atomicType().getSize();
		code.add(PushI, 60);
		String format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		
		code.append(rangeCode);
		
		code.add(Duplicate);
		
		code.add(LoadC);
		format = printFormat(type.atomicType());
		code.add(PushD, format);
		code.add(Printf);
		
		code.add(PushI, 46);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		code.add(PushI, 46);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		
		
		code.add(PushI, subTypeSize);
		code.add(Add);
		code.add(LoadC);
		format = printFormat(type.atomicType());
		code.add(PushD, format);
		code.add(Printf);
		
		code.add(PushI, 62);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
	}
	
	private void RecursiveCharRangeConvertToCRange(ASMCodeFragment rangeCode, Type type) {

		int subTypeSize = type.atomicType().getSize();
		code.add(PushI, 60);
		String format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		
		//code.append(rangeCode);
		
		code.add(Duplicate);
		
		code.add(LoadC);
		format = printFormat(type.atomicType());
		code.add(PushD, format);
		code.add(Printf);
		
		code.add(PushI, 46);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		code.add(PushI, 46);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
		
		
		code.add(PushI, subTypeSize);
		code.add(Add);
		code.add(LoadC);
		format = printFormat(type.atomicType());
		code.add(PushD, format);
		code.add(Printf);
		
		code.add(PushI, 62);
		format = printFormat(PrimitiveType.CHARACTER);
		code.add(PushD, format);
		code.add(Printf);
		
	}
	

	private static String printFormat(Type type) {
		//assert type instanceof PrimitiveType;
		
		switch((PrimitiveType)type) {
		case INTEGER:	return RunTime.INTEGER_PRINT_FORMAT;
		case FLOATING:	return RunTime.FLOATING_PRINT_FORMAT;
		case BOOLEAN:	return RunTime.BOOLEAN_PRINT_FORMAT;
		case CHARACTER: return RunTime.CHAR_PRINT_FORMAT;
		case STRING:	return RunTime.STRING_PRINT_FORMAT;
		
		default:		
			assert false : "Type " + type + " unimplemented in PrintStatementGenerator.printFormat()";
			return "";
		}
	}
}
