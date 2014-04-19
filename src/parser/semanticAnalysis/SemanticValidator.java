package parser.semanticAnalysis;

import exception.Exception;
import exception.ExceptionHandler;
import exception.ExceptionStrength;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.Binary;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.Expression;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.Number;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Field;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Index;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.InvalidLocation;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Location;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.utilities.ConstantFolder;
import parser.semanticAnalysis.symbolTable.declarations.Declaration;
import parser.semanticAnalysis.symbolTable.declarations.InvalidDeclaration;
import parser.semanticAnalysis.symbolTable.declarations.constants.constant.ConstantBuilder;
import parser.semanticAnalysis.symbolTable.declarations.procedures.Argument;
import parser.semanticAnalysis.symbolTable.declarations.procedures.Procedure;
import parser.semanticAnalysis.symbolTable.declarations.procedures.ProcedureBuilder;
import parser.semanticAnalysis.symbolTable.declarations.types.Integer;
import parser.semanticAnalysis.symbolTable.declarations.types.InvalidType;
import parser.semanticAnalysis.symbolTable.declarations.types.Type;
import parser.semanticAnalysis.symbolTable.declarations.types.array.Array;
import parser.semanticAnalysis.symbolTable.declarations.types.array.ArrayBuilder;
import parser.semanticAnalysis.symbolTable.declarations.types.record.InvalidRecord;
import parser.semanticAnalysis.symbolTable.declarations.types.record.Record;
import parser.semanticAnalysis.symbolTable.declarations.types.record.RecordBuilder;
import parser.semanticAnalysis.symbolTable.declarations.variable.InvalidVariable;
import parser.semanticAnalysis.symbolTable.declarations.variable.Variable;
import parser.semanticAnalysis.symbolTable.declarations.variable.VariableBuilder;
import parser.semanticAnalysis.symbolTable.scope.ScopeManager;
import parser.syntacticAnalysis.SyntacticSynchronizationCoordinator;
import parser.utilities.DesignatorType;
import parser.utilities.TokenIterator;
import parser.utilities.Tuple;
import shared.Token;

import java.util.List;

/**
 * Matches and queries context conditions.
 */
public class SemanticValidator {

	/**
	 * Represents the semantic validator.
	 */
	private static SemanticValidator m_instance;
	/**
	 * Represents the program identifier.
	 */
	private String m_programIdentifier;
	/**
	 * Represents if an exception was encountered;
	 */
	private boolean m_encounteredExceptions;
	/**
	 * Represents if the semantic validator is enabled.
	 */
	private boolean m_isEnabled;

	/**
	 * Constructs the semantic validator.
	 */
	private SemanticValidator() {
		m_isEnabled = true;
	}

	/**
	 * Gets the instance of the semantic validator.
	 */
	public static SemanticValidator getInstance() {
		if (m_instance == null) {
			m_instance = new SemanticValidator();
		}
		return m_instance;
	}

	/**
	 * Gets if the semantic validator has been disabled.
	 */
	public boolean isEnabled() {
		return m_isEnabled && !SyntacticSynchronizationCoordinator.getInstance().isSynchronizing();
	}

	/**
	 * Disables the semantic validator.
	 */
	public void disable() {
		if (m_isEnabled) {
			ConstantBuilder.getInstance().disable();
			ArrayBuilder.getInstance().disable();
			RecordBuilder.getInstance().disable();
			VariableBuilder.getInstance().disable();
			ConstantFolder.getInstance().disable();
			ProcedureBuilder.getInstance().disable();
			m_isEnabled = false;
		}
	}

	/**
	 * Queries whether an exception was encountered.
	 */
	public boolean encounteredExceptions() {
		return m_isEnabled && m_encounteredExceptions;
	}

	/**
	 * Validates that program identifier is the same throughout the SIMPLE program.
	 *
	 * @param programIdentifier The program identifier.
	 */
	public void validateProgramIdentifier(Token programIdentifier) {
		if (!isEnabled()) {
			return;
		}
		if (m_programIdentifier == null) {
			m_programIdentifier = programIdentifier.getValue().toString();
		} else if (!m_programIdentifier.equals(programIdentifier.getValue().toString())) {
			m_encounteredExceptions = true;
			ExceptionHandler.getInstance().throwException(Exception.PROGRAM_IDENTIFIERS_DO_NOT_MATCH, ExceptionStrength.WEAK, m_programIdentifier, programIdentifier.getValue().toString());
		}
	}

	/**
	 * Validates that procedure identifier matches up.
	 *
	 * @param begin The beginning program identifier.
	 * @param end   The ending program identifier.
	 */
	public void validateProcedureIdentifier(String begin, String end) {
		if (!isEnabled()) {
			return;
		}
		if (begin == null || end == null || !begin.equals(end)) {
			m_encounteredExceptions = true;
			ExceptionHandler.getInstance().throwException(Exception.PROCEDURE_IDENTIFIERS_DO_NOT_MATCH, ExceptionStrength.WEAK, begin, end);
		}
	}


	/**
	 * Validates that a declaration was declared in the current scope.
	 *
	 * @param token The token associated with the declaration for which to test this condition.
	 */
	public Declaration validateDeclarationWasPreviouslyDeclared(Token token) {
		if (!isEnabled()) {
			return new InvalidDeclaration(token.getValue().toString()).setToken(token);
		}
		Declaration declaration = ScopeManager.getInstance().getCurrentScope().find(token.getValue().toString());
		if (declaration instanceof InvalidDeclaration) {
			m_encounteredExceptions = true;
			ExceptionHandler.getInstance().throwException(Exception.DECLARATION_NOT_PREVIOUSLY_DECLARED, ExceptionStrength.WEAK, token);
			return new InvalidDeclaration(token.getValue().toString()).setToken(token);
		}
		return declaration;
	}

	/**
	 * Validates that an expression is a location.
	 *
	 * @param expression The expression to validate.
	 */
	public Location validateExpressionIsALocation(Expression expression) {
		if (!isEnabled()) {
			return new InvalidLocation();
		}
		if (expression instanceof Location) {
			return (Location) expression;
		} else {
			m_encounteredExceptions = true;
			ExceptionHandler.getInstance().throwException(Exception.EXPRESSION_IS_NOT_A_LOCATION, ExceptionStrength.WEAK, expression);
			return new InvalidLocation();
		}
	}

	/**
	 * Validates that a declaration was already declared in the current scope.
	 *
	 * @param declaration The declaration for which to test this condition.
	 */
	public boolean validateDeclarationWasNotPreviouslyDeclared(Declaration declaration) {
		if (!isEnabled()) {
			return true;
		}
		if (ScopeManager.getInstance().getCurrentScope().isLocal(declaration.getName())) {
			m_encounteredExceptions = true;
			ExceptionHandler.getInstance().throwException(Exception.DECLARATION_PREVIOUSLY_DECLARED, ExceptionStrength.WEAK, declaration, ScopeManager.getInstance().getCurrentScope().find(declaration.getName()));
			return false;
		}
		return true;
	}

	/**
	 * Validates that a declaration is an array.
	 *
	 * @param declaration The declaration to validate.
	 */
	public boolean validateDeclarationIsAnArray(Declaration declaration) {
		if (!isEnabled()) {
			return true;
		}
		if (declaration instanceof Array) {
			return true;
		} else if (declaration instanceof Variable && ((Variable) declaration).getType() instanceof Array) {
			return true;
		}
		m_encounteredExceptions = true;
		ExceptionHandler.getInstance().throwException(Exception.DECLARATION_IS_NOT_AN_ARRAY, ExceptionStrength.WEAK, declaration);
		return false;
	}

	/**
	 * Validates that a variable is a nested array.
	 *
	 * @param variable       The variable to validate.
	 * @param expressionList The expression list to use in validating the nesting of the array.
	 * @param location       The location to write to.
	 */
	public Location validateVariableIsNestedArray(Variable variable, List<Expression> expressionList, Location location) {
		if (!isEnabled()) {
			return new InvalidLocation();
		}
		int depthCounter = 0;
		boolean error = false;
		for (Expression expression : expressionList) {
			SemanticValidator.getInstance().validateDeclarationIsAnArray(variable.getType());
			if (location == null) {
				SemanticValidator.getInstance().validateDeclarationIsAnArray(variable);
				Type type = null;
				if (variable.getType() instanceof Array) {
					Array array = (Array) variable.getType();
					type = array.getElementType();
				} else {
					type = expression.getType();
				}
				location = new Index(new parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Variable(variable, variable.getType()), expression, type);
			} else {
				depthCounter++;
				Type type = variable.getType();
				for (int i = 0; i < depthCounter; i++) {
					if (type instanceof Array) {
						Array array = (Array) type;
						type = array.getElementType();
						if (type instanceof Array) {
							Array arary1 = (Array) type;
							type = arary1.getElementType();
						} else {
							type = expression.getType();
						}
					} else {
						error = true;
						break;
					}
				}
				location = new Index(location, expression, type);
			}
		}
		if (error) {
			m_encounteredExceptions = true;
			ExceptionHandler.getInstance().throwException(Exception.VARIABLE_IS_NOT_A_NESTED_ARRAY, ExceptionStrength.WEAK, variable, depthCounter + 1);
		}
		return location;
	}


	/**
	 * Validates that a declaration is a record.
	 *
	 * @param variable The variable to validate.
	 */
	public Record validateVariableIsARecord(Variable variable) {
		if (!isEnabled()) {
			return new InvalidRecord();
		}
		if (variable.getType() instanceof Record) {
			return (Record) variable.getType();
		} else {
			if (variable.getType() instanceof Array) {
				Array array = (Array) variable.getType();
				if (array.getElementType() instanceof Record) {
					return (Record) array.getElementType();
				}
			}
			m_encounteredExceptions = true;
			ExceptionHandler.getInstance().throwException(Exception.VARIABLE_IS_NOT_A_RECORD, ExceptionStrength.WEAK, variable);
			return new InvalidRecord();
		}
	}

	/**
	 * Validates that a record has a field.
	 *
	 * @param record The record to validate.
	 * @param field  The field to validate.
	 */
	public Variable validateRecordHasField(Record record, String field) {
		if (!isEnabled()) {
			return new InvalidVariable();
		}
		Variable selectedVariable;
		if (record.getScope().find(field) instanceof InvalidDeclaration || record.getScope().find(field) instanceof InvalidVariable) {
			m_encounteredExceptions = true;
			ExceptionHandler.getInstance().throwException(Exception.RECORD_DOES_NOT_CONTAIN_FIELD, ExceptionStrength.WEAK, record, field);
			selectedVariable = new InvalidVariable();
		} else {
			selectedVariable = (Variable) record.getScope().find(field);
		}
		return selectedVariable;
	}

	/**
	 * Validates that an expression is a constant.
	 *
	 * @param expression The expression to validate.
	 */
	public int validateExpressionIsAConstant(Expression expression) {
		if (!isEnabled()) {
			return java.lang.Integer.MIN_VALUE;
		}
		if (expression instanceof Number) {
			return ((Number) expression).getConstant().getValue();
		} else {
			m_encounteredExceptions = true;
			ExceptionHandler.getInstance().throwException(Exception.EXPRESSION_IS_NOT_A_CONSTANT, ExceptionStrength.WEAK, expression);
			return java.lang.Integer.MIN_VALUE;
		}
	}

	/**
	 * Validates  an expression in a type declaration.
	 *
	 * @param expression The expression to validate.
	 */
	public int validateExpressionIsAPositiveConstant(Expression expression) {
		if (!isEnabled()) {
			return java.lang.Integer.MAX_VALUE;
		}
		if (validateExpressionIsAConstant(expression) > 0) {
			return validateExpressionIsAConstant(expression);
		} else {
			m_encounteredExceptions = true;
			ExceptionHandler.getInstance().throwException(Exception.EXPRESSION_IS_NOT_A_POSITIVE_CONSTANT, ExceptionStrength.WEAK, expression);
			return java.lang.Integer.MAX_VALUE;
		}
	}

	/**
	 * Validates that an expression is numeric.
	 *
	 * @param expression The expression to validate.
	 */
	public void validateExpressionIsNumeric(Expression expression) {
		if (!isEnabled()) {
			return;
		}
		if (!expression.isANumber()) {
			m_encounteredExceptions = true;
			ExceptionHandler.getInstance().throwException(Exception.EXPRESSION_IS_NOT_NUMERIC, ExceptionStrength.WEAK, expression);
		}
	}

	/**
	 * Validates that a declaration is not a type (i.e. either a constant or variable).
	 *
	 * @param declaration The declaration to validate.
	 */
	public boolean validateDeclarationIsNotAType(Declaration declaration) {
		if (!isEnabled()) {
			return true;
		}
		if (declaration instanceof Type) {
			m_encounteredExceptions = true;
			ExceptionHandler.getInstance().throwException(Exception.DECLARATION_IS_A_TYPE, ExceptionStrength.WEAK, declaration);
			return false;
		}
		return true;
	}

	/**
	 * Validates that a declaration is a type (i.e. not a constant or variable).
	 *
	 * @param declaration The declaration to validate.
	 */
	public Type validateDeclarationIsAType(Declaration declaration) {
		if (!isEnabled()) {
			return new InvalidType();
		}
		if (!(declaration instanceof Type)) {
			m_encounteredExceptions = true;
			ExceptionHandler.getInstance().throwException(Exception.DECLARATION_IS_NOT_A_TYPE, ExceptionStrength.WEAK, declaration);
			return new InvalidType();
		}
		return (Type) declaration;
	}

	/**
	 * Validates that a declaration is a variable.
	 *
	 * @param declaration The declaration to validate.
	 */
	public boolean validateDeclarationIsAVariable(Declaration declaration) {
		if (!isEnabled()) {
			return true;
		}
		if (!(declaration instanceof Variable)) {
			m_encounteredExceptions = true;
			ExceptionHandler.getInstance().throwException(Exception.DECLARATION_IS_NOT_A_VARIABLE, ExceptionStrength.WEAK, declaration);
			return false;
		}
		return true;
	}

	/**
	 * Validates that a declaration is a variable of type integer.
	 *
	 * @param declaration The declaration to validate.
	 */
	public boolean validateDeclarationIsAVariableWhoseTypeIsInteger(Declaration declaration) {
		if (!isEnabled()) {
			return true;
		}
		if (validateDeclarationIsAVariable(declaration) && ((Variable) declaration).getType() instanceof parser.semanticAnalysis.symbolTable.declarations.types.Integer) {
			return true;
		}
		m_encounteredExceptions = true;
		ExceptionHandler.getInstance().throwException(Exception.DECLARATION_IS_NOT_A_VARIABLE_OF_TYPE_INTEGER, ExceptionStrength.WEAK, declaration);
		return false;
	}

	/**
	 * Validates that an expression is a variable of type integer.
	 *
	 * @param expression The expression to validate.
	 */
	public Location validateDeclarationIsAVariableWhoseTypeIsInteger(Expression expression) {
		if (!isEnabled()) {
			return new InvalidLocation();
		}
		if (expression instanceof parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Variable) {
			parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Variable variable = (parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Variable) expression;
			if (variable.isNumeric()) {
				return variable;
			}
		} else if (expression instanceof Index) {
			Index index = (Index) expression;
			if (index.isNumeric()) {
				return index;
			}
		} else if (expression instanceof Field) {
			Field field = (Field) expression;
			if (field.isNumeric()) {
				return field;
			}
		}
		m_encounteredExceptions = true;
		ExceptionHandler.getInstance().throwException(Exception.EXPRESSION_IS_NOT_A_VARIABLE_OF_TYPE_INTEGER, ExceptionStrength.WEAK, expression);
		return new InvalidLocation();
	}

	/**
	 * Validates that two expressions are of the same type.
	 *
	 * @param expressionOne The first expression.
	 * @param expressionTwo The second expression.
	 */
	public Type validateExpressionsAreOfTheSameType(Expression expressionOne, Expression expressionTwo) {
		if (!isEnabled()) {
			return new InvalidType();
		}
		if (expressionOne.getType() != expressionTwo.getType()) {
			try {
				ExceptionHandler.getInstance().throwException("The two expressions, \"" + expressionOne.toString() + "\" and \"" + expressionTwo.toString() + "\" are not of the same type.", ExceptionStrength.WEAK);
			} catch (java.lang.Exception e) {
				ExceptionHandler.getInstance().throwException("Two expressions are not of the same type.", ExceptionStrength.WEAK);
			}
			m_encounteredExceptions = true;
		}
		return expressionOne.getType();
	}

	/**
	 * Validates the identifier in a designator.
	 *
	 * @param declaration    The declaration to validate.
	 * @param designatorType The type of the designator.
	 */
	public void validateDesignatorIdentifier(Declaration declaration, DesignatorType designatorType) {
		if (!isEnabled()) {
			return;
		}
		if (designatorType == DesignatorType.ASSIGN || designatorType == DesignatorType.READ) {
			validateDeclarationIsAVariable(declaration);
		} else {
			validateDeclarationIsNotAType(declaration);
		}
	}

	/**
	 * Validates that a constant does not divide or mod by zero.
	 *
	 * @param expression The expression to validate.
	 */
	public void validateDivisionAndModByZero(Binary expression) {
		if (!isEnabled()) {
			return;
		}
		String string = expression.toString();
		if (string.indexOf("/0") != -1) {
			m_encounteredExceptions = true;
			ExceptionHandler.getInstance().throwException(exception.Exception.CONSTANT_DIVIDE_BY_ZERO, ExceptionStrength.WEAK, expression.toString());
		} else if (string.indexOf("%0") != -1) {
			m_encounteredExceptions = true;
			ExceptionHandler.getInstance().throwException(exception.Exception.CONSTANT_MOD_BY_ZERO, ExceptionStrength.WEAK, expression.toString());
		}
	}

	/**
	 * Validates that an identifier is a procedure.
	 *
	 * @param identifier The identifier to validate.
	 */
	public Procedure validateIsProcedure(String identifier, boolean includeReturn) {
		if (!isEnabled()) {
			return new Procedure();
		}
		Declaration declaration = ScopeManager.getInstance().getProgramScope().find(identifier);
		if (!(declaration instanceof Procedure)) {
			if (declaration instanceof InvalidDeclaration) {
				ProcedureBuilder.getInstance().startBuilding();
				ProcedureBuilder.getInstance().setName(TokenIterator.getInstance().getCurrent().getValue().toString());
				if (includeReturn) {
					ProcedureBuilder.getInstance().setReturnType(Integer.getInstance());
				}
				Procedure procedure = ProcedureBuilder.getInstance().getDeclaration();
				ProcedureBuilder.getInstance().getPromises().add(procedure);
				ProcedureBuilder.getInstance().reset();
				ScopeManager.getInstance().getProgramScope().insert(procedure.getName(), procedure);
				return procedure;
			}
			m_encounteredExceptions = true;
			return ExceptionHandler.getInstance().throwException(Exception.NOT_A_PROCEDURE, ExceptionStrength.STRONG, declaration.getName(), declaration.getPosition());
		} else {
			return (Procedure) declaration;
		}
	}

	/**
	 * Validates that an identifier is an integer.
	 *
	 * @param identifier The identifier to validate.
	 */
	public Boolean validateIsOfTypeInteger(String identifier) {
		if (!isEnabled()) {
			return true;
		}
		Declaration declaration = ScopeManager.getInstance().getCurrentScope().find(identifier);
		if (declaration == null || !declaration.isIntegral()) {
			m_encounteredExceptions = true;
			if (declaration != null) {
				return ExceptionHandler.getInstance().throwException(Exception.IDENTIFIER_NOT_AN_INTEGER, ExceptionStrength.STRONG, identifier, declaration.getPosition());
			} else {
				return ExceptionHandler.getInstance().throwException("The identifier, " + identifier + ", is not an integer.", ExceptionStrength.STRONG);
			}
		}
		return true;
	}

	/**
	 * Validates that an expression is of type integer.
	 *
	 * @param expression The expression to validate.
	 */
	public void validateExpressionIsOfTypeInteger(Expression expression) {
		if (!isEnabled()) {
			return;
		} else if (expression == null || !expression.isANumber()) {
			m_encounteredExceptions = true;
			ExceptionHandler.getInstance().throwException(Exception.EXPRESSION_NOT_AN_INTEGER, ExceptionStrength.STRONG, expression.toString(), expression.getType().getPosition());
		}
	}

	/**
	 * Validates that a procedure has a return expression.
	 *
	 * @param procedure The procedure to validate.
	 */
	public void validateProcedureHasReturnExpression(Procedure procedure) {
		if (!isEnabled() || ProcedureBuilder.getInstance().peekDeclaration() == procedure || ProcedureBuilder.getInstance().containsPromise(procedure.getName())) {
			return;
		}
		if (procedure.getReturnExpression() == null) {
			m_encounteredExceptions = true;
			ExceptionHandler.getInstance().throwException(Exception.PROCEDURE_DOES_NOT_HAVE_RETURN, ExceptionStrength.STRONG, procedure.getName(), procedure.getPosition());
		}
	}

	/**
	 * Validates that a procedure does not have a return expression.
	 *
	 * @param procedure The procedure to validate.
	 */
	public void validateProcedureDoesNotHaveReturnExpression(Procedure procedure) {
		if (!isEnabled() || ProcedureBuilder.getInstance().peekDeclaration() == procedure || ProcedureBuilder.getInstance().containsPromise(procedure.getName())) {
			return;
		}
		if (procedure.getReturnExpression() != null) {
			m_encounteredExceptions = true;
			ExceptionHandler.getInstance().throwException(Exception.PROCEDURE_DOES_HAVE_RETURN, ExceptionStrength.STRONG, procedure.getName(), procedure.getPosition());
		}
	}

	/**
	 * Validates the correctness of a procedure and its arguments.
	 *
	 * @param procedure The procedure to validate.
	 * @param arguments The arguments to validate.
	 */
	public void validateProcedureArguments(Procedure procedure, List<Argument> arguments) {
		if (!isEnabled()) {
			return;
		}

		if (ProcedureBuilder.getInstance().getPromises().contains(procedure)) {
			ProcedureBuilder.getInstance().getCallPromises().add(new Tuple<Procedure, List<Argument>>(procedure, arguments));
			return;
		}

		String argumentsString = "void";
		if (arguments.size() > 0) {
			argumentsString = "(" + arguments.get(0).getExpression().toString();
			for (int i = 1; i < arguments.size(); i++) {
				argumentsString = argumentsString + ", " + arguments.get(i).getExpression().toString();
			}
			argumentsString = argumentsString + ")";
		}
		if (arguments == null || (arguments.size() != procedure.getParameters().size())) {
			m_encounteredExceptions = true;
			ExceptionHandler.getInstance().throwException(Exception.ARGUMENTS_DO_NOT_MATCH_PROCEDURE, ExceptionStrength.STRONG, argumentsString, procedure.getName(), procedure.getPosition());
		} else {
			for (int i = 0; i < arguments.size(); i++) {
				if (arguments.get(i).getExpression().getType() != procedure.getParameters().get(i).getType()) {
					try {
						ExceptionHandler.getInstance().throwException("The two types, \"" + arguments.get(i).getExpression().getType().toString() + "\" and \"" + procedure.getParameters().get(i).getType().toString() + "\" are not of the same type.", ExceptionStrength.WEAK);
					} catch (java.lang.Exception e) {
						ExceptionHandler.getInstance().throwException("Two expressions are not of the same type.", ExceptionStrength.WEAK);
					}
					m_encounteredExceptions = true;
				}
			}
		}
	}

	public void validateProcedures() {
		if (!m_isEnabled) {
			return;
		}
		if (!ProcedureBuilder.getInstance().getPromises().isEmpty()) {
			ExceptionHandler.getInstance().throwException("Two procedure, \"" + ProcedureBuilder.getInstance().getPromises().get(0).getName() + "\" was never defined.",  ExceptionStrength.STRONG);
		}
	}
}
