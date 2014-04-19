package parser;

import parser.semanticAnalysis.SemanticValidator;
import parser.semanticAnalysis.abstractSyntaxTree.conditions.Condition;
import parser.semanticAnalysis.abstractSyntaxTree.conditions.relation.Relation;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.*;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.Number;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Field;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Location;
import parser.semanticAnalysis.abstractSyntaxTree.instructions.*;
import parser.semanticAnalysis.symbolTable.declarations.Declaration;
import parser.semanticAnalysis.symbolTable.declarations.constants.constant.Constant;
import parser.semanticAnalysis.symbolTable.declarations.constants.constant.ConstantBuilder;
import parser.semanticAnalysis.symbolTable.declarations.procedures.Argument;
import parser.semanticAnalysis.symbolTable.declarations.procedures.Parameter;
import parser.semanticAnalysis.symbolTable.declarations.procedures.Procedure;
import parser.semanticAnalysis.symbolTable.declarations.procedures.ProcedureBuilder;
import parser.semanticAnalysis.symbolTable.declarations.types.*;
import parser.semanticAnalysis.symbolTable.declarations.types.array.ArrayBuilder;
import parser.semanticAnalysis.symbolTable.declarations.types.record.RecordBuilder;
import parser.semanticAnalysis.symbolTable.declarations.variable.InvalidVariable;
import parser.semanticAnalysis.symbolTable.declarations.variable.Variable;
import parser.semanticAnalysis.symbolTable.declarations.variable.VariableBuilder;
import parser.semanticAnalysis.symbolTable.scope.ScopeManager;
import parser.syntacticAnalysis.ProductionRule;
import parser.syntacticAnalysis.SyntacticValidator;
import parser.syntacticAnalysis.events.ParserEvent;
import parser.syntacticAnalysis.events.dispatcher.ParserEventDispatcher;
import parser.utilities.DesignatorType;
import parser.utilities.ExpressionParser;
import parser.utilities.TokenIterator;
import shared.Reserved;
import shared.Token;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Reads the source program as a sequence of tokens and recognizes its structure in the form of a parse tree.
 */
public class Parser extends ParserEventDispatcher {

	/**
	 * Represents the instance of the parser.
	 */
	private static Parser m_instance;

	/**
	 * Represents the list of instructions in the form of an abstract syntax tree.
	 */
	private List<Instruction> m_instructions;

	/**
	 * Constructs the instance of the parser.
	 */
	private Parser() {
		m_instructions = new ArrayList<Instruction>();
	}

	/**
	 * Gets the instance of the parser.
	 */
	public static Parser getInstance() {
		if (m_instance == null) {
			m_instance = new Parser();
		}
		return m_instance;
	}

	/**
	 * Sets the tokens to be parsed.
	 *
	 * @param tokens The tokens to be parsed.
	 */
	public void setTokens(List<Token> tokens) {
		TokenIterator.getInstance().setTokens(tokens);
	}

	/**
	 * Gets the abstract syntax tree in the form of a list of instructions.
	 */
	public List<Instruction> getAbstractSyntaxTree() {
		return m_instructions;
	}

	/**
	 * Parses the set tokens.
	 *
	 * @return true if no exceptions were encountered; otherwise, false.
	 */
	public boolean parseTokens() {
		matchProgram();
		SemanticValidator.getInstance().validateProcedures();
		return !(SyntacticValidator.getInstance().encounteredExceptions() || SemanticValidator.getInstance().encounteredExceptions());
	}

	/**
	 * Matches the grammatical correctness of the program.
	 */
	private void matchProgram() {
		dispatchEvent(ParserEvent.BEGAN_PARSING_NON_TERMINAL_SYMBOL, "Program");

		SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Keyword.PROGRAM);
		SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Token.Type.IDENTIFIER);
		SemanticValidator.getInstance().validateProgramIdentifier(TokenIterator.getInstance().getCurrent());
		SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Symbol.SEMICOLON);

		matchDeclarations();
		if (SyntacticValidator.getInstance().isNextToken(ProductionRule.OPTIONAL, Reserved.Keyword.BEGIN)) {
			SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Keyword.BEGIN);
			m_instructions = matchInstructions();
		}

		SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Keyword.END);
		SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Token.Type.IDENTIFIER);
		SemanticValidator.getInstance().validateProgramIdentifier(TokenIterator.getInstance().getCurrent());
		SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Symbol.PERIOD);

		dispatchEvent(ParserEvent.FINISHED_PARSING_NON_TERMINAL_SYMBOL, "Program");
	}

	/**
	 * Matches the grammatical correctness of the declarations.
	 */
	private void matchDeclarations() {
		dispatchEvent(ParserEvent.BEGAN_PARSING_NON_TERMINAL_SYMBOL, "Declarations");

		while (SyntacticValidator.getInstance().isNextToken(ProductionRule.OPTIONAL, Reserved.Keyword.CONST, Reserved.Keyword.TYPE, Reserved.Keyword.VAR, Reserved.Keyword.PROCEDURE)) {
			if (SyntacticValidator.getInstance().isNextToken(ProductionRule.OPTIONAL, Reserved.Keyword.CONST)) {
				matchConstantDeclarations();
			} else if (SyntacticValidator.getInstance().isNextToken(ProductionRule.OPTIONAL, Reserved.Keyword.TYPE)) {
				matchTypeDeclarations();
			} else if (SyntacticValidator.getInstance().isNextToken(ProductionRule.OPTIONAL, Reserved.Keyword.VAR)) {
				matchVariableDeclarations();
			} else if (SyntacticValidator.getInstance().isNextToken(ProductionRule.OPTIONAL, Reserved.Keyword.PROCEDURE)) {
				matchProcedureDeclarations();
			}
		}

		dispatchEvent(ParserEvent.FINISHED_PARSING_NON_TERMINAL_SYMBOL, "Declarations");
	}

	/**
	 * Matches the grammatical correctness of constant declarations.
	 */
	private void matchConstantDeclarations() {
		dispatchEvent(ParserEvent.BEGAN_PARSING_NON_TERMINAL_SYMBOL, "ConstDecl");

		SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Keyword.CONST);
		while (SyntacticValidator.getInstance().isNextToken(ProductionRule.OPTIONAL, Token.Type.IDENTIFIER)) {
			SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Token.Type.IDENTIFIER);
			int startingIndex = TokenIterator.getInstance().getIndex();
			ConstantBuilder.getInstance().startBuilding();
			ConstantBuilder.getInstance().setName(TokenIterator.getInstance().getCurrent().getValue().toString());

			SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Symbol.EQUALS);
			ConstantBuilder.getInstance().setValue(SemanticValidator.getInstance().validateExpressionIsAConstant(matchExpression()));
			ConstantBuilder.getInstance().setTokens(TokenIterator.getInstance().getRange(startingIndex, TokenIterator.getInstance().getIndex()));
			SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Symbol.SEMICOLON);

			ScopeManager.getInstance().getCurrentScope().insert(ConstantBuilder.getInstance().getDeclaration().getName(), ConstantBuilder.getInstance().getDeclaration());
			ConstantBuilder.getInstance().reset();
		}

		dispatchEvent(ParserEvent.FINISHED_PARSING_NON_TERMINAL_SYMBOL, "ConstDecl");
	}

	/**
	 * Matches the grammatical correctness of type declarations.
	 */
	private void matchTypeDeclarations() {
		dispatchEvent(ParserEvent.BEGAN_PARSING_NON_TERMINAL_SYMBOL, "TypeDecl");

		SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Keyword.TYPE);
		while (SyntacticValidator.getInstance().isNextToken(ProductionRule.OPTIONAL, Token.Type.IDENTIFIER)) {
			SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Token.Type.IDENTIFIER);
			String name = TokenIterator.getInstance().getCurrent().getValue().toString();

			SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Symbol.EQUALS);
			ScopeManager.getInstance().getCurrentScope().insert(name, matchType(name));

			SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Symbol.SEMICOLON);
		}

		dispatchEvent(ParserEvent.FINISHED_PARSING_NON_TERMINAL_SYMBOL, "TypeDecl");
	}

	/**
	 * Matches the grammatical correctness of variable declarations.
	 */
	private void matchVariableDeclarations() {
		dispatchEvent(ParserEvent.BEGAN_PARSING_NON_TERMINAL_SYMBOL, "VarDecl");

		SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Keyword.VAR);
		while (SyntacticValidator.getInstance().isNextToken(ProductionRule.OPTIONAL, Token.Type.IDENTIFIER)) {
			List<Token> identifiers = matchIdentifierList();
			SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Symbol.COLON);
			VariableBuilder.getInstance().insertVariablesIntoSymbolTable(identifiers, matchType(TokenIterator.getInstance().getName(identifiers)));
			SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Symbol.SEMICOLON);
		}

		dispatchEvent(ParserEvent.FINISHED_PARSING_NON_TERMINAL_SYMBOL, "VarDecl");
	}

	/**
	 * Matches the grammatical correctness of procedure declarations.
	 */
	private void matchProcedureDeclarations() {
		dispatchEvent(ParserEvent.BEGAN_PARSING_NON_TERMINAL_SYMBOL, "ProcDecl");

		boolean hasReturnType = false;

		ProcedureBuilder.getInstance().startBuilding();

		SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Keyword.PROCEDURE);
		SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Token.Type.IDENTIFIER);
		String name = TokenIterator.getInstance().getCurrent().getValue().toString();
		ProcedureBuilder.getInstance().setName(name);
		if (ProcedureBuilder.getInstance().containsPromise(name)) {
			ProcedureBuilder.getInstance().swap(ProcedureBuilder.getInstance().getPromise(name));
		} else {
			ScopeManager.getInstance().getProgramScope().insert(name, ProcedureBuilder.getInstance().peekDeclaration());
		}

		SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Symbol.OPENING_PARENTHESIS);
		if (SyntacticValidator.getInstance().isNextToken(ProductionRule.OPTIONAL, Token.Type.IDENTIFIER)) {
			ProcedureBuilder.getInstance().setParameters(matchFormals(name));
		}

		SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Symbol.CLOSING_PARENTHESIS);

		if (SyntacticValidator.getInstance().isNextToken(ProductionRule.OPTIONAL, Reserved.Symbol.COLON)) {
			SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Symbol.COLON);
			SemanticValidator.getInstance().validateIsOfTypeInteger(TokenIterator.getInstance().peek().getValue().toString());
			ProcedureBuilder.getInstance().setReturnType(matchType(name));
			hasReturnType = true;
		}
		SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Symbol.SEMICOLON);

		LinkedHashMap<List<Token>, Type> localVariables = new LinkedHashMap<List<Token>, Type>();
		while (SyntacticValidator.getInstance().isNextToken(ProductionRule.OPTIONAL, Reserved.Keyword.VAR)) {
			SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Keyword.VAR);
			while (SyntacticValidator.getInstance().isNextToken(ProductionRule.OPTIONAL, Token.Type.IDENTIFIER)) {
				List<Token> identifiers = matchIdentifierList();
				SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Symbol.COLON);
				localVariables.put(identifiers, matchType(TokenIterator.getInstance().getName(identifiers)));
				SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Symbol.SEMICOLON);
			}
		}
		ProcedureBuilder.getInstance().setLocalVariables(localVariables);

		if (SyntacticValidator.getInstance().isNextToken(ProductionRule.OPTIONAL, Reserved.Keyword.BEGIN)) {
			SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Keyword.BEGIN);
			ProcedureBuilder.getInstance().setInstructions(matchInstructions());
		}

		if (SyntacticValidator.getInstance().isNextToken(ProductionRule.OPTIONAL, Reserved.Keyword.RETURN)) {
			SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Keyword.RETURN);
			Expression expression = matchExpression();
			SemanticValidator.getInstance().validateExpressionIsOfTypeInteger(expression);
			ProcedureBuilder.getInstance().setReturnExpression(expression);
		} else if (hasReturnType) {
			SemanticValidator.getInstance().validateProcedureHasReturnExpression(ProcedureBuilder.getInstance().getDeclaration());
		}

		SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Keyword.END);
		SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Token.Type.IDENTIFIER);
		String name1 = TokenIterator.getInstance().getCurrent().getValue().toString();
		SemanticValidator.getInstance().validateProcedureIdentifier(name, name1);
		SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Symbol.SEMICOLON);

		ProcedureBuilder.getInstance().getDeclaration();

		ProcedureBuilder.getInstance().reset();
		if (ProcedureBuilder.getInstance().containsPromise(name)) {
			ProcedureBuilder.getInstance().finalizeSwap(ProcedureBuilder.getInstance().getPromise(name));
		}
		dispatchEvent(ParserEvent.FINISHED_PARSING_NON_TERMINAL_SYMBOL, "ProcDecl");
	}

	/**
	 * Matches the grammatical correctness of formals.
	 *
	 * @param name The name of the procedure, whose formals are being matched.
	 */
	private LinkedHashMap<List<Token>, Type> matchFormals(String name) {
		LinkedHashMap<List<Token>, Type> formals = new LinkedHashMap<List<Token>, Type>();

		do {
			if (SyntacticValidator.getInstance().isNextToken(ProductionRule.OPTIONAL, Reserved.Symbol.SEMICOLON)) {
				SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Symbol.SEMICOLON);
			}
			List<Token> formalList = matchIdentifierList();
			SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Symbol.COLON);
			formals.put(formalList, matchType(name));
		} while (SyntacticValidator.getInstance().isNextToken(ProductionRule.OPTIONAL, Reserved.Symbol.SEMICOLON));

		return formals;
	}

	/**
	 * Matches the grammatical correctness of a selector.
	 *
	 * @param name The name of the type.
	 */
	private Type matchType(String name) {
		dispatchEvent(ParserEvent.BEGAN_PARSING_NON_TERMINAL_SYMBOL, "Type");

		Type type = new InvalidType();
		int startingIndex = TokenIterator.getInstance().getIndex() - 2;
		if (SyntacticValidator.getInstance().isNextToken(ProductionRule.REQUIRED, Token.Type.IDENTIFIER, Reserved.Keyword.ARRAY, Reserved.Keyword.RECORD)) {
			if (SyntacticValidator.getInstance().isNextToken(ProductionRule.OPTIONAL, Token.Type.IDENTIFIER)) {
				SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Token.Type.IDENTIFIER);
				type = SemanticValidator.getInstance().validateDeclarationIsAType(ScopeManager.getInstance().getCurrentScope().find(TokenIterator.getInstance().getCurrent().getValue().toString()));
			} else if (SyntacticValidator.getInstance().isNextToken(ProductionRule.OPTIONAL, Reserved.Keyword.ARRAY)) {
				SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Keyword.ARRAY);

				ArrayBuilder.getInstance().startBuilding();
				ArrayBuilder.getInstance().setName(name);
				ArrayBuilder.getInstance().setLength(SemanticValidator.getInstance().validateExpressionIsAPositiveConstant(matchExpression()));

				SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Keyword.OF);
				ArrayBuilder.getInstance().setElementType(SemanticValidator.getInstance().validateDeclarationIsAType(matchType(name + " ARRAY OF")));
				ArrayBuilder.getInstance().setTokens(TokenIterator.getInstance().getRange(startingIndex, TokenIterator.getInstance().getIndex()));

				type = ArrayBuilder.getInstance().getDeclaration(); // No need to reset array builder, since each call to getDeclaration() pops an array off the stack (i.e. it is guaranteed to be empty).
			} else {
				SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Keyword.RECORD);

				RecordBuilder.getInstance().startBuilding();
				RecordBuilder.getInstance().setName(name);
				while (SyntacticValidator.getInstance().isNextToken(ProductionRule.OPTIONAL, Token.Type.IDENTIFIER)) {
					List<Token> identifiers = matchIdentifierList();
					SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Symbol.COLON);
					VariableBuilder.getInstance().insertVariablesIntoSymbolTable(identifiers, matchType(TokenIterator.getInstance().getName(identifiers)));
					SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Symbol.SEMICOLON);
				}
				SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Keyword.END);
				RecordBuilder.getInstance().setTokens(TokenIterator.getInstance().getRange(startingIndex, TokenIterator.getInstance().getIndex()));

				type = RecordBuilder.getInstance().getDeclaration();
				RecordBuilder.getInstance().reset();
			}
		}

		dispatchEvent(ParserEvent.FINISHED_PARSING_NON_TERMINAL_SYMBOL, "Type");
		return type;
	}

	/**
	 * Matches the grammatical correctness of an expression.
	 */
	private Expression matchExpression() {
		dispatchEvent(ParserEvent.BEGAN_PARSING_NON_TERMINAL_SYMBOL, "Expression");
		dispatchEvent(ParserEvent.FINISHED_PARSING_NON_TERMINAL_SYMBOL, "Expression");
		return ExpressionParser.getInstance().parseExpression("matchTerm", true, Reserved.Symbol.PLUS, Reserved.Symbol.MINUS);
	}

	/**
	 * Matches the grammatical correctness of a term.
	 */
	@SuppressWarnings("unused") // matchTerm() is invoked dynamically via ExpressionParser
	private Expression matchTerm() {
		dispatchEvent(ParserEvent.BEGAN_PARSING_NON_TERMINAL_SYMBOL, "Term");
		dispatchEvent(ParserEvent.FINISHED_PARSING_NON_TERMINAL_SYMBOL, "Term");
		return ExpressionParser.getInstance().parseExpression("matchFactor", false, Reserved.Symbol.ASTERISK, Reserved.Keyword.DIV, Reserved.Keyword.MOD);
	}

	/**
	 * Matches the grammatical correctness of a factor.
	 */
	@SuppressWarnings("unused") // matchFactor() is invoked dynamically via ExpressionParser
	private Expression matchFactor() {
		dispatchEvent(ParserEvent.BEGAN_PARSING_NON_TERMINAL_SYMBOL, "Factor");

		Expression expression = null;
		if (SyntacticValidator.getInstance().isNextToken(ProductionRule.REQUIRED, Token.Type.INTEGER, Token.Type.IDENTIFIER, Reserved.Symbol.OPENING_PARENTHESIS)) {
			if (SyntacticValidator.getInstance().isNextToken(ProductionRule.OPTIONAL, Token.Type.INTEGER)) {
				SyntacticValidator.getInstance().matchNextToken(ProductionRule.OPTIONAL, Token.Type.INTEGER);
				expression = new Number(new Constant((java.lang.Integer) TokenIterator.getInstance().getCurrent().getValue())); // TODO remove this cast.
			} else if (SyntacticValidator.getInstance().isNextToken(ProductionRule.OPTIONAL, Token.Type.IDENTIFIER)) {
				if (ScopeManager.getInstance().getCurrentScope().find(TokenIterator.getInstance().peek().getValue().toString()) instanceof Procedure) {
					Procedure procedure = (Procedure) ScopeManager.getInstance().getCurrentScope().find(TokenIterator.getInstance().peek().getValue().toString());
					SemanticValidator.getInstance().validateProcedureHasReturnExpression(procedure);
					expression = matchFunction();
				} else {
					expression = matchDesignator(DesignatorType.FACTOR);
				}
			} else {
				SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Symbol.OPENING_PARENTHESIS);
				expression = matchExpression();
				SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Symbol.CLOSING_PARENTHESIS);
			}
		}

		dispatchEvent(ParserEvent.FINISHED_PARSING_NON_TERMINAL_SYMBOL, "Factor");
		return expression;
	}

	/**
	 * Matches the grammatical correctness of a designator.
	 *
	 * @param designatorType The type of the designator
	 */
	private Expression matchDesignator(DesignatorType designatorType) {
		dispatchEvent(ParserEvent.BEGAN_PARSING_NON_TERMINAL_SYMBOL, "Designator");

		SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Token.Type.IDENTIFIER);
		SemanticValidator.getInstance().validateDeclarationIsNotAType(SemanticValidator.getInstance().validateDeclarationWasPreviouslyDeclared(TokenIterator.getInstance().getCurrent()));
		Declaration declaration = ScopeManager.getInstance().getCurrentScope().find(TokenIterator.getInstance().getCurrent().getValue().toString());
		SemanticValidator.getInstance().validateDesignatorIdentifier(declaration, designatorType);

		Expression expression = new InvalidExpression();

		if (declaration instanceof Variable) {
			Variable variable = (Variable) declaration;
			if (declaration instanceof Parameter) {
				Parameter parameter = (Parameter) declaration;
				return new parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Variable(parameter, parameter.getType());
			}
			Location location = matchSelector(variable);
			if (location == null) {
				if (designatorType == DesignatorType.READ) {
					SemanticValidator.getInstance().validateDeclarationIsAVariableWhoseTypeIsInteger(variable);
				}
				expression = new parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Variable(variable, variable.getType());
			} else {
				if (designatorType == DesignatorType.READ) {
					SemanticValidator.getInstance().validateDeclarationIsAVariableWhoseTypeIsInteger(location);
				}
				expression = location;
			}
		} else if (declaration instanceof Constant && designatorType == DesignatorType.FACTOR) {
			return new Number(new Constant(((Constant) declaration).getValue()));
		} else {
			matchSelector(new InvalidVariable());// TODO ELSE (DISABLED)
		}

		dispatchEvent(ParserEvent.FINISHED_PARSING_NON_TERMINAL_SYMBOL, "Designator");
		return expression;
	}

	/**
	 * Matches the grammatical correctness of a selector.
	 *
	 * @param variable The variable from which to select.
	 */
	private Location matchSelector(parser.semanticAnalysis.symbolTable.declarations.variable.Variable variable) {
		dispatchEvent(ParserEvent.BEGAN_PARSING_NON_TERMINAL_SYMBOL, "Selector");

		Location location = null;
		while (SyntacticValidator.getInstance().isNextToken(ProductionRule.OPTIONAL, Reserved.Symbol.OPENING_BRACKET, Reserved.Symbol.PERIOD)) {
			if (SyntacticValidator.getInstance().isNextToken(ProductionRule.OPTIONAL, Reserved.Symbol.OPENING_BRACKET)) {
				SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Symbol.OPENING_BRACKET);
				location = SemanticValidator.getInstance().validateVariableIsNestedArray(variable, matchExpressionList(), location);
				SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Symbol.CLOSING_BRACKET);
				//TODO Update variable with
			} else {
				SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Symbol.PERIOD);
				SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Token.Type.IDENTIFIER);
				Variable selectedVariable = SemanticValidator.getInstance().validateRecordHasField(SemanticValidator.getInstance().validateVariableIsARecord(variable), TokenIterator.getInstance().getCurrent().getValue().toString());
				if (location == null) {
					location = new Field(new parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Variable(variable, variable.getType()), new parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Variable(selectedVariable, selectedVariable.getType()), selectedVariable.getType());
				} else {
					location = new Field(location, new parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Variable(selectedVariable, selectedVariable.getType()), selectedVariable.getType());
				}
				variable = selectedVariable;
			}
		}

		dispatchEvent(ParserEvent.FINISHED_PARSING_NON_TERMINAL_SYMBOL, "Selector");
		return location; //TODO HANDLE NULL RETURN
	}

	/**
	 * Matches the grammatical correctness of an expression list.
	 */
	private List<Expression> matchExpressionList() {
		dispatchEvent(ParserEvent.BEGAN_PARSING_NON_TERMINAL_SYMBOL, "ExpressionList");

		List<Expression> expressions = new ArrayList<Expression>();
		expressions.add(matchExpression());
		while (SyntacticValidator.getInstance().isNextToken(ProductionRule.OPTIONAL, Reserved.Symbol.COMMA)) {
			SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Symbol.COMMA);
			expressions.add(matchExpression());
		}

		dispatchEvent(ParserEvent.FINISHED_PARSING_NON_TERMINAL_SYMBOL, "ExpressionList");

		return expressions;
	}

	/**
	 * Matches the grammatical correctness of the instructions.
	 */
	private List<Instruction> matchInstructions() {
		dispatchEvent(ParserEvent.BEGAN_PARSING_NON_TERMINAL_SYMBOL, "Instructions");
		List<Instruction> instructions = new ArrayList<Instruction>();
		Instruction instruction = matchInstruction();
		instructions.add(instruction);
		while (SyntacticValidator.getInstance().isNextToken(ProductionRule.OPTIONAL, Reserved.Symbol.SEMICOLON)) {
			SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Symbol.SEMICOLON);
			Instruction nextInstruction = matchInstruction();
			instruction.setNextInstruction(nextInstruction);
			instructions.add(nextInstruction);
			instruction = nextInstruction;
		}

		dispatchEvent(ParserEvent.FINISHED_PARSING_NON_TERMINAL_SYMBOL, "Instructions");
		return instructions;
	}

	/**
	 * Matches the grammatical correctness of an instruction.
	 */
	private Instruction matchInstruction() {
		dispatchEvent(ParserEvent.BEGAN_PARSING_NON_TERMINAL_SYMBOL, "Instruction");

		Instruction instruction = null; // TODO Invalid instruction.
		if (SyntacticValidator.getInstance().isNextToken(ProductionRule.REQUIRED, Token.Type.IDENTIFIER, Reserved.Keyword.IF, Reserved.Keyword.REPEAT, Reserved.Keyword.WHILE, Reserved.Keyword.READ, Reserved.Keyword.WRITE)) {
			if (SyntacticValidator.getInstance().isNextToken(ProductionRule.OPTIONAL, Token.Type.IDENTIFIER)) {
				if (ScopeManager.getInstance().getCurrentScope().find(TokenIterator.getInstance().peek().getValue().toString()) instanceof Procedure || TokenIterator.getInstance().peek(2).getValue().toString().equals(Reserved.Symbol.OPENING_PARENTHESIS.toString())) {
					instruction = matchCall();
				} else {
					instruction = matchAssign();
				}
			} else if (SyntacticValidator.getInstance().isNextToken(ProductionRule.OPTIONAL, Reserved.Keyword.IF)) {
				instruction = matchIf();
			} else if (SyntacticValidator.getInstance().isNextToken(ProductionRule.OPTIONAL, Reserved.Keyword.REPEAT)) {
				instruction = matchRepeat();
			} else if (SyntacticValidator.getInstance().isNextToken(ProductionRule.OPTIONAL, Reserved.Keyword.WHILE)) {
				instruction = matchWhile();
			} else if (SyntacticValidator.getInstance().isNextToken(ProductionRule.OPTIONAL, Reserved.Keyword.READ)) {
				instruction = matchRead();
			} else {
				instruction = matchWrite();
			}
		}

		dispatchEvent(ParserEvent.FINISHED_PARSING_NON_TERMINAL_SYMBOL, "Instruction");
		return instruction;
	}

	/**
	 * Matches the grammatical correctness of an assignment.
	 */
	private Assign matchAssign() {
		dispatchEvent(ParserEvent.BEGAN_PARSING_NON_TERMINAL_SYMBOL, "Assign");

		Location location = SemanticValidator.getInstance().validateExpressionIsALocation(matchDesignator(DesignatorType.ASSIGN));
		SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Symbol.COLON_EQUALS);
		Expression expression = matchExpression();
		SemanticValidator.getInstance().validateExpressionsAreOfTheSameType(location, expression);

		dispatchEvent(ParserEvent.FINISHED_PARSING_NON_TERMINAL_SYMBOL, "Assign");
		return new Assign(location, expression);
	}

	/**
	 * Matches the grammatical correctness of an if-statement.
	 */
	private If matchIf() {
		dispatchEvent(ParserEvent.BEGAN_PARSING_NON_TERMINAL_SYMBOL, "If");

		SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Keyword.IF);
		Condition condition = matchCondition();
		SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Keyword.THEN);
		List<Instruction> trueInstructions = matchInstructions();
		List<Instruction> falseInstructions = new ArrayList<Instruction>();
		if (SyntacticValidator.getInstance().isNextToken(ProductionRule.OPTIONAL, Reserved.Keyword.ELSE)) {
			SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Keyword.ELSE);
			falseInstructions = matchInstructions();
		}
		SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Keyword.END);

		dispatchEvent(ParserEvent.FINISHED_PARSING_NON_TERMINAL_SYMBOL, "If");
		return new If(condition, trueInstructions, falseInstructions);
	}

	/**
	 * Matches the grammatical correctness of a repeat-statement.
	 */
	private Repeat matchRepeat() {
		dispatchEvent(ParserEvent.BEGAN_PARSING_NON_TERMINAL_SYMBOL, "Repeat");

		SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Keyword.REPEAT);
		List<Instruction> instructions = matchInstructions();
		SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Keyword.UNTIL);
		Condition condition = matchCondition();
		condition.negateExpression(); // REPEAT UNTIL --> DO WHILE
		SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Keyword.END);

		dispatchEvent(ParserEvent.FINISHED_PARSING_NON_TERMINAL_SYMBOL, "Repeat");
		return new Repeat(condition, instructions);
	}

	/**
	 * Matches the grammatical correctness of a while-statement.
	 */
	private If matchWhile() {
		dispatchEvent(ParserEvent.BEGAN_PARSING_NON_TERMINAL_SYMBOL, "While");

		SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Keyword.WHILE);
		Condition condition = matchCondition();
		SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Keyword.DO);
		List<Instruction> instructions = new ArrayList<Instruction>();
		instructions.add(new Repeat(condition, matchInstructions()));
		SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Keyword.END);

		dispatchEvent(ParserEvent.FINISHED_PARSING_NON_TERMINAL_SYMBOL, "While");
		condition = condition.clone();
		return new If(condition, instructions);
	}

	/**
	 * Matches the grammatical correctness of a condition.
	 */
	private Condition matchCondition() {
		dispatchEvent(ParserEvent.BEGAN_PARSING_NON_TERMINAL_SYMBOL, "Condition");

		Expression leftExpression = matchExpression();
		SemanticValidator.getInstance().validateExpressionIsNumeric(leftExpression);
		SyntacticValidator.getInstance().matchNextToken(
				ProductionRule.REQUIRED,
				Reserved.Symbol.EQUALS,
				Reserved.Symbol.POUND,
				Reserved.Symbol.LESS_THAN,
				Reserved.Symbol.GREATER_THAN,
				Reserved.Symbol.LESS_THAN_OR_EQUAL_TO,
				Reserved.Symbol.GREATER_THAN_OR_EQUAL_TO
		);
		Relation relation = Relation.getRelation(TokenIterator.getInstance().getCurrent().getValue().toString());

		Expression rightExpression = matchExpression();
		SemanticValidator.getInstance().validateExpressionIsNumeric(rightExpression);

		dispatchEvent(ParserEvent.FINISHED_PARSING_NON_TERMINAL_SYMBOL, "Condition");

		return new Condition(leftExpression, rightExpression, relation);
	}

	/**
	 * Matches the grammatical correctness of a write-statement.
	 */
	private Write matchWrite() {
		dispatchEvent(ParserEvent.BEGAN_PARSING_NON_TERMINAL_SYMBOL, "Write");

		SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Keyword.WRITE);
		Expression expression = matchExpression();
		SemanticValidator.getInstance().validateExpressionIsNumeric(expression);

		dispatchEvent(ParserEvent.FINISHED_PARSING_NON_TERMINAL_SYMBOL, "Write");
		return new Write(expression);
	}

	/**
	 * Matches the grammatical correctness of a read-statement.
	 */
	private Read matchRead() {
		dispatchEvent(ParserEvent.BEGAN_PARSING_NON_TERMINAL_SYMBOL, "Read");

		SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Keyword.READ);
		Location location = SemanticValidator.getInstance().validateDeclarationIsAVariableWhoseTypeIsInteger(matchDesignator(DesignatorType.READ));

		dispatchEvent(ParserEvent.FINISHED_PARSING_NON_TERMINAL_SYMBOL, "Read");
		return new Read(location);
	}

	/**
	 * Matches the grammatical correctness of a call-statement.
	 */
	private Call matchCall() {
		dispatchEvent(ParserEvent.BEGAN_PARSING_NON_TERMINAL_SYMBOL, "Call");

		SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Token.Type.IDENTIFIER);
		Procedure procedure = SemanticValidator.getInstance().validateIsProcedure(TokenIterator.getInstance().getCurrent().getValue().toString(), false);
		SemanticValidator.getInstance().validateProcedureDoesNotHaveReturnExpression(procedure);
		SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Symbol.OPENING_PARENTHESIS);

		List<Argument> arguments = new ArrayList<Argument>();
		if (SyntacticValidator.getInstance().isNextToken(ProductionRule.OPTIONAL, Token.Type.IDENTIFIER) || SyntacticValidator.getInstance().isNextToken(ProductionRule.OPTIONAL, Token.Type.INTEGER)) {
			for (Expression expression : matchExpressionList()) {
				arguments.add(new Argument(expression));
			}
		}
		SemanticValidator.getInstance().validateProcedureArguments(procedure, arguments);

		SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Symbol.CLOSING_PARENTHESIS);
		dispatchEvent(ParserEvent.FINISHED_PARSING_NON_TERMINAL_SYMBOL, "Call");
		return new Call(arguments, procedure);
	}

	/**
	 * Matches the grammatical correctness of a function-statement.
	 */
	private Function matchFunction() {
		dispatchEvent(ParserEvent.BEGAN_PARSING_NON_TERMINAL_SYMBOL, "Function");

		SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Token.Type.IDENTIFIER);
		Procedure procedure = SemanticValidator.getInstance().validateIsProcedure(TokenIterator.getInstance().getCurrent().getValue().toString(), true);
		SemanticValidator.getInstance().validateProcedureHasReturnExpression(procedure);
		SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Symbol.OPENING_PARENTHESIS);

		List<Argument> arguments = new ArrayList<Argument>();
		if (SyntacticValidator.getInstance().isNextToken(ProductionRule.OPTIONAL, Token.Type.IDENTIFIER, Token.Type.INTEGER)) {
			for (Expression expression : matchExpressionList()) {
				arguments.add(new Argument(expression));
			}
		}
		SemanticValidator.getInstance().validateProcedureArguments(procedure, arguments);

		SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Symbol.CLOSING_PARENTHESIS);
		dispatchEvent(ParserEvent.FINISHED_PARSING_NON_TERMINAL_SYMBOL, "Function");
		return new Function(new Call(arguments, procedure));
	}

	/**
	 * Matches the grammatical correctness of an identifier list.
	 */
	private List<Token> matchIdentifierList() {
		dispatchEvent(ParserEvent.BEGAN_PARSING_NON_TERMINAL_SYMBOL, "IdentifierList");
		List<Token> identifiers = new ArrayList<Token>();

		SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Token.Type.IDENTIFIER);
		identifiers.add(TokenIterator.getInstance().getCurrent());
		while (SyntacticValidator.getInstance().isNextToken(ProductionRule.OPTIONAL, Reserved.Symbol.COMMA)) {
			SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Reserved.Symbol.COMMA);
			SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, Token.Type.IDENTIFIER);
			identifiers.add(TokenIterator.getInstance().getCurrent());
		}

		dispatchEvent(ParserEvent.FINISHED_PARSING_NON_TERMINAL_SYMBOL, "IdentifierList");
		return identifiers;
	}
}