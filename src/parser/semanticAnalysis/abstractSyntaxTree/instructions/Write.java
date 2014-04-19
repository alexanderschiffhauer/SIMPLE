package parser.semanticAnalysis.abstractSyntaxTree.instructions;

import parser.semanticAnalysis.abstractSyntaxTree.expressions.Expression;

public class Write extends Instruction {

	Expression m_expression;

	public Write(Expression expression) {

		m_expression = expression;
	}

	public Expression getExpression() {
		return m_expression;
	}

	public String toString() {
		return "WRITE " + m_expression.toString();
	}

	@Override
	public int getNumberOfInstructions() {
		return 1;
	}
}
