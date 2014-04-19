package parser.semanticAnalysis.abstractSyntaxTree.instructions;

import parser.semanticAnalysis.abstractSyntaxTree.expressions.Expression;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Location;

public class Assign extends Instruction {

	private Location m_location;

	private Expression m_expression;

	public Assign(Location location, Expression expression) {
		m_location = location;
		m_expression = expression;
	}

	public Location getLocation() {
		return m_location;
	}

	public Expression getExpression() {
		return m_expression;
	}

	public String toString() {
		return m_location.toString() + " := " + m_expression;
	}

	@Override
	public int getNumberOfInstructions() {
		return 1;
	}
}
