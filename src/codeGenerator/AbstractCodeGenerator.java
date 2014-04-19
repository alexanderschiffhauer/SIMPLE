package codeGenerator;

import codeGenerator.utilities.Register;
import parser.semanticAnalysis.abstractSyntaxTree.conditions.Condition;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Location;
import parser.semanticAnalysis.abstractSyntaxTree.instructions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * The base class for code generators that handles basic functionality.
 */
public abstract class AbstractCodeGenerator extends AbstractAssemblyCodePrinter {

	/**
	 * Represents a counter for instructions.
	 */
	protected int m_instructionCounter;
	/**
	 * Represents the current instruction.
	 */
	private String m_currentInstruction;

	/**
	 * Represents a list of array-indexing expression exceptions.
	 */
	protected static List<String> s_arrayExceptions = new ArrayList<String>();

	/**
	 * Generates code for a list of instructions.
	 *
	 * @param instructions The instructions for which to generate code.
	 */
	public void generate(List<Instruction> instructions) {
		printHeader();
		printDeclarations();
		printOpening();
		generateInstructions(instructions);
		printClosing();
	}

	/**
	 * Generates a list of instructions.
	 *
	 * @param instructions The instructions to interpret.
	 */
	protected void generateInstructions(List<Instruction> instructions) {
		for (Instruction instruction : instructions) {
			m_currentInstruction = instruction.toString();
			print(LABEL_PREFIX + m_instructionCounter++ + ": # " + m_currentInstruction);
			indent();
			if (instruction instanceof Assign) {
				generateAssign((Assign) instruction);
			} else if (instruction instanceof If) {
				generateIf((If) instruction);
			} else if (instruction instanceof Repeat) {
				generateRepeat((Repeat) instruction);
			} else if (instruction instanceof Read) {
				generateRead((Read) instruction);
			} else if (instruction instanceof Write) {
				generateWrite((Write) instruction);
			} else if (instruction instanceof Call) {
				generateCall((Call) instruction);
			}
			removeIndent();
		}
	}

	/**
	 * Generates a write instruction.
	 *
	 * @param write The write instruction to generate.
	 */
	protected abstract void generateWrite(Write write);

	/**
	 * Generates a read instruction.
	 *
	 * @param read The read instruction to generate.
	 */
	protected abstract void generateRead(Read read);

	/**
	 * Generates a repeat instruction.
	 *
	 * @param repeat The repeat instruction to generate.
	 */
	protected abstract void generateRepeat(Repeat repeat);

	/**
	 * Generates an if instruction.
	 *
	 * @param ifInstruction The if instruction to generate.
	 */
	protected abstract void generateIf(If ifInstruction);

	/**
	 * Generates an assign instruction.
	 *
	 * @param assign The assign instruction to generate.
	 */
	protected abstract void generateAssign(Assign assign);

	/**
	 * Generates a call instruction.
	 *
	 * @param call The instruction to generate.
	 */
	protected abstract void generateCall(Call call);

	/**
	 * Jumps to a label on a condition.
	 *
	 * @param condition        The condition on which to jump.
	 * @param instructionLabel The label to which to jump.
	 */
	protected void jumpOnCondition(Condition condition, int instructionLabel) {
		codeGenerator.utilities.Instruction instruction = null;
		switch (condition.getRelation()) {
			case EQUALITY:
				instruction = codeGenerator.utilities.Instruction.JE;
				break;
			case INEQUALITY:
				instruction = codeGenerator.utilities.Instruction.JNE;
				break;
			case LESS_THAN:
				instruction = codeGenerator.utilities.Instruction.JL;
				break;
			case GREATER_THAN:
				instruction = codeGenerator.utilities.Instruction.JG;
				break;
			case LESS_THAN_OR_EQUAL_TO:
				instruction = codeGenerator.utilities.Instruction.JLE;
				break;
			case GREATER_THAN_OR_EQUAL_TO:
				instruction = codeGenerator.utilities.Instruction.JGE;
				break;
		}
		print(instruction, LABEL_PREFIX + instructionLabel);
	}

	/**
	 * Generates array bounds checking for a location.
	 *
	 * @param location The location containing the potential out-of-bounds expression.
	 * @param register The register that contains the value of the index.
	 */
	public abstract void generateArrayBoundsChecking(Location location, Register register);

	/**
	 * Gets a list of array-indexing expression exceptions.
	 */
	public static List<String> getArrayExceptions() {
		return s_arrayExceptions;
	}


	/**
	 * Prints the header of the assembly code.
	 */
	private void printHeader() {
		print(".intel_syntax noprefix");
		printNewLine();
	}

	/**
	 * Prints the declarations.
	 */
	protected abstract void printDeclarations();

	/**
	 * Prints a declaration.
	 *
	 * @param declarationName The name of the declaration.
	 */
	protected abstract void printDeclaration(String declarationName);

	/**
	 * Prints the opening of main routine.
	 */
	private void printOpening() {
		print(".section .text");
		indent();
		print(".global main");
		print("main:");
		indent();
		printMainInitialization();
	}


	/**
	 * Initializes main.
	 */
	protected abstract void printMainInitialization();


	/**
	 * Prints the closing of the main routine.
	 */
	private void printClosing() {
		print(codeGenerator.utilities.Instruction.MOVQ, Register.RDI, 0);
		print(codeGenerator.utilities.Instruction.CALL, "exit");
		removeIndent();
		printRead();
		printWrite();
		printIndexOutOfBounds();
		removeIndent();
		close();
	}

	/**
	 * Prints the read subroutine.
	 */
	protected abstract void printRead();

	/**
	 * Prints the write subroutine.
	 */
	protected abstract void printWrite();

	/**
	 * Prints the error messages for array indexing.
	 */
	protected abstract void printIndexOutOfBounds();
}
