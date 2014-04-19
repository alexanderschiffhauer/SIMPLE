package parser.semanticAnalysis.abstractSyntaxTree.instructions;

import parser.semanticAnalysis.symbolTable.declarations.procedures.Argument;
import parser.semanticAnalysis.symbolTable.declarations.procedures.Procedure;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an invocation of a procedure.
 */
public class Call extends Instruction {

	/**
	 * Represents the procedure of the call.
	 */
	private Procedure m_procedure;
	/**
	 * Represents the list of arguments used to invoke the procedure.
	 */
	private List<Argument> m_arguments = new ArrayList<Argument>();

	/**
	 * Constructs a new call.
	 *
	 * @param arguments THe arguments with which to invoke the procedure.
	 * @param procedure The procedure that will be called.
	 */
	public Call(List<Argument> arguments, Procedure procedure) {
		m_arguments = arguments;
		m_procedure = procedure;
	}

	/**
	 * Gets the arguments of the procedure.
	 */
	public List<Argument> getArguments() {
		return m_arguments;
	}

	/**
	 * Gets the procedure of the call.
	 */
	public Procedure getProcedure() {
		return m_procedure;
	}

	@Override
	public int getNumberOfInstructions() {
		int numberOfInstructions = 0;
		for (Instruction instruction : m_procedure.getInstructions()) {
			numberOfInstructions += instruction.getNumberOfInstructions();
		}
		return numberOfInstructions;
	}
}
