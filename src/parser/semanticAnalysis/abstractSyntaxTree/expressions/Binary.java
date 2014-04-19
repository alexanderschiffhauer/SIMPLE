package parser.semanticAnalysis.abstractSyntaxTree.expressions;

import parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Field;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Index;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Variable;
import parser.semanticAnalysis.symbolTable.declarations.types.Integer;
import parser.semanticAnalysis.symbolTable.declarations.types.Type;

public class Binary extends Expression {

	Operator m_operator;

	Expression m_leftExpression;

	Expression m_rightExpression;

	public Binary(Operator operator, Expression leftExpression, Expression rightExpression, Type type) {
		super(type);
		m_operator = operator;
		m_leftExpression = leftExpression;
		m_rightExpression = rightExpression;
	}

	public Operator getOperator() {
		return m_operator;
	}

	public Expression getLeftExpression() {
		return m_leftExpression;
	}

	public Expression getRightExpression() {
		return m_rightExpression;
	}

	public String toString() {
		String operator = "";
		switch (m_operator) {
			case PLUS:
				operator = "+";
				break;
			case MINUS:
				operator = "-";
				break;
			case MULTIPLICATION:
				operator = "*";
				break;
			case DIV:
				operator = "/";
				break;
			case MOD:
				operator = "%";
				break;
		}
		return m_leftExpression.toString() + operator + m_rightExpression.toString();
	}

	public boolean canBeFolded() {
		return isConstant(m_leftExpression) && isConstant(m_rightExpression);
	}

	private boolean isConstant(Expression expression) {
		if (expression instanceof Number) {
			return true;
		} else if (expression instanceof Binary) {
			return isConstant(((Binary) expression).getLeftExpression()) && isConstant(((Binary) expression).getRightExpression());
		} else if (expression instanceof Variable) {
			return ((Variable) expression).getVariable().getType() instanceof Integer;
		} else if (expression instanceof Index) {
			return false;
		} else if (expression instanceof Field) {
			return false;
		} else {
			return false;
		}
	}

	/**
	 * Clones the expression.
	 */
	@SuppressWarnings("CloneDoesntCallSuperClone")
	@Override
	public Expression clone() {
		return new Binary(m_operator, m_leftExpression.clone(), m_rightExpression.clone(), getType());
	}
}
