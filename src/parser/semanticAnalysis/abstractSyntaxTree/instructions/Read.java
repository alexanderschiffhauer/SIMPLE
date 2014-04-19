package parser.semanticAnalysis.abstractSyntaxTree.instructions;

import parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Location;

public class Read extends Instruction {

	Location m_location;

	public Location getLocation() {
		return m_location;
	}

	public Read(Location location) {
		m_location = location;
	}

	public String toString() {
		return "READ " + m_location.toString();
	}

	@Override
	public int getNumberOfInstructions() {
		return 1;
	}
}
