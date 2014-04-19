package parser.utilities;

import parser.Parser;
import parser.semanticAnalysis.SemanticValidator;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.*;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.Number;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.utilities.ConstantFolder;
import parser.semanticAnalysis.symbolTable.declarations.constants.constant.Constant;
import parser.syntacticAnalysis.ProductionRule;
import parser.syntacticAnalysis.SyntacticValidator;

import java.lang.reflect.Method;

/**
 * Parses expressions.
 */
public class ExpressionParser {

	/**
	 * Represents the instance of expression parser.
	 */
	private static ExpressionParser m_instance;

	/**
	 * Constructs the expression parser.
	 */
	private ExpressionParser() {}

	/**
	 * Gets the instance of the expression parser.
	 */
	public static ExpressionParser getInstance() {
		if (m_instance == null) {
			m_instance = new ExpressionParser();
		}
		return m_instance;
	}

	/**
	 * Parses an expression.
	 *
	 * @param methodName  The method to invoke down the chain on expression parsing.
	 * @param allowPrefix Determines if a prefix of the symbols is allowed.
	 * @param symbols     The reserved symbols for which to parse the expression.
	 */
	public Expression parseExpression(String methodName, boolean allowPrefix, Object... symbols) {
		try {
			Method method = Parser.getInstance().getClass().getDeclaredMethod(methodName);
			method.setAccessible(true);
			Binary binaryExpression = null;
			Expression leftExpression;

			if (allowPrefix && SyntacticValidator.getInstance().isNextToken(ProductionRule.OPTIONAL, symbols)) {
				SyntacticValidator.getInstance().matchNextToken(ProductionRule.OPTIONAL, symbols);
				Operator operator = Operator.getOperator(TokenIterator.getInstance().getCurrent().getValue().toString());
				Expression expression = (Expression) method.invoke(Parser.getInstance());
				SemanticValidator.getInstance().validateExpressionIsNumeric(expression);
				leftExpression = binaryExpression = new Binary(operator, new Number(new Constant(0)), expression, parser.semanticAnalysis.symbolTable.declarations.types.Integer.getInstance());
				if (!SyntacticValidator.getInstance().isNextToken(ProductionRule.OPTIONAL, symbols)) {
					SemanticValidator.getInstance().validateExpressionIsNumeric(binaryExpression);
					return ConstantFolder.getInstance().reduceExpression(binaryExpression);
				}
			} else {
				leftExpression = (Expression) method.invoke(Parser.getInstance());
				if (SyntacticValidator.getInstance().isNextToken(ProductionRule.OPTIONAL, symbols)) {
					SemanticValidator.getInstance().validateExpressionIsNumeric(leftExpression);
				} else {
					return ConstantFolder.getInstance().reduceExpression(leftExpression);
				}
			}
			do {
				SyntacticValidator.getInstance().matchNextToken(ProductionRule.REQUIRED, symbols);
				Operator operator = Operator.getOperator(TokenIterator.getInstance().getCurrent().getValue().toString());
				if (binaryExpression == null) {
					Expression rightExpression = (Expression) method.invoke(Parser.getInstance());
					SemanticValidator.getInstance().validateExpressionIsNumeric(leftExpression);
					SemanticValidator.getInstance().validateExpressionIsNumeric(rightExpression);
					binaryExpression = new Binary(operator, leftExpression, rightExpression, SemanticValidator.getInstance().validateExpressionsAreOfTheSameType(leftExpression, rightExpression));
					leftExpression = rightExpression;
				} else {
					Expression left = ConstantFolder.getInstance().reduceExpression(binaryExpression);
					Expression right = (Expression) method.invoke(Parser.getInstance());
					SemanticValidator.getInstance().validateExpressionIsNumeric(left);
					SemanticValidator.getInstance().validateExpressionIsNumeric(right);
					binaryExpression = new Binary(operator, left, right, SemanticValidator.getInstance().validateExpressionsAreOfTheSameType(left, right));
				}
			} while (SyntacticValidator.getInstance().isNextToken(ProductionRule.OPTIONAL, symbols));
			return ConstantFolder.getInstance().reduceExpression(binaryExpression);
		} catch (Exception e) {
			return new InvalidExpression();
		}
	}
}
