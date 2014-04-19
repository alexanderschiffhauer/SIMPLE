package parser.semanticAnalysis.abstractSyntaxTree.conditions;

import parser.semanticAnalysis.abstractSyntaxTree.Node;
import parser.semanticAnalysis.abstractSyntaxTree.conditions.relation.IRelation;
import parser.semanticAnalysis.abstractSyntaxTree.conditions.relation.Relation;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.Expression;

public class Condition extends Node {

	private Expression m_leftExpression;
	private Expression m_rightExpression;
	private Relation m_relation;

	public Condition(Expression leftExpression, Expression rightExpression, Relation relation) {
		m_leftExpression = leftExpression;
		m_rightExpression = rightExpression;
		m_relation = relation;
	}

	public Condition(Condition condition) {
		m_leftExpression = condition.getLeftExpression();
		m_rightExpression = condition.getRightExpression();
		m_relation = condition.getRelation();
	}

	public Expression getLeftExpression() {
		return m_leftExpression;
	}

	public Expression getRightExpression() {
		return m_rightExpression;
	}

	public Relation getRelation() {
		return m_relation;
	}

	@SuppressWarnings("all")
	// Because of a bug in JDK 6, we need to explicitly cast enums to their interfaces to invoke any promises.  We suppress any warnings pertinent to this issue.
	public void negateExpression() {
		m_relation = ((IRelation) m_relation).getNegation();
	}

	/**
	 * Clones the condition.
	 */
	public Condition clone() {
		return new Condition(m_leftExpression.clone(), m_rightExpression.clone(), m_relation);
	}
}
