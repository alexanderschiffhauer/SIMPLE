package parser.semanticAnalysis.symbolTable.declarations.procedures;

import parser.semanticAnalysis.abstractSyntaxTree.expressions.Expression;

/**
 * Represents the encapsulation of an argument passed to a procedure.
 */
public class Argument {

	/**
	 * Represents the expression of the argument.
	 */
	private Expression m_expression;

	/**
	 * Constructs a new argument.
	 *
	 * @param expression The expression of the argument.
	 */
	public Argument(Expression expression) {
		m_expression = expression;
	}

	/**
	 * Gets the expression of the argument.
	 */
	public Expression getExpression() {
		return m_expression;
	}
}
