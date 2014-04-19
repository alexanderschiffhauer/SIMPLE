package parser.semanticAnalysis.abstractSyntaxTree.instructions;

import parser.semanticAnalysis.abstractSyntaxTree.conditions.Condition;

import java.util.ArrayList;
import java.util.List;

public class If extends Instruction {

	Condition m_condition;

	List<Instruction> m_trueInstructions;

	List<Instruction> m_falseInstructions;

	public If(Condition condition, List<Instruction> trueInstructions) {
		this(condition, trueInstructions, new ArrayList<Instruction>());
	}

	public If(Condition condition, List<Instruction> trueInstructions, List<Instruction> falseInstructions) {
		m_condition = condition;
		m_trueInstructions = trueInstructions;
		m_falseInstructions = falseInstructions;
	}

	public Condition getCondition() {
		return m_condition;
	}

	public List<Instruction> getTrueInstructions() {
		return m_trueInstructions;
	}

	public List<Instruction> getFalseInstructions() {
		return m_falseInstructions;
	}

	public boolean falseInstructionsExist() {
		return !m_falseInstructions.isEmpty();
	}

	public String toString() {
		return "IF " + m_condition.getLeftExpression().toString() + " " + m_condition.getRelation().toString() + " " + m_condition.getRightExpression().toString();
	}

	@Override
	public int getNumberOfInstructions() {
		int total = 0;
		for (Instruction instruction : m_trueInstructions) {
			total += instruction.getNumberOfInstructions();
		}
		for (Instruction instruction : m_falseInstructions) {
			total += instruction.getNumberOfInstructions();
		}
		return total;
	}

	public int getNumberOfFalseInstructions() {
		int total = 0;
		for (Instruction instruction : m_falseInstructions) {
			total += instruction.getNumberOfInstructions();
		}
		return total;
	}
}
