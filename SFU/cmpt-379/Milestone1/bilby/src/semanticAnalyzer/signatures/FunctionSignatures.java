package semanticAnalyzer.signatures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import asmCodeGenerator.codeStorage.ASMOpcode;
import asmCodeGenerator.codeStorage.ASMCodeFragment.CodeType;
import asmCodeGenerator.operators.AllocCodeGenerator;
import asmCodeGenerator.operators.AndCodeGenerator;
import asmCodeGenerator.operators.ArrayExpressionCodeGenerator;
import asmCodeGenerator.operators.CastingCodeGenerator;
import asmCodeGenerator.operators.Char2BoolCodeGenerator;
import asmCodeGenerator.operators.CharRangeCodeGenerator;
import asmCodeGenerator.operators.ElseCodeGenerator;
import asmCodeGenerator.operators.EqualCodeGenerator;
import asmCodeGenerator.operators.TypeRangeADDCodeGenerator;
import asmCodeGenerator.operators.FloatRangeCodeGenerator;
import asmCodeGenerator.operators.FloatingDivideCodeGenerator;
import asmCodeGenerator.operators.ForCodeGenerator;
import asmCodeGenerator.operators.GreaterCodeGenerator;
import asmCodeGenerator.operators.GreaterEqualCodeGenerator;
import asmCodeGenerator.operators.HighCodeGenerator;
import asmCodeGenerator.operators.IfCodeGenerator;
import asmCodeGenerator.operators.InRangeCharCodeGenerator;
import asmCodeGenerator.operators.InRangeCodeGenerator;
import asmCodeGenerator.operators.InRangeIntCodeGenerator;
import asmCodeGenerator.operators.IndexAssignCodeGenerator;
import asmCodeGenerator.operators.IndexCodeGenerator;
import asmCodeGenerator.operators.Int2BoolCodeGenerator;
import asmCodeGenerator.operators.Int2CharCodeGenerator;
import asmCodeGenerator.operators.IntRangeADDCodeGenerator;
import asmCodeGenerator.operators.IntegerDivideCodeGenerator;
import asmCodeGenerator.operators.LengthCodeGenerator;
import asmCodeGenerator.operators.LesserEqualCodeGenerator;
import asmCodeGenerator.operators.LowCodeGenerator;
import asmCodeGenerator.operators.NotCodeGenerator;
import asmCodeGenerator.operators.NotEqualCodeGenerator;
import asmCodeGenerator.operators.OrCodeGenerator;
import asmCodeGenerator.operators.RangeCastRangeCodeGenerator;
import asmCodeGenerator.operators.RangeCastingCodeGenerator;
import asmCodeGenerator.operators.RangeFloatADDCodeGenerator;
import asmCodeGenerator.operators.RangeIntADDCodeGenerator;
import asmCodeGenerator.operators.RangeTypeADDCodeGenerator;
import asmCodeGenerator.operators.IntRangeCodeGenerator;
import asmCodeGenerator.operators.StringEqualCodeGenerator;
import asmCodeGenerator.operators.WhileCodeGenerator;
import asmCodeGenerator.operators.LesserCodeGenerator;
import lexicalAnalyzer.Keyword;
import lexicalAnalyzer.Punctuator;
import semanticAnalyzer.types.Array;
import semanticAnalyzer.types.Range;
import semanticAnalyzer.types.Type;
import semanticAnalyzer.types.TypeVariable;

import static semanticAnalyzer.types.PrimitiveType.*;


public class FunctionSignatures extends ArrayList<FunctionSignature> {
	private static final long serialVersionUID = -4907792488209670697L;
	private static Map<Object, FunctionSignatures> signaturesForKey = new HashMap<Object, FunctionSignatures>();
	
	Object key;

	public FunctionSignatures(Object key, FunctionSignature ...functionSignatures) {
		this.key = key;
		for(FunctionSignature functionSignature: functionSignatures) {
			add(functionSignature);
		}
		signaturesForKey.put(key, this);
	}
	
	public Object getKey() {
		return key;
	}
	public boolean hasKey(Object key) {
		return this.key.equals(key);
	}
	
	public FunctionSignature acceptingSignature(List<Type> types) {
		for(FunctionSignature functionSignature: this) {
			if(functionSignature.accepts(types)) {
				return functionSignature;
			}
		}
		return FunctionSignature.nullInstance();
	}
	public boolean accepts(List<Type> types) {
		return !acceptingSignature(types).isNull();
	}
	
	public List<PromotionSignature> leastLevelPromotions(List<Type> actuals) {
		List<PromotionSignature> allPromotions = PromotionSignature.makeAll(this, actuals);
		
		List<List<PromotionSignature>> byNumPromotions = new ArrayList<>();
		for(int i = 0; i<= actuals.size(); i++) {
			byNumPromotions.add(new ArrayList<PromotionSignature>());
		}
		
		for(PromotionSignature promotedSignature :allPromotions) {
			byNumPromotions.get(promotedSignature.numPromotions()).add(promotedSignature);
		}
		
		
		for(int i = 0; i<=actuals.size(); i++) {
			if(!byNumPromotions.get(i).isEmpty()) {
				return byNumPromotions.get(i);
			}
		}
		return byNumPromotions.get(0);
	}

	
	/////////////////////////////////////////////////////////////////////////////////
	// access to FunctionSignatures by key object.
	
	public static FunctionSignatures nullSignatures = new FunctionSignatures(0, FunctionSignature.nullInstance());

	public static FunctionSignatures signaturesOf(Object key) {
		if(signaturesForKey.containsKey(key)) {
			return signaturesForKey.get(key);
		}
		return nullSignatures;
	}
	public static FunctionSignature signature(Object key, List<Type> types) {
		FunctionSignatures signatures = FunctionSignatures.signaturesOf(key);
		return signatures.acceptingSignature(types);
	}

	
	
	/////////////////////////////////////////////////////////////////////////////////
	// Put the signatures for operators in the following static block.
	
	static {
		// here's one example to get you started with FunctionSignatures: the signatures for addition.		
		// for this to work, you should statically import PrimitiveType.*
		TypeVariable S = new TypeVariable("S");
		new FunctionSignatures(Keyword.IF,
				new FunctionSignature(new IfCodeGenerator(), BOOLEAN, BOOLEAN)
			);
		new FunctionSignatures(Keyword.ELSE,
				new FunctionSignature(new ElseCodeGenerator(), BOOLEAN, BOOLEAN)
			);
		new FunctionSignatures(Keyword.WHILE,
				new FunctionSignature(new WhileCodeGenerator(), BOOLEAN, NO_TYPE)
			);
		new FunctionSignatures(Keyword.FOR,
				new FunctionSignature(new ForCodeGenerator(), BOOLEAN, NO_TYPE)
			);
		new FunctionSignatures(Punctuator.ADD,
			new FunctionSignature(ASMOpcode.Nop, INTEGER, INTEGER),
			new FunctionSignature(ASMOpcode.Nop, FLOATING, FLOATING),
		    new FunctionSignature(ASMOpcode.Add, INTEGER, INTEGER, INTEGER),
		    new FunctionSignature(ASMOpcode.FAdd,FLOATING, FLOATING, FLOATING),
		    new FunctionSignature(new RangeTypeADDCodeGenerator(), new Range(INTEGER), INTEGER, new Range(INTEGER)),
		    new FunctionSignature(new TypeRangeADDCodeGenerator(), INTEGER, new Range(INTEGER), new Range(INTEGER)),
		    new FunctionSignature(new RangeTypeADDCodeGenerator(), new Range(FLOATING), FLOATING, new Range(FLOATING)),
		    new FunctionSignature(new TypeRangeADDCodeGenerator(), FLOATING, new Range(FLOATING), new Range(FLOATING))
		    
		);

		new FunctionSignatures(Punctuator.SUBTRACT,
				new FunctionSignature(ASMOpcode.Negate, INTEGER, INTEGER),
				new FunctionSignature(ASMOpcode.FNegate, FLOATING, FLOATING),
			    new FunctionSignature(ASMOpcode.Subtract, INTEGER, INTEGER, INTEGER),
			    new FunctionSignature(ASMOpcode.FSubtract,FLOATING, FLOATING, FLOATING)
			);
		
		new FunctionSignatures(Punctuator.MULTIPLY,
			    new FunctionSignature(ASMOpcode.Multiply, INTEGER, INTEGER, INTEGER),
			    new FunctionSignature(ASMOpcode.FMultiply,FLOATING, FLOATING, FLOATING)
			);
		new FunctionSignatures(Punctuator.DIVIDE,
			    new FunctionSignature(new IntegerDivideCodeGenerator(), INTEGER, INTEGER, INTEGER),
			    new FunctionSignature(new FloatingDivideCodeGenerator(),FLOATING, FLOATING, FLOATING)
			);
		
		
		new FunctionSignatures(Punctuator.GREATER,
				new FunctionSignature(new GreaterCodeGenerator(ASMOpcode.Subtract,ASMOpcode.JumpPos), INTEGER, INTEGER, BOOLEAN),
				new FunctionSignature(new GreaterCodeGenerator(ASMOpcode.FSubtract,ASMOpcode.JumpFPos), FLOATING, FLOATING, BOOLEAN),
				new FunctionSignature(new GreaterCodeGenerator(ASMOpcode.Subtract,ASMOpcode.JumpPos), CHARACTER, CHARACTER, BOOLEAN)
			);
		
		
		new FunctionSignatures(Punctuator.LESSER,
				new FunctionSignature(new LesserCodeGenerator(ASMOpcode.Subtract,ASMOpcode.JumpNeg), INTEGER, INTEGER, BOOLEAN),
				new FunctionSignature(new LesserCodeGenerator(ASMOpcode.FSubtract,ASMOpcode.JumpFNeg), FLOATING, FLOATING, BOOLEAN),
				new FunctionSignature(new LesserCodeGenerator(ASMOpcode.Subtract,ASMOpcode.JumpNeg), CHARACTER, CHARACTER, BOOLEAN)
			);
		
		new FunctionSignatures(Punctuator.GREATEREQUAL,
				new FunctionSignature(new GreaterEqualCodeGenerator(ASMOpcode.Subtract,ASMOpcode.JumpNeg), INTEGER, INTEGER, BOOLEAN),
				new FunctionSignature(new GreaterEqualCodeGenerator(ASMOpcode.FSubtract,ASMOpcode.JumpFNeg), FLOATING, FLOATING, BOOLEAN),
				new FunctionSignature(new GreaterEqualCodeGenerator(ASMOpcode.Subtract,ASMOpcode.JumpNeg), CHARACTER, CHARACTER, BOOLEAN)
			);
		
		new FunctionSignatures(Punctuator.LESSEREQUAL,
				new FunctionSignature(new LesserEqualCodeGenerator(ASMOpcode.Subtract,ASMOpcode.JumpPos), INTEGER, INTEGER, BOOLEAN),
				new FunctionSignature(new LesserEqualCodeGenerator(ASMOpcode.FSubtract,ASMOpcode.JumpFPos), FLOATING, FLOATING, BOOLEAN),
				new FunctionSignature(new LesserEqualCodeGenerator(ASMOpcode.Subtract,ASMOpcode.JumpPos), CHARACTER, CHARACTER, BOOLEAN)
			);
		
		new FunctionSignatures(Punctuator.EQUAL,
				new FunctionSignature(new EqualCodeGenerator(ASMOpcode.Subtract,ASMOpcode.JumpFalse), INTEGER, INTEGER, BOOLEAN),
				new FunctionSignature(new EqualCodeGenerator(ASMOpcode.FSubtract,ASMOpcode.JumpFZero), FLOATING, FLOATING, BOOLEAN),
				new FunctionSignature(new EqualCodeGenerator(ASMOpcode.Subtract,ASMOpcode.JumpFalse), CHARACTER, CHARACTER, BOOLEAN),
				new FunctionSignature(new EqualCodeGenerator(ASMOpcode.Subtract,ASMOpcode.JumpFalse), STRING, STRING, BOOLEAN),
				new FunctionSignature(new EqualCodeGenerator(ASMOpcode.Subtract,ASMOpcode.JumpFalse), BOOLEAN, BOOLEAN, BOOLEAN)
			);
		
		new FunctionSignatures(Punctuator.AND,
				new FunctionSignature(new AndCodeGenerator(ASMOpcode.Subtract,ASMOpcode.JumpFalse), BOOLEAN, BOOLEAN, BOOLEAN)
			);
		new FunctionSignatures(Punctuator.OR,
				new FunctionSignature(new OrCodeGenerator(ASMOpcode.Subtract,ASMOpcode.JumpFalse), BOOLEAN, BOOLEAN, BOOLEAN)
			);
		new FunctionSignatures(Punctuator.NOT,
				new FunctionSignature(new NotCodeGenerator(ASMOpcode.Subtract,ASMOpcode.JumpFalse),BOOLEAN, BOOLEAN)
			);
		
		new FunctionSignatures(Punctuator.NOTEQUAL,
				new FunctionSignature(new NotEqualCodeGenerator(ASMOpcode.Subtract,ASMOpcode.JumpFalse), INTEGER, INTEGER, BOOLEAN),
				new FunctionSignature(new NotEqualCodeGenerator(ASMOpcode.FSubtract,ASMOpcode.JumpFZero), FLOATING, FLOATING, BOOLEAN),
				new FunctionSignature(new EqualCodeGenerator(ASMOpcode.Subtract,ASMOpcode.JumpTrue), CHARACTER, CHARACTER, BOOLEAN),
				new FunctionSignature(new EqualCodeGenerator(ASMOpcode.Subtract,ASMOpcode.JumpTrue), STRING, STRING, BOOLEAN),
				new FunctionSignature(new EqualCodeGenerator(ASMOpcode.Subtract,ASMOpcode.JumpTrue), BOOLEAN, BOOLEAN, BOOLEAN)
			);
		new FunctionSignatures(Keyword.CAST,
				new FunctionSignature(new CastingCodeGenerator(ASMOpcode.ConvertF),INTEGER,FLOATING,FLOATING),
				new FunctionSignature(new Int2CharCodeGenerator(),INTEGER,CHARACTER, CHARACTER),
				new FunctionSignature(new Int2BoolCodeGenerator(),INTEGER,BOOLEAN, BOOLEAN),
				new FunctionSignature(new CastingCodeGenerator(ASMOpcode.Nop),CHARACTER,INTEGER, INTEGER),
				new FunctionSignature(new Char2BoolCodeGenerator(),CHARACTER,BOOLEAN, BOOLEAN),
				new FunctionSignature(new CastingCodeGenerator(ASMOpcode.ConvertI),FLOATING,INTEGER, INTEGER),
				new FunctionSignature(new CastingCodeGenerator(ASMOpcode.Nop),INTEGER,INTEGER, INTEGER),
				new FunctionSignature(new CastingCodeGenerator(ASMOpcode.Nop),CHARACTER,CHARACTER, CHARACTER),
				new FunctionSignature(new CastingCodeGenerator(ASMOpcode.Nop),BOOLEAN,BOOLEAN, BOOLEAN),
				new FunctionSignature(new CastingCodeGenerator(ASMOpcode.Nop),FLOATING,FLOATING, FLOATING),
				new FunctionSignature(new CastingCodeGenerator(ASMOpcode.Nop),STRING,STRING, STRING),
				new FunctionSignature(new RangeCastingCodeGenerator(),new Range(S),CHARACTER, new Range(S)),
				new FunctionSignature(new RangeCastingCodeGenerator(),new Array(S),CHARACTER, new Array(S)),
				new FunctionSignature(new RangeCastRangeCodeGenerator(), new Range(S), new Range(S), new Range(S)),
				new FunctionSignature(new RangeCastRangeCodeGenerator(), new Array(S), new Array(S), new Array(S))
				);
		
		new FunctionSignatures(Keyword.ALLOC,
				new FunctionSignature(new AllocCodeGenerator(),new Array(S), INTEGER, new Array(S))
				);
		new FunctionSignatures(Punctuator.COMMA,
				new FunctionSignature(new ArrayExpressionCodeGenerator(),new Array(S), S, new Array(S))
				);
		new FunctionSignatures(Punctuator.INDEXING,
				new FunctionSignature(new IndexCodeGenerator(),new Array(S), INTEGER, S)
				);
		new FunctionSignatures(Punctuator.INDEXING_ASSIGN,
				new FunctionSignature(new IndexAssignCodeGenerator(),new Array(S), S, S)
				);
		new FunctionSignatures(Keyword.LENGTH,
				new FunctionSignature(new LengthCodeGenerator(),new Array(S), INTEGER)
				);
		new FunctionSignatures(Punctuator.RANGE_CREATION,
				new FunctionSignature(new IntRangeCodeGenerator(),INTEGER, INTEGER, new Range(S)),
				new FunctionSignature(new FloatRangeCodeGenerator(),FLOATING, FLOATING, new Range(S)),
				new FunctionSignature(new CharRangeCodeGenerator(),CHARACTER, CHARACTER, new Range(S))
				);
		new FunctionSignatures(Keyword.LOW,
				new FunctionSignature(new LowCodeGenerator(), new Range(S), S)
				);
		new FunctionSignatures(Keyword.HIGH,
				new FunctionSignature(new HighCodeGenerator(), new Range(S), S)
				);
		new FunctionSignatures(Keyword.IN,
				new FunctionSignature(new InRangeCodeGenerator(), S, new Range(S), BOOLEAN)
				);
		// First, we use the operator itself (in this case the Punctuator ADD) as the key.
		// Then, we give that key two signatures: one an (INT x INT -> INT) and the other
		// a (FLOAT x FLOAT -> FLOAT).  Each signature has a "whichVariant" parameter where
		// I'm placing the instruction (ASMOpcode) that needs to be executed.
		//
		// I'll follow the convention that if a signature has an ASMOpcode for its whichVariant,
		// then to generate code for the operation, one only needs to generate the code for
		// the operands (in order) and then add to that the Opcode.  For instance, the code for
		// floating addition should look like:
		//
		//		(generate argument 1)	: may be many instructions
		//		(generate argument 2)   : ditto
		//		FAdd					: just one instruction
		//
		// If the code that an operator should generate is more complicated than this, then
		// I will not use an ASMOpcode for the whichVariant.  In these cases I typically use
		// a small object with one method (the "Command" design pattern) that generates the
		// required code.

	}

}
