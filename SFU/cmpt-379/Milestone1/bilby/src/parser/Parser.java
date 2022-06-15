package parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tokens.*;
import logging.BilbyLogger;
import parseTree.*;
import parseTree.nodeTypes.BooleanConstantNode;
import parseTree.nodeTypes.BreakNode;
import parseTree.nodeTypes.CharConstantNode;
import parseTree.nodeTypes.ContinueNode;
import parseTree.nodeTypes.ArrayConstantNode;
import parseTree.nodeTypes.ArrayExpressionNode;
import parseTree.nodeTypes.AssignmentForNode;
import parseTree.nodeTypes.AssignmentNode;
import parseTree.nodeTypes.BlockNode;
import parseTree.nodeTypes.DeclarationNode;
import parseTree.nodeTypes.ErrorNode;
import parseTree.nodeTypes.ExpressionListNode;
import parseTree.nodeTypes.FloatingConstantNode;
import parseTree.nodeTypes.ForNode;
import parseTree.nodeTypes.FunctionBlockNode;
import parseTree.nodeTypes.FunctionInvocationNode;
import parseTree.nodeTypes.FunctionNode;
import parseTree.nodeTypes.IdentifierNode;
import parseTree.nodeTypes.IfBlockNode;
import parseTree.nodeTypes.IntegerConstantNode;
import parseTree.nodeTypes.LoopBlockNode;
import parseTree.nodeTypes.NewlineNode;
import parseTree.nodeTypes.OperatorNode;
import parseTree.nodeTypes.ParameterListNode;
import parseTree.nodeTypes.ParameterNode;
import parseTree.nodeTypes.ParanthesisNode;
import parseTree.nodeTypes.PrintStatementNode;
import parseTree.nodeTypes.ProgramNode;
import parseTree.nodeTypes.RangeConstantNode;
import parseTree.nodeTypes.ReturnNode;
import parseTree.nodeTypes.SpaceNode;
import parseTree.nodeTypes.StringConstantNode;
import parseTree.nodeTypes.TabNode;
import parseTree.nodeTypes.TypeNode;
import lexicalAnalyzer.Keyword;
import lexicalAnalyzer.Lextant;
import lexicalAnalyzer.Punctuator;
import lexicalAnalyzer.Scanner;

public class Parser {
	private Scanner scanner;
	private Token nowReading;
	private Token previouslyRead;

	public static ParseNode parse(Scanner scanner) {
		Parser parser = new Parser(scanner);
		return parser.parse();
	}

	public Parser(Scanner scanner) {
		super();
		this.scanner = scanner;
	}

	public ParseNode parse() {
		readToken();
		return parseProgram();
	}

	////////////////////////////////////////////////////////////
	// "program" is the start symbol S
	// S -> MAIN blockStatement

	private ParseNode parseProgram() {
		if (!startsProgram(nowReading)) {
			return syntaxErrorNode("main or func");
		}
		ParseNode program = new ProgramNode(nowReading);
		//Parse all func defs
		while (nowReading.isLextant(Keyword.FUNC)) {
			ParseNode funcStatement = parseFuncStatement();
			program.appendChild(funcStatement);
		}

		expect(Keyword.MAIN);
		ParseNode blockStatement = parseBlockStatement();
		program.appendChild(blockStatement);

		if (!(nowReading instanceof NullToken)) {
			return syntaxErrorNode("end of program");
		}

		return program;
	}

	private boolean startsProgram(Token token) {
		return (token.isLextant(Keyword.MAIN) || (token.isLextant(Keyword.FUNC)));
	}

	///////////////////////////////////////////////////////////
	// funcStatement
	//functionDefinition -> func type identifier ( parameterList ) blockStatement
	private ParseNode parseFuncStatement() {
		if (!startsFuncStatement(nowReading)) {
			return syntaxErrorNode("funcStatement");
		}
		expect(Keyword.FUNC);
		ParseNode type = parseType();
		ParseNode identifier = parseFuncIdentifier();
		expect(Punctuator.OPEN_PARANTHESIS);
		ParseNode paramList = parseParamList();
		expect(Punctuator.CLOSE_PARANTHESIS);
		ParseNode blockNode = parseFuncBlockStatement();
		return FunctionNode.withChildren(identifier.getToken(), type, identifier, paramList, blockNode);

	}

	private boolean startsFuncStatement(Token token) {
		return token.isLextant(Keyword.FUNC);
	}

	///////////////////////////////////////////////////////////
	// blockStatement

	// blockStatement -> { statement* }
	private ParseNode parseBlockStatement() {
		if (!startsBlockStatement(nowReading)) {
			return syntaxErrorNode("blockStatement");
		}
		BlockNode blockNode = null;
		if (previouslyRead.isLextant(Keyword.MAIN)) {
			blockNode = new BlockNode(previouslyRead);
		} 
		else {
			blockNode = new BlockNode(nowReading);
		}
		expect(Punctuator.OPEN_BRACE);
		while (startsStatement(nowReading)) {
			ParseNode statement = parseStatement();
			blockNode.appendChild(statement);
			/*
			if(startsBreakStatement(nowReading)) {
				ParseNode statement = parseBreakStatement();
				blockNode.appendChild(statement); 
			}
			else if(startsContinueStatement(nowReading)) {
				ParseNode statement = parseContinueStatement();
				blockNode.appendChild(statement); 
			}
			else if (startsAssignment(nowReading)) {
				ParseNode statement = parseAssignemnt();
				blockNode.appendChild(statement);
			} else if (startsBlockStatement(nowReading)) {
				ParseNode statement = parseBlockStatement();
				blockNode.appendChild(statement);
			} else if (startsIfStatement(nowReading)) {
				ParseNode statement = parseIfStatement();
				blockNode.appendChild(statement);
			}else {
				ParseNode statement = parseStatement(blockNode);
				blockNode.appendChild(statement);
			}
			*/
		}
		expect(Punctuator.CLOSE_BRACE);
		return blockNode;
	}

	private ParseNode parseFuncBlockStatement() {
		if (!startsBlockStatement(nowReading)) {
			return syntaxErrorNode("blockStatement");
		}
		FunctionBlockNode blockNode = new FunctionBlockNode(nowReading);
		expect(Punctuator.OPEN_BRACE);
		while (startsStatement(nowReading)) {
			ParseNode statement = parseStatement();
			blockNode.appendChild(statement);
			/*
			if(startsBreakStatement(nowReading)) {
				ParseNode statement = parseBreakStatement();
				blockNode.appendChild(statement); 
			}
			else if(startsContinueStatement(nowReading)) {
				ParseNode statement = parseContinueStatement();
				blockNode.appendChild(statement); 
			}
			else if (startsAssignment(nowReading)) {
				ParseNode statement = parseAssignemnt();
				blockNode.appendChild(statement);
			} else if (startsBlockStatement(nowReading)) {
				ParseNode statement = parseBlockStatement();
				blockNode.appendChild(statement);
			} else if (startsIfStatement(nowReading)) {
				ParseNode statement = parseIfStatement();
				blockNode.appendChild(statement);
			} else {
				ParseNode statement = parseStatement(blockNode);
				blockNode.appendChild(statement);
			}
			*/
		}
		expect(Punctuator.CLOSE_BRACE);
		return blockNode;
	}

	private boolean startsBlockStatement(Token token) {
		return token.isLextant(Punctuator.OPEN_BRACE);
	}

	private ParseNode parseLoopBlockStatement() {
		if (!startsBlockStatement(nowReading)) {
			return syntaxErrorNode("blockStatement");
		}
		LoopBlockNode blockNode = new LoopBlockNode(nowReading);
		expect(Punctuator.OPEN_BRACE);
		while (startsStatement(nowReading)) {
			ParseNode statement = parseStatement();
			blockNode.appendChild(statement);
			/*
			if(startsBreakStatement(nowReading)) {
				ParseNode statement = parseBreakStatement();
				blockNode.appendChild(statement); 
			}
			else if(startsContinueStatement(nowReading)) {
				ParseNode statement = parseContinueStatement();
				blockNode.appendChild(statement); 
			}
			else if (startsAssignment(nowReading)) {
				ParseNode statement = parseAssignemnt();
				blockNode.appendChild(statement);
			} else if (startsBlockStatement(nowReading)) {
				ParseNode statement = parseBlockStatement();
				blockNode.appendChild(statement);
			} else if (startsIfStatement(nowReading)) {
				ParseNode statement = parseIfStatement();
				blockNode.appendChild(statement);
			} else {
				ParseNode statement = parseStatement(blockNode);
				blockNode.appendChild(statement);
			}
			*/
		}
		expect(Punctuator.CLOSE_BRACE);
		return blockNode;
	}

	private ParseNode parseIfBlockStatement() {
		if (!startsBlockStatement(nowReading)) {
			return syntaxErrorNode("blockStatement");
		}
		ParseNode blockNode = new IfBlockNode(nowReading);
		expect(Punctuator.OPEN_BRACE);
		while (startsStatement(nowReading)) {
			ParseNode statement = parseStatement();
			blockNode.appendChild(statement);
			/*
			if(startsBreakStatement(nowReading)) {
				ParseNode statement = parseBreakStatement();
				blockNode.appendChild(statement); 
			}
			else if(startsContinueStatement(nowReading)) {
				ParseNode statement = parseContinueStatement();
				blockNode.appendChild(statement); 
			}
			else if (startsAssignment(nowReading)) {
				ParseNode statement = parseAssignemnt();
				blockNode.appendChild(statement);
			} else if (startsBlockStatement(nowReading)) {
				ParseNode statement = parseBlockStatement();
				blockNode.appendChild(statement);
			} else if (startsIfStatement(nowReading)) {
				ParseNode statement = parseIfStatement();
				blockNode.appendChild(statement);
			} else {
				ParseNode statement = parseStatement(blockNode);
				blockNode.appendChild(statement);
			}
			*/
		}
		expect(Punctuator.CLOSE_BRACE);
		return blockNode;
	}

	///////////////////////////////////////////////////////////
	//parameterList -> [parameterSpecification]* , // 0 or more comma-separated parameterSpecifications
	//parameterSpecification -> type identifier
	private ParseNode parseParamList() {
		if (nowReading.isLextant(Punctuator.CLOSE_PARANTHESIS)) {
			return new ParameterListNode(nowReading);
		}
		List<ParseNode> paramList = new ArrayList<ParseNode>();
		ParseNode type = parseType();
		ParseNode identifier = parseIdentifier();
		ParseNode paramNode = ParameterNode.withChildren(nowReading, type, identifier);
		paramList.add(paramNode);
		while (nowReading.isLextant(Punctuator.COMMA)) {
			expect(Punctuator.COMMA);
			type = parseType();
			identifier = parseIdentifier();
			paramNode = ParameterNode.withChildren(nowReading, type, identifier);
			paramList.add(paramNode);
		}
		return ParameterListNode.withArrayChildren(nowReading, paramList);
	}

///////////////////////////////////////////////////////////
// ExprList -> [expression,]*

	private ParseNode parseExprList() {
		if (nowReading.isLextant(Punctuator.CLOSE_PARANTHESIS)) {
			return new ExpressionListNode(nowReading);
		}
		List<ParseNode> exprList = new ArrayList<ParseNode>();
		ParseNode expression = parseExpression();
		
		exprList.add(expression);
		while (nowReading.isLextant(Punctuator.COMMA)) {
			expect(Punctuator.COMMA);
			expression = parseExpression();
			exprList.add(expression);
		}
		return ExpressionListNode.withArrayChildren(nowReading, exprList);
	}

	///////////////////////////////////////////////////////////
	// statements

	// statement-> declaration | printStmt | blockStmt | if/else | while | for | break | continue | return | assignment 
	private ParseNode parseStatement() {
		if (!startsStatement(nowReading)) {
			return syntaxErrorNode("statement");
		}
		if (startsDeclaration(nowReading)) {
			return parseDeclaration();
		}
		if (startsPrintStatement(nowReading)) {
			return parsePrintStatement();
		}
		if (startsIfStatement(nowReading)) {
			return parseIfStatement();
		}
		if (startsWhileStatement(nowReading)) {
			return parseWhileStatement();
		}
		if (startsForStatement(nowReading)) {
			return parseForStatement();
		}
		if (startsBreakStatement(nowReading)) {
			return parseBreakStatement();
		}
		if (startsContinueStatement(nowReading)) {
			return parseContinueStatement();
		}
		if (startsReturnStatement(nowReading)) {
			return parseReturnStatement();
		}
		if (startsAssignment(nowReading)) {
			return parseAssignemnt();
		}
		if (startsBlockStatement(nowReading)) {
			return parseBlockStatement();
		}
		
		return syntaxErrorNode("statement");
	}

	private boolean startsStatement(Token token) {
		return startsPrintStatement(token) || startsDeclaration(token) || startsAssignment(token)
				|| startsBlockStatement(token) || startsWhileStatement(token) || startsBreakStatement(token)
				|| startsContinueStatement(token) || startsReturnStatement(token) || startsIfStatement(token) || startsForStatement(token);
	}

	// return statement -> return expression?

	private ParseNode parseReturnStatement() {
		if (!startsReturnStatement(nowReading)) {
			return syntaxErrorNode("return statement");
		}
		Token returnToken = nowReading;
		readToken();
		// return void;
		if(nowReading.isLextant(Punctuator.TERMINATOR)) {
			expect(Punctuator.TERMINATOR);
			return new ReturnNode(returnToken);
		}
		ParseNode returnNode = parseExpression();
		expect(Punctuator.TERMINATOR);
		return ReturnNode.withChildren(returnToken, returnNode);
	}

	private boolean startsReturnStatement(Token token) {
		return token.isLextant(Keyword.RETURN);
	}

	// if statement -> if (expression) blockStatement ( else blockStatement)?

	private ParseNode parseIfStatement() {
		if (!startsIfStatement(nowReading)) {
			return syntaxErrorNode("if statement");
		}
		Token ifToken = nowReading;
		readToken();
		ParseNode expression = parseExpression();
		ParseNode ifBlock = parseIfBlockStatement();
		if (nowReading.isLextant(Keyword.ELSE)) {
			Token elseToken = nowReading;
			readToken();
			ParseNode elseBlock = parseIfBlockStatement();
			return OperatorNode.withChildren(elseToken, expression, ifBlock, elseBlock);
		}
		return OperatorNode.withChildren(ifToken, expression, ifBlock);
	}

	private boolean startsIfStatement(Token token) {
		return token.isLextant(Keyword.IF);
	}
	
	// while statement -> while (expression) blockStatement

	private ParseNode parseWhileStatement() {
		if (!startsWhileStatement(nowReading)) {
			return syntaxErrorNode("while statement");
		}
		Token whileToken = nowReading;
		readToken();
		expect(Punctuator.OPEN_PARANTHESIS);
		ParseNode expression = parseExpression();
		expect(Punctuator.CLOSE_PARANTHESIS);
		ParseNode block = parseLoopBlockStatement();
		return OperatorNode.withChildren(whileToken, expression, block);
	}

	private boolean startsWhileStatement(Token token) {
		return token.isLextant(Keyword.WHILE);
	}
	
	//forStatement -> for ( identifier in expression ) blockStatement // expression must have a char or int range type
	/*
	 { // new scope
		 imm identifier = low expression;
		 while ( identifier <= high expression ) {
		 	blockStatement;
		 	identifier++;
		 }
		  }
	*/
	private ParseNode parseForStatement() {
		if (!startsForStatement(nowReading)) {
			return syntaxErrorNode("for statement");
		}
		//Setup tokens
		Token whileToken = nowReading;
		Token imm = LextantToken.make(nowReading, Keyword.IMM.toString(), Keyword.IMM);
		Token add = LextantToken.make(whileToken, Punctuator.ADD.toString(), Punctuator.ADD);
		readToken();
		Token lessEqual = LextantToken.make(nowReading, Punctuator.LESSEREQUAL.toString(), Punctuator.LESSEREQUAL);
		expect(Punctuator.OPEN_PARANTHESIS);
		Token IntOne = IntegerConstantToken.make(nowReading, "1");
		
		//setup range identifier to compare to identifier
		
		ParseNode identifier = parseIdentifier();
		Token lesserEqualToken = IdentifierToken.make(nowReading,identifier.getToken().getLexeme());
		IdentifierNode lesserEqualID = new IdentifierNode(lesserEqualToken);
		Token rangeIDToken = IdentifierToken.make(nowReading, "range");
		Token low = LextantToken.make(nowReading, Keyword.LOW.toString(), Keyword.LOW);
		
		
		expect(Keyword.IN);
		
		//setting up high val of range
		ParseNode range = parseExpression();
		ParseNode rangeIDNode = new IdentifierNode(rangeIDToken);
		Token rangeLowToken = IdentifierToken.make(nowReading,rangeIDNode.getToken().getLexeme());
		IdentifierNode rangeLowID = new IdentifierNode(rangeLowToken);
		Token high = LextantToken.make(nowReading, Keyword.HIGH.toString(), Keyword.HIGH);
		Token rangeHighToken = IdentifierToken.make(nowReading,rangeIDNode.getToken().getLexeme());
		IdentifierNode rangeHighID = new IdentifierNode(rangeHighToken);
		
		
		ParseNode lowNode = OperatorNode.withChildren(low, rangeLowID);
		ParseNode highNode = OperatorNode.withChildren(high, rangeHighID);
		ParseNode lesserEqualNode = OperatorNode.withChildren(lessEqual, lesserEqualID, highNode);
		ParseNode one = new IntegerConstantNode(IntOne);
		
		
		expect(Punctuator.CLOSE_PARANTHESIS);
		Token assignToken = IdentifierToken.make(nowReading,identifier.getToken().getLexeme());
		IdentifierNode assignID = new IdentifierNode(assignToken);
		Token adderToken = IdentifierToken.make(nowReading,identifier.getToken().getLexeme());
		IdentifierNode adderID = new IdentifierNode(adderToken);
		ParseNode block = parseLoopBlockStatement();
		ParseNode adder = OperatorNode.withChildren(add, adderID,one);
		ParseNode assignmentNode = AssignmentForNode.withChildren(assignToken, assignID, adder);
		
		ParseNode declarationNode = DeclarationNode.withChildren(imm, identifier, lowNode);
		ParseNode rangeDeclarationNode = DeclarationNode.withChildren(imm, rangeIDNode,range);
		ParseNode forNode = ForNode.withChildren(whileToken, rangeDeclarationNode,declarationNode,lesserEqualNode,block, assignmentNode);
		return forNode;
	}

	private boolean startsForStatement(Token token) {
		return token.isLextant(Keyword.FOR);
	}

	// printStmt -> PRINT printExpressionList TERMINATOR
	private ParseNode parsePrintStatement() {
		if (!startsPrintStatement(nowReading)) {
			return syntaxErrorNode("print statement");
		}
		ParseNode statement = new PrintStatementNode(nowReading);

		readToken();
		ParseNode exprList = parsePrintExpressionList(statement);

		expect(Punctuator.TERMINATOR);
		return exprList;
	}

	private boolean startsPrintStatement(Token token) {
		return token.isLextant(Keyword.PRINT);
	}

	// This adds the printExpressions it parses to the children of the given parent
	// printExpressionList -> printSeparator* (expression printSeparator+)*
	// expression? (note that this is nullable)

	private ParseNode parsePrintExpressionList(ParseNode parent) {
		if (!startsPrintExpressionList(nowReading)) {
			return syntaxErrorNode("printExpressionList");
		}
		while (startsPrintSeparator(nowReading)) {
			parsePrintSeparator(parent);
		}
		while (startsExpression(nowReading)) {
			parent.appendChild(parseExpression());
			if (nowReading.isLextant(Punctuator.TERMINATOR)) {
				return parent;
			}
			while (startsPrintSeparator(nowReading)) {
				parsePrintSeparator(parent);
			}
		}
		return parent;
	}

	private boolean startsPrintExpressionList(Token token) {
		return startsExpression(token) || startsPrintSeparator(token);
	}

	// This adds the printSeparator it parses to the children of the given parent
	// printSeparator -> PRINT_SEPARATOR | PRINT_SPACE | PRINT_NEWLINE || PRINT_TAB

	private void parsePrintSeparator(ParseNode parent) {
		if (!startsPrintSeparator(nowReading)) {
			ParseNode child = syntaxErrorNode("print separator");
			parent.appendChild(child);
			return;
		}

		if (nowReading.isLextant(Punctuator.PRINT_NEWLINE)) {
			readToken();
			ParseNode child = new NewlineNode(previouslyRead);
			parent.appendChild(child);
		} else if (nowReading.isLextant(Punctuator.PRINT_SPACE)) {
			readToken();
			ParseNode child = new SpaceNode(previouslyRead);
			parent.appendChild(child);
		} else if (nowReading.isLextant(Punctuator.PRINT_SEPARATOR)) {
			readToken();
		} else if (nowReading.isLextant(Punctuator.PRINT_TAB)) {
			readToken();
			ParseNode child = new TabNode(previouslyRead);
			parent.appendChild(child);
		}

	}

	private boolean startsPrintSeparator(Token token) {
		return token.isLextant(Punctuator.PRINT_SEPARATOR, Punctuator.PRINT_SPACE, Punctuator.PRINT_NEWLINE,
				Punctuator.PRINT_TAB);
	}
	
	//assignmentStatement -> target := expression ;
	private ParseNode parseAssignemnt() {
		if (!startsAssignment(nowReading)) {
			return syntaxErrorNode("assignment");
		}
		ParseNode assignmentNode = new AssignmentNode(nowReading);
		ParseNode target = parseIdentifier();
		assignmentNode.appendChild(target);
		
		//Call void func
		if(target instanceof FunctionInvocationNode) {
			readToken();
			return target;
		}
		int indexAssigning = 0;
		int multiArray = 0;
		ParseNode indexAssign = null;
		ParseNode parent = assignmentNode;
		ParseNode indexAssignIndex = null;
		// Check if assigning to Array
		//arrayAssignment -> expression [ expression ] 
		if (nowReading.isLextant(Punctuator.OPEN_SQUARE)) {
			indexAssigning = 1;
			Token openToken = nowReading;
			Token indexingToken = LextantToken.make(openToken, openToken.getLexeme(), Punctuator.INDEXING_ASSIGN);
			indexAssign = new OperatorNode(indexingToken);
			readToken();
			indexAssignIndex = parseExpression();
			assignmentNode.appendChild(indexAssign);
			parent = indexAssign;
			expect(Punctuator.CLOSE_SQUARE);
			//multiarrayAssignment -> expression [([)* expression ](])* 
			while (nowReading.isLextant(Punctuator.OPEN_SQUARE)) {
				multiArray = 1;
				openToken = nowReading;
				indexingToken = LextantToken.make(openToken, openToken.getLexeme(), Punctuator.INDEXING);
				ParseNode index = new OperatorNode(indexingToken);
				index.appendChild(target);
				readToken();
				ParseNode indexValue = parseExpression();
				index.appendChild(indexValue);
				parent.appendChild(index);
				parent = index;
				expect(Punctuator.CLOSE_SQUARE);
			}
		}
		expect(Punctuator.ASSIGN);
		ParseNode expression = parseExpression();
		//check if assignment relates to arrayAssignment
		if (indexAssigning == 1) {
			if (!(multiArray == 1)) {
				indexAssign.appendChild(target);
			}
			indexAssign.appendChild(indexAssignIndex);
			indexAssign.appendChild(expression);
		//Normal assignment;
		} else {
			assignmentNode.appendChild(expression);
		}
		expect(Punctuator.TERMINATOR);
		return assignmentNode;

	}

	// declaration -> IMM identifier := expression TERMINATOR || MUT identifier := expression TERMINATOR
	private ParseNode parseDeclaration() {
		if (!startsDeclaration(nowReading)) {
			return syntaxErrorNode("declaration");
		}
		Token declarationToken = nowReading;
		readToken();

		DeclarationNode declarationNode = new DeclarationNode(declarationToken);
		IdentifierNode identifier = (IdentifierNode) parseIdentifier();

		declarationNode.appendChild(identifier);

		if (declarationToken.isLextant(Keyword.MUT)) {
			identifier.setMutability(true);
		}

		expect(Punctuator.ASSIGN);

		ParseNode expression = parseExpression();
		expect(Punctuator.TERMINATOR);

		return DeclarationNode.withChildren(declarationToken, identifier, expression);
	}

	private boolean startsDeclaration(Token token) {
		return (token.isLextant(Keyword.IMM) || token.isLextant(Keyword.MUT));
	}

	private boolean startsAssignment(Token token) {
		return startsLiteral(token);
	}

	///////////////////////////////////////////////////////////
	// expressions
	// expr -> paranthesisExpression | booleanExpression
	// paranthesisExpression -> (expr)
	// booleanExpression -> comparisonExpression [BOOLEAN comparisonExpression]?
	// comparisonExpression -> additiveExpression [COMPARISON additiveExpression]?
	// additiveExpression -> multiplicativeExpression [ADOOP multiplicativeExpression]* (left-assoc)
	// multiplicativeExpression -> unaryExpression [MULTOP atomicExpression]*(left-assoc)
	// unaryExpression -> UNARYOP* indexingExpression
	// indexingExpression -> atomicExpression ([expression])*
	// atomicExpression -> bracketedExpression | literal | rangeExpression
	// bracketedExpression -> [expression*] | [expression CAST type] | ALLOC [type] (expression)
	// literal -> identifier | Constant | break/continue | functionInvocation
	// rangeExpression -> < literal .. literal >

	// expr -> paranthesisExpression | booleanExpression
	private ParseNode parseExpression() {
		if (!startsExpression(nowReading)) {
			return syntaxErrorNode("expression");
		}
		if(startsParanthesisExpression(nowReading)) {
			return parseParanthesisExpression();
		}
		return parseBooleanComparison();
	}

	private boolean startsExpression(Token token) {
		return startsBooleanComparison(token);
	}
	
	// paranthesisExpression -> (expr)
	private ParseNode parseParanthesisExpression() {
		if (!startsParanthesisExpression(nowReading)) {
			return syntaxErrorNode("paranthesis expression");
		}
		Token paranthesis = nowReading;
		readToken();
		ParseNode expression = parseExpression();
		expect(Punctuator.CLOSE_PARANTHESIS);
		return ParanthesisNode.withChildren(paranthesis, expression);
	}
	private boolean startsParanthesisExpression(Token token) {
		return token.isLextant(Punctuator.OPEN_PARANTHESIS);
	}
	// booleanExpression -> comparisonExpression [BOOLEAN comparisonExpression]?
	
	private ParseNode parseBooleanComparison() {
		if (!startsBooleanComparison(nowReading)) {
			return syntaxErrorNode("comparison expression");
		}
		ParseNode left = parseComparisonExpression();
		while (nowReading.isLextant(Punctuator.OR, Punctuator.AND)) {
			readToken();
			if (!startsComparisonExpression(nowReading)) {
				break;
			}
			Token compareToken = previouslyRead;
			
			ParseNode right = parseComparisonExpression();

			left = OperatorNode.withChildren(compareToken, left, right);

		}
		return left;
	}
	private boolean startsBooleanComparison(Token token) {
		return startsComparisonExpression(token);
	}

	// comparisonExpression -> additiveExpression [COMPARISON additiveExpression]?
	private ParseNode parseComparisonExpression() {
		if (!startsComparisonExpression(nowReading)) {
			return syntaxErrorNode("comparison expression");
		}

		ParseNode left = parseAdditiveExpression();
		while (nowReading.isLextant(Punctuator.GREATER, Punctuator.LESSER, Punctuator.GREATEREQUAL,
				Punctuator.LESSEREQUAL, Punctuator.EQUAL, Punctuator.NOTEQUAL,
				Keyword.IN)) {
			// for(String punctuator : list) {
			// if(list.contains(nowReading)) {
			readToken();
			if (!startsAdditiveExpression(nowReading) || (nowReading.isLextant(Punctuator.ADD,Punctuator.OR))) {
				break;
			}
			Token compareToken = previouslyRead;
			// readToken();
			ParseNode right = parseAdditiveExpression();

			left = OperatorNode.withChildren(compareToken, left, right);
			//System.out.println(left);
			// }

		}
		return left;

	}

	private boolean startsComparisonExpression(Token token) {
		return startsAdditiveExpression(token);
	}

	// additiveExpression -> multiplicativeExpression [ADDOP multiplicativeExpression]*
	// (left-assoc)
	private ParseNode parseAdditiveExpression() {
		if (!startsAdditiveExpression(nowReading)) {
			return syntaxErrorNode("additiveExpression");
		}
		ParseNode left = parseMultiplicativeExpression();
		while (nowReading.isLextant(Punctuator.ADD, Punctuator.SUBTRACT)) {
			Token additiveToken = nowReading;
			readToken();
			ParseNode right = parseMultiplicativeExpression();

			left = OperatorNode.withChildren(additiveToken, left, right);
		}
		return left;
	}

	private boolean startsAdditiveExpression(Token token) {
		return startsMultiplicativeExpression(token);
	}

	// multiplicativeExpression -> UnaryExpression [MULTOP atomicExpression]*
	// (left-assoc)
	private ParseNode parseMultiplicativeExpression() {
		if (!startsMultiplicativeExpression(nowReading)) {
			return syntaxErrorNode("multiplicativeExpression");
		}
		ParseNode left = parseUnaryExpression();
		while (nowReading.isLextant(Punctuator.MULTIPLY, Punctuator.DIVIDE)) {
			Token multiplicativeToken = nowReading;
			readToken();
			ParseNode right = parseUnaryExpression();

			left = OperatorNode.withChildren(multiplicativeToken, left, right);
		}
		return left;
	}

	private boolean startsMultiplicativeExpression(Token token) {
		return startsUnaryExpression(token);
	}

	// atomicExpression -> Bracketed | literal
	private ParseNode parseAtomicExpression() {
		if (!startsAtomicExpression(nowReading)) {
			return syntaxErrorNode("atomic expression");
		} else if (startsBracketExpression(nowReading)) {
			return parseBracketExpression();
		} else if (startsRangeExpression(nowReading)) {
			return parseRangeExpression();
		} 
		return parseLiteral();
	}

	private boolean startsAtomicExpression(Token token) {
		return startsLiteral(token) || startsBracketExpression(token) || startsCastExpression(token)
				|| startsRangeExpression(token);
	}

	// [expressionList] | [expression AS type] | ALLOC [type] (expression)
	private ParseNode parseBracketExpression() {
		if (!startsBracketExpression(nowReading)) {
			return syntaxErrorNode("bracketed expression");
		}
		if (nowReading.isLextant(Punctuator.OPEN_SQUARE)) {
			expect(Punctuator.OPEN_SQUARE);
			ParseNode child = parseExpression();
			if (nowReading.isLextant(Keyword.CAST)) {
				child = parseCastExpression(child);
				expect(Punctuator.CLOSE_SQUARE);
				return child;
			} 
			//ExpressionList
			//note, the expressionList will contain two extra nodes.
			// first will be an IntConsNode of the size of the array
			// second will be the leastLevelPromotion TypeNode, which will be the arrayType.
			else {
				Token openSquare = LextantToken.make(nowReading, nowReading.getLexeme(), Punctuator.OPEN_SQUARE);
				Token listToken = LextantToken.make(nowReading, nowReading.getLexeme(), Punctuator.COMMA);
				// readToken();
				int finalType = -1;
				if (child.toString().charAt(0) == 'C') {
					finalType = 0;
				}
				else if (child.toString().charAt(0) == 'I') {
					finalType = 1;
				} else if (child.toString().charAt(0) == 'F') {
					finalType = 2;
				}
				else if (child.toString().charAt(0) == 'S') {
					finalType = 3;
				}

				
				List<ParseNode> nodes = new ArrayList<>();
				nodes.add(child);
				int numNodes = 1;
				while (nowReading.isLextant(Punctuator.COMMA)) {
					expect(Punctuator.COMMA);
					child = parseExpression();
					nodes.add(child);
					if (finalType != 2 || finalType != 3) {
						int newTypeVal = -1;
						if (child.toString().charAt(0) == 'C') {
							newTypeVal = 0;
						}
						else if (child.toString().charAt(0) == 'I') {
							newTypeVal = 1;
						} else if (child.toString().charAt(0) == 'F') {
							newTypeVal = 2;
						}
						if (newTypeVal > finalType) {
							finalType = newTypeVal;
						}
					}

					numNodes = numNodes + 1;
				}
				String length = Integer.toString(numNodes);
				Token lengthToken = IntegerConstantToken.make(openSquare, length);
				ParseNode lengthNode = new IntegerConstantNode(lengthToken);
				expect(Punctuator.CLOSE_SQUARE);
				Token arrayType;
				if(finalType == -1) {
					arrayType = LextantToken.make(openSquare, openSquare.getLexeme(), Keyword.BOOL);
				}
				else if(finalType == 0) {
					arrayType = LextantToken.make(openSquare, openSquare.getLexeme(), Keyword.CHAR);
				}
				else if(finalType == 1) {
					arrayType = LextantToken.make(openSquare, openSquare.getLexeme(), Keyword.INT);
				}
				else if(finalType == 2){
					arrayType = LextantToken.make(openSquare, openSquare.getLexeme(), Keyword.FLOAT);
				}
				else {
					arrayType = LextantToken.make(openSquare, openSquare.getLexeme(), Keyword.STRING);
				}
				ParseNode actualType = TypeNode.make(arrayType);
				nodes.add(lengthNode);
				nodes.add(actualType);
				return ArrayExpressionNode.withArrayChildren(listToken, nodes);
			}
		}
		if (nowReading.isLextant(Keyword.ALLOC)) {
			Token allocToken = nowReading;
			expect(Keyword.ALLOC);
			if (!nowReading.isLextant(Punctuator.OPEN_SQUARE)) {
				return syntaxErrorNode("array type");
			}
			ParseNode type = parseType();
			expect(Punctuator.OPEN_PARANTHESIS);
			ParseNode expression = parseExpression();
			expect(Punctuator.CLOSE_PARANTHESIS);
			return OperatorNode.withChildren(allocToken, type, expression);
		}
		return syntaxErrorNode("bracketed not implementd");
	}

	private boolean startsBracketExpression(Token token) {
		return token.isLextant(Punctuator.OPEN_SQUARE, Keyword.ALLOC);
	}

	private ParseNode parseRangeExpression() {
		if (!startsRangeExpression(nowReading)) {
			return syntaxErrorNode("range type");
		}
		expect(Punctuator.LESSER);
		ParseNode expression1 = parseExpression();
		Token rangeToken = nowReading;
		expect(Punctuator.RANGE_CREATION);
		System.out.println(nowReading);
		ParseNode expression2 = parseExpression();

		return OperatorNode.withChildren(rangeToken, expression1, expression2);
	}

	private boolean startsRangeExpression(Token token) {
		return token.isLextant(Punctuator.LESSER);
	}

	private ParseNode parseCastExpression(ParseNode literal) {
		Token operatorToken = nowReading;
		if (!(operatorToken.isLextant(Keyword.CAST))) {
			return syntaxErrorNode("Cast Keyword");
		}
		readToken();
		if (nowReading.isLextant(Keyword.RANGE)) {
			ParseNode rangeNode = new RangeConstantNode(nowReading);
			readToken();
			return OperatorNode.withChildren(operatorToken, literal, rangeNode);
		}
		if (nowReading.isLextant(Keyword.ARRAY)) {
			ParseNode rangeNode = new ArrayConstantNode(nowReading);
			readToken();
			return OperatorNode.withChildren(operatorToken, literal, rangeNode);
		}
		ParseNode toType = parseType();

		return OperatorNode.withChildren(operatorToken, literal, toType);
		// return child;
	}

	private ParseNode parseType() {
		if (!startsType(nowReading)) {
			return syntaxErrorNode("type");
		}
		Token typeToken = nowReading;
		// readToken();
		if (typeToken.isLextant(Punctuator.OPEN_SQUARE)) {
			readToken();
			ParseNode child = parseType();
			expect(Punctuator.CLOSE_SQUARE);
			return TypeNode.withChild(typeToken, child);
		} else if(typeToken.isLextant(Punctuator.LESSER)) {
			readToken();
			ParseNode child = parseType();
			expect(Punctuator.GREATER);
			return TypeNode.withChild(typeToken, child);
		}
		else {
			readToken();
			return TypeNode.make(typeToken);
		}

	}

	private boolean startsType(Token token) {
		if ((previouslyRead.isLextant(Keyword.FUNC) && (token.isLextant(Keyword.VOID)))) {
			return true;
		}
		return token.isLextant(Keyword.FLOAT, Keyword.INT, Keyword.STRING, Keyword.CHAR, Keyword.BOOL, Keyword.RANGE, Keyword.ARRAY,
				Punctuator.OPEN_SQUARE, Punctuator.LESSER);
	}

	// private ParseNode parseType(Token token) {
	// if (token.getLexeme().toString().equals("float")) {
	// return new FloatingConstantNode(FloatingConstantToken.make(token, "0.0"));
	// }
	// else if (token.getLexeme().toString().equals("char")) {
	// return new CharConstantNode(CharConstantToken.make(token, "A"));
	// }
	// else if (token.getLexeme().toString().equals("bool")) {
	// return new BooleanConstantNode(token);
	// }
	// else if (token.getLexeme().toString().equals("int")) {
	// return new IntegerConstantNode(IntegerConstantToken.make(token, "0"));
	// }
	// else {
	// return syntaxErrorNode("Casting Type");
	// }
	//
	// }

	private boolean startsCastExpression(Token token) {
		return token.isLextant(Punctuator.OPEN_SQUARE);
	}

	// unaryExpression -> UNARYOP atomicExpression
	private ParseNode parseUnaryExpression() {
		if (!startsUnaryExpression(nowReading)) {
			return syntaxErrorNode("unary expression");
		}
		if (nowReading.isLextant(Punctuator.SUBTRACT, Punctuator.ADD, Punctuator.NOT, Keyword.LENGTH, Keyword.LOW,
				Keyword.HIGH)) {
			Token operatorToken = nowReading;
			readToken();
			ParseNode child = parseAtomicExpression();

			return OperatorNode.withChildren(operatorToken, child);
		}
		return parseIndexingExpression();
	}

	// IndexingExpression -> AtomicExpression ([expression])*
	private ParseNode parseIndexingExpression() {
		if (!startsIndexingExpression(nowReading)) {
			return syntaxErrorNode("Indexing expression");
		}
		ParseNode left = parseAtomicExpression();
		while (nowReading.isLextant(Punctuator.OPEN_SQUARE)) {
			Token bracketToken = nowReading;
			expect(Punctuator.OPEN_SQUARE);
			ParseNode right = parseExpression();
			expect(Punctuator.CLOSE_SQUARE);
			Token indexingToken = LextantToken.make(bracketToken, bracketToken.getLexeme(), Punctuator.INDEXING);
			left = OperatorNode.withChildren(indexingToken, left, right);
		}

		return left;
	}

	private boolean startsUnaryExpression(Token token) {
		return token.isLextant(Punctuator.SUBTRACT, Punctuator.ADD, Punctuator.NOT, Keyword.LENGTH, Keyword.LOW,
				Keyword.HIGH) || startsIndexingExpression(token);
	}

	private boolean startsIndexingExpression(Token token) {
		return startsAtomicExpression(token);
	}

	// literal -> intConst | floatConst | identifier | booleanConstant | char |
	// stringConstant
	private ParseNode parseLiteral() {
		if (!startsLiteral(nowReading)) {
			return syntaxErrorNode("literal");
		}
		if (startsCharLiteral(nowReading)) {
			return parseCharLiteral();
		}

		if (startsStringLiteral(nowReading)) {
			return parseStringLiteral();
		}

		if (startsIntLiteral(nowReading)) {
			return parseIntLiteral();
		}
		if (startsFloatLiteral(nowReading)) {
			return parseFloatLiteral();
		}
		if (startsIdentifier(nowReading)) {
			return parseIdentifier();
		}
		if (startsBooleanLiteral(nowReading)) {
			return parseBooleanLiteral();
		}
		if (startsBreakStatement(nowReading)) {
			return parseBreakStatement();
		}
		if (startsContinueStatement(nowReading)) {
			return parseContinueStatement();
		}
		if (startsParanthesisExpression(nowReading)) {
			return parseParanthesisExpression();
		}

		return syntaxErrorNode("literal");
	}

	private boolean startsLiteral(Token token) {
		return startsIntLiteral(token) || startsFloatLiteral(token) || startsIdentifier(token)
				|| startsBooleanLiteral(token) || startsCharLiteral(token) || startsStringLiteral(token)
				|| startsBreakStatement(token) || startsContinueStatement(token) || startsParanthesisExpression(token);
	}

	// break Statement
	private ParseNode parseBreakStatement() {
		Token breakToken = nowReading;
		readToken();
		expect(Punctuator.TERMINATOR);
		return new BreakNode(breakToken);
	}

	private boolean startsBreakStatement(Token token) {
		return token.isLextant(Keyword.BREAK);
	}

	private ParseNode parseContinueStatement() {
		Token breakToken = nowReading;
		readToken();
		expect(Punctuator.TERMINATOR);
		return new ContinueNode(breakToken);
	}

	private boolean startsContinueStatement(Token token) {
		return token.isLextant(Keyword.CONTINUE);
	}

	// integer (literal)
	private ParseNode parseIntLiteral() {
		if (!startsIntLiteral(nowReading)) {
			return syntaxErrorNode("integer constant");
		}
		readToken();
		return new IntegerConstantNode(previouslyRead);
	}

	private boolean startsIntLiteral(Token token) {
		return token instanceof IntegerConstantToken;
	}

	// char (literal)
	private ParseNode parseCharLiteral() {
		if (!startsCharLiteral(nowReading)) {
			return syntaxErrorNode("character constant");
		}
		readToken();
		return new CharConstantNode(previouslyRead);
	}

	private boolean startsCharLiteral(Token token) {
		return token instanceof CharConstantToken;
	}

	// string (literal)

	private ParseNode parseStringLiteral() {
		if (!startsStringLiteral(nowReading)) {
			return syntaxErrorNode("string constant");
		}
		readToken();
		return new StringConstantNode(previouslyRead);
	}

	private boolean startsStringLiteral(Token token) {
		return token instanceof StringConstantToken;
	}

	// floating (literal)
	private ParseNode parseFloatLiteral() {
		if (!startsFloatLiteral(nowReading)) {
			return syntaxErrorNode("floating constant");
		}
		readToken();
		return new FloatingConstantNode(previouslyRead);
	}

	private boolean startsFloatLiteral(Token token) {
		return token instanceof FloatingConstantToken;
	}

	// identifier (terminal)
	private ParseNode parseIdentifier() {
		if (!startsIdentifier(nowReading)) {
			return syntaxErrorNode("identifier");
		}
		readToken();
		if (nowReading.isLextant(Punctuator.OPEN_PARANTHESIS)) {
			return parseFunctionInvocation();
		}
		return new IdentifierNode(previouslyRead);
	}

	private boolean startsIdentifier(Token token) {
		return token instanceof IdentifierToken;
	}

	// Func definition parseID

	private ParseNode parseFuncIdentifier() {
		if (!startsIdentifier(nowReading)) {
			return syntaxErrorNode("identifier");
		}
		readToken();
		return new IdentifierNode(previouslyRead);
	}

	// Function Invocation

	private ParseNode parseFunctionInvocation() {
		ParseNode identifier = new IdentifierNode(previouslyRead);
		readToken();
		ParseNode exprList = parseExprList();
		expect(Punctuator.CLOSE_PARANTHESIS);
		// expect(Punctuator.TERMINATOR);
		return FunctionInvocationNode.withChildren(nowReading, identifier, exprList);
	}

	// boolean literal
	private ParseNode parseBooleanLiteral() {
		if (!startsBooleanLiteral(nowReading)) {
			return syntaxErrorNode("boolean constant");
		}
		readToken();
		return new BooleanConstantNode(previouslyRead);
	}

	private boolean startsBooleanLiteral(Token token) {
		return token.isLextant(Keyword.TRUE, Keyword.FALSE);
	}

	private void readToken() {
		previouslyRead = nowReading;
		nowReading = scanner.next();
	}

	// if the current token is one of the given lextants, read the next token.
	// otherwise, give a syntax error and read next token (to avoid endless
	// looping).
	private void expect(Lextant... lextants) {
		if (!nowReading.isLextant(lextants)) {
			syntaxError(nowReading, "expecting " + Arrays.toString(lextants));
		}
		readToken();
	}

	private ErrorNode syntaxErrorNode(String expectedSymbol) {
		syntaxError(nowReading, "expecting " + expectedSymbol);
		ErrorNode errorNode = new ErrorNode(nowReading);
		readToken();
		return errorNode;
	}

	private void syntaxError(Token token, String errorDescription) {
		String message = "" + token.getLocation() + " " + errorDescription;
		error(message);
	}

	private void error(String message) {
		BilbyLogger log = BilbyLogger.getLogger("compiler.Parser");
		log.severe("syntax error: " + message);
	}
}