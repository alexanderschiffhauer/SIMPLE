package codeGenerator.codeGenerator;

import codeGenerator.AbstractCodeGenerator;
import codeGenerator.utilities.DeclarationMangler;
import codeGenerator.utilities.Instruction;
import codeGenerator.utilities.Register;
import parser.semanticAnalysis.abstractSyntaxTree.conditions.Condition;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Field;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Index;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Location;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Variable;
import parser.semanticAnalysis.abstractSyntaxTree.instructions.*;
import parser.semanticAnalysis.symbolTable.declarations.Declaration;
import parser.semanticAnalysis.symbolTable.declarations.types.Integer;
import parser.semanticAnalysis.symbolTable.declarations.types.array.Array;
import parser.semanticAnalysis.symbolTable.declarations.types.record.Record;
import parser.semanticAnalysis.symbolTable.scope.ScopeManager;

/**
 * Generates AMD64 assembly code given a list of SIMPLE instructions.
 */
public class CodeGenerator extends AbstractCodeGenerator {

	/**
	 * Represents the instance of the code generator.
	 */
	private static CodeGenerator m_instance;

	/**
	 * Gets the instance of the code generator.
	 */
	public static CodeGenerator getInstance() {
		if (m_instance == null) {
			m_instance = new CodeGenerator();
		}
		return m_instance;
	}

	/**
	 * Constructs the code generator.
	 */
	private CodeGenerator() {}

	/**
	 * Generates an assign instruction.
	 *
	 * @param assign The assign instruction to generate.
	 */
	@Override
	protected void generateAssign(Assign assign) {
		print(codeGenerator.utilities.Instruction.MOVQ, Register.R8, ExpressionParser.getInstance().getLocationAddress(assign.getLocation()));

		if (assign.getLocation().getType() instanceof Integer) {
			print(Instruction.MOVQ, Register.R9, ExpressionParser.getInstance().getExpressionValue(assign.getExpression()));
			print(Instruction.MOV, "qword ptr [" + Register.R8 + "]", Register.R9);
		} else {
			print(Instruction.MOVQ, Register.R9, ExpressionParser.getInstance().getLocationAddress((Location) assign.getExpression()));
			int size;
			try {
				Variable variable = (Variable) assign.getLocation();
				size = variable.getType().getSize();
			} catch (Exception ignored) {
				try {
					Field field = (Field) assign.getLocation();
					size = field.getType().getSize();
				} catch (Exception ignored1) {
					Index index = (Index) assign.getLocation();
					Array array = (Array) index.getType();
					size = array.getSize();
				}
			}
			for (int i = 0; i < size; i = i + 8) {
				int j = i;
				if (j != 0) {
					j = 8;
				}
				print(Instruction.ADDQ, Register.R8, j);
				print(Instruction.ADDQ, Register.R9, j);

				print(Instruction.MOV, Register.R10, "qword ptr [" + Register.R9 + "]");
				print(Instruction.MOV, "qword ptr [" + Register.R8 + "]", Register.R10);
			}
		}
	}

	/**
	 * Prints the declarations.
	 */
	@Override
	protected void printDeclarations() {
		print(".section .data");
		indent();
		print("_base : .quad 0");
		for (String declarationName : ScopeManager.getInstance().getProgramScope().getMap().keySet()) {
			printDeclaration(declarationName);
		}
		printTemporaryVariables();
		removeIndent();
		printNewLine();
	}

	/**
	 * Prints a declaration.
	 *
	 * @param declarationName The name of the declaration.
	 */
	@Override
	protected void printDeclaration(String declarationName) {
		Declaration declaration = ScopeManager.getInstance().getProgramScope().find(declarationName);
		if (declaration instanceof parser.semanticAnalysis.symbolTable.declarations.variable.Variable) {
			parser.semanticAnalysis.symbolTable.declarations.variable.Variable variable = (parser.semanticAnalysis.symbolTable.declarations.variable.Variable) declaration;
			variable.setOffset(m_memorySize);
			m_memorySize += variable.getType().getSize();
			if (variable.getType() instanceof Array) {
				for (String arrayElement : DeclarationMangler.getInstance().mangle((Array) variable.getType())) {
					print(declarationName + arrayElement + " : .quad 0");
				}
			} else if (variable.getType() instanceof Record) {
				for (String recordField : DeclarationMangler.getInstance().mangle((Record) variable.getType())) {
					print(declarationName + recordField + " : .quad 0");
				}
			} else if (variable.getType() instanceof Integer) {
				print(declarationName + " : .quad 0");
			}
		}
	}

	/**
	 * Initializes main.
	 */
	@Override
	protected void printMainInitialization() {
		print(codeGenerator.utilities.Instruction.LEA, Register.R8, "_base");
		print(Instruction.ADDQ, Register.R8, 8);
		print(codeGenerator.utilities.Instruction.MOVQ, "_base", Register.R8);
	}

	/**
	 * Generates an if instruction.
	 *
	 * @param ifInstruction The if instruction to generate.
	 */
	@Override
	protected void generateIf(If ifInstruction) {
		print(Instruction.MOVQ, Register.R8, ExpressionParser.getInstance().getExpressionValue(ifInstruction.getCondition().getLeftExpression()));
		print(Instruction.MOVQ, Register.R9, ExpressionParser.getInstance().getExpressionValue(ifInstruction.getCondition().getRightExpression()));
		print(Instruction.CMP, Register.R8, Register.R9);
		if (ifInstruction.getTrueInstructions().size() > 0) {
			jumpOnCondition(ifInstruction.getCondition(), m_instructionCounter);
			Condition condition = ifInstruction.getCondition().clone();
			condition.negateExpression();
			if (ifInstruction.getFalseInstructions().size() > 0) {
				int instructionCounter = m_instructionCounter + ifInstruction.getNumberOfFalseInstructions();
				jumpOnCondition(condition, instructionCounter);
			} else {
				jumpOnCondition(condition, m_instructionCounter);
			}
			generateInstructions(ifInstruction.getTrueInstructions());
		}
		if (ifInstruction.getFalseInstructions().size() > 0) {
			print(Instruction.MOVQ, Register.R8, ExpressionParser.getInstance().getExpressionValue(ifInstruction.getCondition().getLeftExpression()));
			print(Instruction.MOVQ, Register.R9, ExpressionParser.getInstance().getExpressionValue(ifInstruction.getCondition().getRightExpression()));
			print(Instruction.CMP, Register.R8, Register.R9);
			jumpOnCondition(ifInstruction.getCondition(), m_instructionCounter + 1);
			generateInstructions(ifInstruction.getFalseInstructions());
		}
	}

	/**
	 * Generates a repeat instruction.
	 *
	 * @param repeat The repeat instruction to generate.
	 */
	@Override
	protected void generateRepeat(Repeat repeat) {
		int currentInstruction = m_instructionCounter - 1;
		generateInstructions(repeat.getInstructions());
		print(Instruction.MOVQ, Register.R8, ExpressionParser.getInstance().getExpressionValue(repeat.getCondition().getLeftExpression()));
		print(Instruction.MOVQ, Register.R9, ExpressionParser.getInstance().getExpressionValue(repeat.getCondition().getRightExpression()));
		print(Instruction.CMP, Register.R8, Register.R9);
		jumpOnCondition(repeat.getCondition(), currentInstruction);
	}

	/**
	 * Generates a read instruction.
	 *
	 * @param read The read instruction to generate.
	 */
	@Override
	protected void generateRead(Read read) {
		print(Instruction.CALL, "read");
		print(Instruction.MOVQ, Register.R8, "_read");
		print(Instruction.MOVQ, ExpressionParser.getInstance().getLocationValue(read.getLocation()), "_read");
	}

	/**
	 * Generates a write instruction.
	 *
	 * @param write The write instruction to generate.
	 */
	@Override
	protected void generateWrite(Write write) {
		print(Instruction.MOVQ, Register.R8, ExpressionParser.getInstance().getExpressionValue(write.getExpression()));
		print(Instruction.MOVQ, "_write", Register.R8);
		print(Instruction.CALL, "write");
	}

	/**
	 * Generates a call instruction.
	 *
	 * @param call The instruction to generate.
	 */
	@Override
	protected void generateCall(Call call) {
	}

	/**
	 * Prints the temporary variables used by the code generator.
	 */
	private void printTemporaryVariables() {
		print("_read : .quad 0");
		print("_write : .quad 0");
		print("_temp : .quad 0");
		print("_temp_index : .quad 0");
		print("_temp_binary: .quad 0");
		print("_temp_expression_left: .quad 0");
		print("_temp_expression_right: .quad 0");
	}

	/**
	 * Prints the read subroutine.
	 */
	@Override
	protected void printRead() {
		print("read:");
		indent();
		print(Instruction.LEA, Register.RDI, "formatRead");
		print(Instruction.LEA, Register.RSI, "_read");
		print(Instruction.XOR, Register.RAX, Register.RAX);
		print(Instruction.CALL, "scanf");
		print(Instruction.RET);
		removeIndent();
		print("formatRead:");
		indent();
		print(".asciz \"%ld\"");
		removeIndent();
	}

	/**
	 * Prints the write subroutine.
	 */
	@Override
	protected void printWrite() {
		print("write:");
		indent();
		print(Instruction.LEA, Register.RDI, "formatWrite");
		print(Instruction.MOVQ, Register.RSI, "_write");
		print(Instruction.XOR, Register.RAX, Register.RAX);
		print(Instruction.CALL, "printf");
		print(Instruction.RET);
		removeIndent();
		print("formatWrite:");
		indent();
		print(".asciz \"%ld\\n\"");
		removeIndent();
	}

	/**
	 * Prints the error messages for array indexing.
	 */
	@Override
	protected void printIndexOutOfBounds() {
		for (int i = 0; i < AbstractCodeGenerator.getArrayExceptions().size(); i++) {
			String string = AbstractCodeGenerator.getArrayExceptions().get(i);
			print(AbstractCodeGenerator.ARRAY_EXCEPTION_PREFIX + i + ":");
			indent();

			print(Instruction.MOV, Register.RDI, "qword ptr stderr[" + Register.RIP + "]");
			print(Instruction.LEA, Register.RSI, AbstractCodeGenerator.ARRAY_EXCEPTION_MESSAGE_PREFIX + i);
			print(Instruction.XOR, Register.RAX, Register.RAX);
			print(Instruction.CALL, "fprintf");
			// Exit
			print(Instruction.MOVQ, Register.RDI, 0);
			print(Instruction.CALL, "exit");

			removeIndent();

			print(AbstractCodeGenerator.ARRAY_EXCEPTION_MESSAGE_PREFIX + i + ":");
			indent();
			print(".asciz \"" + string + "\\n\"");
			removeIndent();
		}
	}

	/**
	 * Generates array bounds checking for a location.
	 *
	 * @param location The location containing the potential out-of-bounds expression.
	 * @param register The register that contains the value of the index.
	 */
	@Override
	public void generateArrayBoundsChecking(Location location, Register register) {
		int upperBoundsExceptionIndex = s_arrayCounter++, lowerBoundsExceptionIndex = s_arrayCounter++;
		s_arrayExceptions.add("UpperOutOfBoundsException: " + location.toString() + " @" + location.getType().getPosition());
		s_arrayExceptions.add("NegativeOutOfBoundsException: " + location.toString() + " @" + location.getType().getPosition());


		print(codeGenerator.utilities.Instruction.CMP, register, ((Array) ((Index) location).getVariable().getType()).getLength() * 8);
		print(codeGenerator.utilities.Instruction.JGE, ARRAY_EXCEPTION_PREFIX + upperBoundsExceptionIndex);
		print(codeGenerator.utilities.Instruction.CMP, register, 0);
		print(codeGenerator.utilities.Instruction.JL, ARRAY_EXCEPTION_PREFIX + lowerBoundsExceptionIndex);
	}
}
