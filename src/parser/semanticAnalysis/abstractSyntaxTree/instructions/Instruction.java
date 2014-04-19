package parser.semanticAnalysis.abstractSyntaxTree.instructions;

import parser.semanticAnalysis.abstractSyntaxTree.Node;

public abstract class Instruction extends Node {

	private Instruction m_nextInstruction;

	public Instruction getNextInstruction() {
		return m_nextInstruction;
	}

	public void setNextInstruction(Instruction nextInstruction) {
		m_nextInstruction = nextInstruction;
	}

	public abstract int getNumberOfInstructions();
}
