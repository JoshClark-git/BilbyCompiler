package asmCodeGenerator.runtime;
import static asmCodeGenerator.codeStorage.ASMCodeFragment.CodeType.*;
import static asmCodeGenerator.codeStorage.ASMOpcode.*;

import java.util.concurrent.TimeUnit;

import asmCodeGenerator.codeStorage.ASMCodeFragment;
import asmCodeGenerator.codeStorage.ASMOpcode;
public class RunTime {
	public static final String EAT_LOCATION_ZERO      = "$eat-location-zero";		// helps us distinguish null pointers from real ones.
	public static final String INTEGER_PRINT_FORMAT   = "$print-format-integer";
	public static final String ARRAY_PRINT_FORMAT   =   "$print-format-array";
	public static final String FLOATING_PRINT_FORMAT  = "$print-format-float";
	public static final String CHAR_PRINT_FORMAT      = "$print-format-char";
	public static final String STRING_PRINT_FORMAT    = "$print-format-string";
	public static final String BOOLEAN_PRINT_FORMAT   = "$print-format-boolean";
	public static final String NEWLINE_PRINT_FORMAT   = "$print-format-newline";
	public static final String SPACE_PRINT_FORMAT     = "$print-format-space";
	public static final String TAB_PRINT_FORMAT     = "  $print-format-tab";
	public static final String BOOLEAN_TRUE_STRING    = "$boolean-true-string";
	public static final String BOOLEAN_FALSE_STRING   = "$boolean-false-string";
	public static final String GLOBAL_MEMORY_BLOCK    = "$global-memory-block";
	public static final String FRAME_BLOCK            = "$frame-memory-block";
	public static final String FRAME_POINTER          = "$frame-pointer";
	public static final String FRAME_POINTER1         = "$frame-pointer1";
	public static final String STACK_POINTER          = "$stack-pointer";
	public static final String USABLE_MEMORY_START    = "$usable-memory-start";
	public static final String MAIN_PROGRAM_LABEL     = "$$main";
	
	public static final String GENERAL_RUNTIME_ERROR = "$$general-runtime-error";
	public static final String INTEGER_DIVIDE_BY_ZERO_RUNTIME_ERROR = "$$i-divide-by-zero";
	public static final String FLOATING_DIVIDE_BY_ZERO_RUNTIME_ERROR = "$$f-divide-by-zero";
	public static final String NEGATIVE_ARRAY_ALLOCATION_ERROR = "$$neg-alloc-array";
	public static final String INDEX_OUTTA_BOUNDS_ERROR = "$$index-outta-bounds";
	public static final String RANGE_ERROR_CREATION = "$$range-bounds";

	private ASMCodeFragment environmentASM() {
		ASMCodeFragment result = new ASMCodeFragment(GENERATES_VOID);
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_ADDRESS);
		frag.add(Memtop);
		
		result.add(DLabel, STACK_POINTER);
		result.add(DataI, 0);
		result.add(PushD, STACK_POINTER);
		result.append(frag);
		result.add(StoreI);
		
		result.append(jumpToMain());
		result.append(stringsForPrintf());
		result.append(runtimeErrors());
		result.add(DLabel, USABLE_MEMORY_START);
		return result;
	}
	private ASMCodeFragment updateSP(int offset) {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_ADDRESS);
		frag.add(PushD, STACK_POINTER);
		frag.add(LoadI);
		frag.add(PushI,offset);
		frag.add(Subtract);
		return frag;
	}
	private ASMCodeFragment updateFP(int offset) {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_ADDRESS);
		frag.add(PushD, FRAME_POINTER);
		frag.add(LoadI);
		frag.add(PushI,offset);
		frag.add(Subtract);
		return frag;
	}
	
	private ASMCodeFragment jumpToMain() {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);
		frag.add(Jump, MAIN_PROGRAM_LABEL);
		return frag;
	}

	private ASMCodeFragment stringsForPrintf() {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);
		frag.add(DLabel, EAT_LOCATION_ZERO);
		frag.add(DataZ, 8);
		frag.add(DLabel, CHAR_PRINT_FORMAT);
		frag.add(DataS, "%c");
		frag.add(DLabel, STRING_PRINT_FORMAT);
		frag.add(DataS, "%s");
		frag.add(DLabel, INTEGER_PRINT_FORMAT);
		frag.add(DataS, "%d");
		frag.add(DLabel, ARRAY_PRINT_FORMAT);
		frag.add(LoadI);
		frag.add(DataS, "%d");
		frag.add(DLabel, FLOATING_PRINT_FORMAT);
		frag.add(DataS, "%f");
		frag.add(DLabel, BOOLEAN_PRINT_FORMAT);
		frag.add(DataS, "%s");
		frag.add(DLabel, NEWLINE_PRINT_FORMAT);
		frag.add(DataS, "\n");
		frag.add(DLabel, SPACE_PRINT_FORMAT);
		frag.add(DataS, " ");
		frag.add(DLabel, TAB_PRINT_FORMAT);
		frag.add(DataS, "\t");
		frag.add(DLabel, BOOLEAN_TRUE_STRING);
		frag.add(DataS, "true");
		frag.add(DLabel, BOOLEAN_FALSE_STRING);
		frag.add(DataS, "false");
		
		return frag;
	}
	
	
	
	private ASMCodeFragment runtimeErrors() {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);
		
		generalRuntimeError(frag);
		integerDivideByZeroError(frag);
		floatingDivideByZeroError(frag);
		NegArrayAllocError(frag);
		IndexOuttaBounds(frag);
		RangeError(frag);
		
		return frag;
	}
	private ASMCodeFragment generalRuntimeError(ASMCodeFragment frag) {
		String generalErrorMessage = "$errors-general-message";

		frag.add(DLabel, generalErrorMessage);
		frag.add(DataS, "Runtime error: %s\n");
		
		frag.add(Label, GENERAL_RUNTIME_ERROR);
		frag.add(PushD, generalErrorMessage);
		frag.add(Printf);
		frag.add(Halt);
		return frag;
	}
	private void integerDivideByZeroError(ASMCodeFragment frag) {
		String intDivideByZeroMessage = "$errors-int-divide-by-zero";
		
		frag.add(DLabel, intDivideByZeroMessage);
		frag.add(DataS, "integer divide by zero");
		
		frag.add(Label, RunTime.INTEGER_DIVIDE_BY_ZERO_RUNTIME_ERROR);
		frag.add(PushD, intDivideByZeroMessage);
		frag.add(Jump, GENERAL_RUNTIME_ERROR);
		
	}
	
	private void floatingDivideByZeroError(ASMCodeFragment frag) {
		String floatDivideByZeroMessage = "$errors-float-divide-by-zero";
		frag.add(DLabel, floatDivideByZeroMessage);
		frag.add(DataS, "floating divide by zero");
		
		frag.add(Label, FLOATING_DIVIDE_BY_ZERO_RUNTIME_ERROR);
		frag.add(PushD, floatDivideByZeroMessage);
		frag.add(Jump, GENERAL_RUNTIME_ERROR);
	}
	
	private void NegArrayAllocError(ASMCodeFragment frag) {
		String negativeArrayAllocMessage = "$errors-Array-Alloc-Negative";
		frag.add(DLabel, negativeArrayAllocMessage);
		frag.add(DataS, "negative array alloc");
		
		frag.add(Label, NEGATIVE_ARRAY_ALLOCATION_ERROR);
		frag.add(PushD, negativeArrayAllocMessage);
		frag.add(Jump, GENERAL_RUNTIME_ERROR);
	}
	
	private void RangeError(ASMCodeFragment frag) {
		String rangeErrorMessage = "$errors-Range";
		frag.add(DLabel, rangeErrorMessage);
		frag.add(DataS, "range low > high");
		
		frag.add(Label, RANGE_ERROR_CREATION);
		frag.add(PushD, rangeErrorMessage);
		frag.add(Jump, GENERAL_RUNTIME_ERROR);
	}
	
	private void IndexOuttaBounds(ASMCodeFragment frag) {
		String indexOuttaBoundsMessage = "$errors-Index-Outta-Bounds";
		frag.add(DLabel, indexOuttaBoundsMessage);
		frag.add(DataS, "index out of bounds");
		
		frag.add(Label, INDEX_OUTTA_BOUNDS_ERROR);
		frag.add(PushD, indexOuttaBoundsMessage);
		frag.add(Jump, GENERAL_RUNTIME_ERROR);
	}
	
	
	public static ASMCodeFragment getEnvironment() {
		RunTime rt = new RunTime();
		return rt.environmentASM();
	}
}
