package parser.semanticAnalysis.abstractSyntaxTree.expressions.locations;

import parser.semanticAnalysis.abstractSyntaxTree.expressions.Expression;
import parser.semanticAnalysis.symbolTable.declarations.types.Type;

/**
 * Represents an element of an array variable.
 */
public class Index extends Location {

	private Location m_variable;

	private Expression m_expression;

	public Index(Location variable, Expression expression, Type type) {
		super(type);
		m_variable = variable;
		m_expression = expression;
	}

	public Location getVariable() {
		return m_variable;
	}

	public Expression getExpression() {
		return m_expression;
	}

	public String toString() {
		return m_variable + "[" + m_expression.toString() + "]";
	}

	/**
	 * Clones the expression.
	 */
	@SuppressWarnings("CloneDoesntCallSuperClone")
	@Override
	public Expression clone() {
		return new Index((Location) m_variable.clone(), m_expression.clone(), getType());
	}

	/**
	 * Gets the position of this location.
	 */
	public String getPosition() {
		return "(" + m_variable.getType().getTokens().get(0) + ", " + m_expression.getType().getTokens().get(m_expression.getType().getTokens().size() - 1) + ")";
	}
}
