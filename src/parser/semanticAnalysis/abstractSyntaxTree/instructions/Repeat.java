package parser.semanticAnalysis.abstractSyntaxTree.instructions;

import parser.semanticAnalysis.abstractSyntaxTree.conditions.Condition;

import java.util.List;

public class Repeat extends Instruction {

	Condition m_condition;
	List<Instruction> m_instructions;

	public Repeat(Condition condition, List<Instruction> instructions) {
		m_condition = condition;
		m_instructions = instructions;
	}

	public Condition getCondition() {
		return m_condition;
	}

	public List<Instruction> getInstructions() {
		return m_instructions;
	}

	public String toString() {
		return "REPEAT...UNTIL " + m_condition.getLeftExpression().toString() + " " + m_condition.getRelation().toString() + " " + m_condition.getRightExpression();
	}

	@Override
	public int getNumberOfInstructions() {
		int total = 0;
		for (Instruction instruction : m_instructions) {
			total += instruction.getNumberOfInstructions();
		}
		return total;
	}
}
