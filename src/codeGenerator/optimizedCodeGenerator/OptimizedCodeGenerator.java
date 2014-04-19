package codeGenerator.optimizedCodeGenerator;

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

import java.util.HashMap;
import java.util.Map;

/**
 * Generates optimized AMD64 assembly code given a list of SIMPLE instructions.
 */
public class OptimizedCodeGenerator extends AbstractCodeGenerator {

	/**
	 * Represents the instance of the optimized code generator.
	 */
	private static OptimizedCodeGenerator m_instance;

	/**
	 * Represents the map of mangled variable names to their addresses in memory.
	 */
	private Map<String, java.lang.Integer> m_variables;

	/**
	 * Represents the name of the variable that contains the contiguous allocated space for the variables.
	 */
	public static final String SPACE = "space";

	/**
	 * Represents the name of the variable that contains the offset of the program's memory.
	 */
	public static final String OFFSET = Register.R8.toString();

	/**
	 * Gets the instance of the optimized code generator.
	 */
	public static OptimizedCodeGenerator getInstance() {
		if (m_instance == null) {
			m_instance = new OptimizedCodeGenerator();
		}
		return m_instance;
	}

	/**
	 * Constructs the optimized code generator.
	 */
	private OptimizedCodeGenerator() {
		m_variables = new HashMap<String, java.lang.Integer>();
	}

	/**
	 * Generates an assign instruction.
	 *
	 * @param assign The assign instruction to generate.
	 */
	@Override
	protected void generateAssign(Assign assign) {
		String leftExpression = OptimizedExpressionParser.getInstance().getLocationAddress(assign.getLocation());
		String rightExpression;
		if (assign.getLocation().getType() instanceof Integer) {
			rightExpression = OptimizedExpressionParser.getInstance().getExpressionValue(assign.getExpression());
			print(codeGenerator.utilities.Instruction.MOV, "qword ptr [" + leftExpression + "]", rightExpression);
		} else {
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
			print(codeGenerator.utilities.Instruction.MOVQ, Register.RDI, leftExpression);
			rightExpression = OptimizedExpressionParser.getInstance().getLocationAddress((Location) assign.getExpression());
			print(codeGenerator.utilities.Instruction.MOVQ, Register.RSI, rightExpression);
			print(codeGenerator.utilities.Instruction.MOVQ, Register.RDX, size);
			print(codeGenerator.utilities.Instruction.CALL, "memmove");
		}
		RegisterPool.getInstance().freeRegister(leftExpression);
		RegisterPool.getInstance().freeRegister(rightExpression);
		RegisterPool.getInstance().reset();
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
	 * Generates an if instruction.
	 *
	 * @param ifInstruction The if instruction to generate.
	 */
	@Override
	protected void generateIf(If ifInstruction) {
		String leftExpression = OptimizedExpressionParser.getInstance().getExpressionValue(ifInstruction.getCondition().getLeftExpression());
		if (!Register.contains(leftExpression)) {
			String temp = RegisterPool.getInstance().requestNewRegister().toString();
			print(Instruction.MOVQ, temp, leftExpression);
			leftExpression = temp;
		}
		String rightExpression = OptimizedExpressionParser.getInstance().getExpressionValue(ifInstruction.getCondition().getRightExpression());
		if (!Register.contains(rightExpression)) {
			String temp = RegisterPool.getInstance().requestNewRegister().toString();
			print(Instruction.MOVQ, temp, rightExpression);
			rightExpression = temp;
		}
		print(codeGenerator.utilities.Instruction.CMP, leftExpression, rightExpression);
		RegisterPool.getInstance().freeRegister(leftExpression);
		RegisterPool.getInstance().freeRegister(rightExpression);
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
			jumpOnCondition(ifInstruction.getCondition(), m_instructionCounter + 1);
			generateInstructions(ifInstruction.getFalseInstructions());
		}
		RegisterPool.getInstance().reset();
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
		String leftExpression = OptimizedExpressionParser.getInstance().getExpressionValue(repeat.getCondition().getLeftExpression());
		if (!Register.contains(leftExpression)) {
			String temp = RegisterPool.getInstance().requestNewRegister().toString();
			print(Instruction.MOVQ, temp, leftExpression);
			leftExpression = temp;
		}
		String rightExpression = OptimizedExpressionParser.getInstance().getExpressionValue(repeat.getCondition().getRightExpression());
		if (!Register.contains(rightExpression)) {
			String temp = RegisterPool.getInstance().requestNewRegister().toString();
			print(Instruction.MOVQ, temp, rightExpression);
			rightExpression = temp;
		}
		print(codeGenerator.utilities.Instruction.CMP, leftExpression, rightExpression);
		RegisterPool.getInstance().freeRegister(leftExpression);
		RegisterPool.getInstance().freeRegister(rightExpression);
		jumpOnCondition(repeat.getCondition(), currentInstruction);
		RegisterPool.getInstance().reset();
	}

	/**
	 * Generates a read instruction.
	 *
	 * @param read The read instruction to generate.
	 */
	@Override
	protected void generateRead(Read read) {
		print(codeGenerator.utilities.Instruction.CALL, "read");
		String register = OptimizedExpressionParser.getInstance().getLocationAddress(read.getLocation());
		print(Instruction.MOVQ, Register.RSI, "qword ptr [" + register + "]");
		RegisterPool.getInstance().freeRegister(register);
		RegisterPool.getInstance().reset();
	}

	/**
	 * Generates a write instruction.
	 *
	 * @param write The write instruction to generate.
	 */
	@Override
	protected void generateWrite(Write write) {
		String register = OptimizedExpressionParser.getInstance().getExpressionValue(write.getExpression());
		print(Instruction.MOVQ, Register.RSI, register);
		RegisterPool.getInstance().freeRegister(register);
		print(codeGenerator.utilities.Instruction.CALL, "write");
		RegisterPool.getInstance().reset();
	}

	/**
	 * Generates array bounds checking for a location.
	 *
	 * @param location The location containing the potential out-of-bounds expression.
	 * @param register The register that contains the value of the index.
	 */
	@Override
	public void generateArrayBoundsChecking(Location location, Register register) {
		print(codeGenerator.utilities.Instruction.CMP, register, ((Array) ((Index) location).getVariable().getType()).getLength() * 8);
		print(Instruction.JGE, "indexOutOfBounds");
		print(codeGenerator.utilities.Instruction.CMP, register, 0);
		print(Instruction.JL, "indexOutOfBounds");
	}

	/**
	 * Prints the read subroutine.
	 */
	@Override
	protected void printRead() {
		print("read:");
		indent();
		print(Instruction.LEA, Register.RDI, "formatRead");
		print(Instruction.POP, Register.RSI);
		print(Instruction.XOR, Register.RAX, Register.RAX);
		print(Instruction.CALL, "scanf");
		print(Instruction.LEA, OFFSET, SPACE);
		print(Instruction.RET);
		removeIndent();
		print("formatRead:");
		indent();
		print(".asciz \"%ld\"");
		removeIndent();
	}

	/**
	 * Prints the declarations.
	 */
	@Override
	protected void printDeclarations() {
		print(".section .data");
		indent();
		for (String declarationName : ScopeManager.getInstance().getProgramScope().getMap().keySet()) {
			printDeclaration(declarationName);
		}
		if (m_memorySize > 0) {
			print(SPACE + " : .quad 0");
			int adjustedSize = m_memorySize - 8;
			print(".space " + adjustedSize + ", 0");
		}
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
					m_variables.put(declarationName + arrayElement, m_variables.size() * 8);
				}
			} else if (variable.getType() instanceof Record) {
				for (String recordField : DeclarationMangler.getInstance().mangle((Record) variable.getType())) {
					m_variables.put(declarationName + recordField, m_variables.size() * 8);
				}
			} else if (variable.getType() instanceof Integer) {
				m_variables.put(declarationName, m_variables.size() * 8);
			}
		}
	}

	/**
	 * Initializes main.
	 */
	@Override
	protected void printMainInitialization() {
		print(Instruction.LEA, OFFSET, SPACE);
	}


	/**
	 * Prints the write subroutine.
	 */
	@Override
	protected void printWrite() {
		print("write:");
		indent();
		print(Instruction.LEA, Register.RDI, "formatWrite");
		print(Instruction.XOR, Register.RAX, Register.RAX);
		print(Instruction.CALL, "printf");
		print(Instruction.LEA, OFFSET, SPACE);
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
		print("indexOutOfBounds:");
		indent();
		print(Instruction.MOV, Register.RDI, "qword ptr stderr[" + Register.RIP + "]");
		print(Instruction.LEA, Register.RSI, "formatIndexOutOfBounds");
		print(Instruction.POP, Register.RDX);
		print(Instruction.POP, Register.RCX);
		print(Instruction.XOR, Register.RAX, Register.RAX);
		print(Instruction.CALL, "fprintf");
		// Exit
		print(Instruction.MOVQ, Register.RDI, 0);
		print(Instruction.CALL, "exit");
		removeIndent();
		print("formatIndexOutOfBounds:");
		indent();
		print(".asciz \"error: The array @(%d,%d) is out of bounds.\\n\"");
		removeIndent();
	}

	/**
	 * Gets the map of mangled variable names to their addresses in memory.
	 */
	public Map<String, java.lang.Integer> getVariables() {
		return m_variables;
	}
}
