package codeGenerator.codeGenerator;

import codeGenerator.AbstractExpressionParser;
import codeGenerator.utilities.Instruction;
import codeGenerator.utilities.Register;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.Binary;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.Expression;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.Number;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.Operator;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Field;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Index;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Location;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Variable;
import parser.semanticAnalysis.symbolTable.declarations.types.array.Array;

/**
 * Parses expressions for AMD64.
 */
public class ExpressionParser extends AbstractExpressionParser {

	/**
	 * Represents the instance of the expression parser.
	 */
	private static ExpressionParser m_instance;

	/**
	 * Gets the instance of the expression parser.
	 */
	public static ExpressionParser getInstance() {
		if (m_instance == null) {
			m_instance = new ExpressionParser();
		}
		return m_instance;
	}

	/**
	 * Constructs the the expression parser.
	 */
	private ExpressionParser() {}

	/**
	 * Gets the value from a location, returning either the mangled name of the variable for use in assembly or the stringified representation of the register that contains the value of the variable.
	 *
	 * @param location The location whose value will be returned.
	 */
	@Override
	public String getLocationValue(Location location) {
		String register = getLocationAddress(location);
		print(Instruction.MOV, register, "qword ptr[" + register + "]");
		return register;
	}

	/**
	 * Gets the address from a location.  The address is guaranteed to be in R15 if using a non-optimized code generator.
	 *
	 * @param location The location whose address will be returned.
	 */
	@Override
	public String getLocationAddress(Location location) {
		String register = getLocationOffset(location);
		print(Instruction.ADDQ, register, "_base");
		return register;
	}

	/**
	 * Gets the offset from a location.  The offset is guaranteed to be in R15 if using a non-optimized code generator.
	 *
	 * @param location The location whose offset will be returned.
	 */
	@Override
	protected String getLocationOffset(Location location) {
		if (location instanceof Field) {
			Field field = (Field) location;
			getLocationOffset(field.getVariable()); // R15 has the offset of the VARIABLE
			try {
				parseBinaryExpression(Register.R15 + " + " + String.valueOf(((Variable) field.getSelection()).getVariable().getOffset())); // Offset of the SELECTION
				print(Instruction.MOVQ, Register.R15, Register.R13); // R15 has the COMPLETE offset (e.g. a.x)
			} catch (Exception ignored) {
				Index array = (Index) field.getVariable();
				getExpressionValue(array.getExpression()); // R14 has the INDEX of the array.
				parseBinaryExpression(Register.R14 + " * " + array.getVariable().getType().getSize()); // R13 has the OFFSET of the INDEX.  R14 = i * size
				print(Instruction.MOVQ, Register.R14, Register.R13); // R14 has the OFFSET of the INDEX.  R14 = i * size
				parseBinaryExpression(String.valueOf(field.getSelection().getType().getSize()) + " * " + Register.R14); // Size type of array * index of array => 13 has the COMPLETE offset (e.g. a.a[1]).
				print(Instruction.MOVQ, Register.R15, Register.R13);
			}
		} else if (location instanceof Index) {
			Index index = (Index) location;
			Array array = (Array) index.getVariable().getType();
			getLocationOffset(index.getVariable()); // R15 has the address of a, when the expression is a[i].  We need to evaluate the expression, multiplying its value by the size of the type.
			print(Instruction.PUSH, Register.R15); // Save the address of a[0].
			print(Instruction.MOVQ, Register.R15, getExpressionValue(index.getExpression())); // R15 has the value of i
			print(Instruction.MOVQ, "_temp_index", array.getElementType().getSize()); // temp has the size of each element.
			parseBinaryExpression(Register.R15 + "* _temp_index"); // R13 has the value of sizeOfElement*i
			print(Instruction.MOVQ, Register.R15, Register.R13); // R15 has the value of sizeOfElement*i

			parseBinaryExpression(Register.R15 + "/ 8");
			CodeGenerator.getInstance().generateArrayBoundsChecking(location, Register.R13);


			print(Instruction.POP, "_temp_index"); // temp has the address of a[0]
			parseBinaryExpression(Register.R15 + "+ _temp_index"); // R15 has the address of a[i]
			print(Instruction.MOVQ, Register.R15, Register.R13);
		} else if (location instanceof Variable) {
			Variable variable = (Variable) location;
			print(Instruction.MOVQ, Register.R15, variable.getVariable().getOffset()); // R15 has the address of the variable.
		}
		return Register.R15.toString();
	}

	/**
	 * Gets the value from a location, returning either the mangled name of the variable for use in assembly or the stringified representation of R14 if using a non-optimized code generator, which contains the value of the expression.
	 *
	 * @param expression The expression whose value will be returned.
	 */
	@Override
	public String getExpressionValue(Expression expression) {
		if (expression instanceof Location) {
			print(Instruction.MOV, Register.R14, "qword ptr [" + getLocationAddress((Location) expression) + "]");
		} else if (expression instanceof Binary) {
			Binary binary = (Binary) expression;
			print(Instruction.PUSH, getExpressionValue(binary.getRightExpression()));
			print(Instruction.PUSH, getExpressionValue(binary.getLeftExpression()));
			print(Instruction.POP, "_temp_expression_left");
			print(Instruction.POP, "_temp_expression_right");
			parseBinaryExpression("_temp_expression_left " + Operator.getOperator(binary.getOperator()) + " _temp_expression_right");
			print(Instruction.MOVQ, Register.R14, Register.R13);
		} else if (expression instanceof Number) {
			Number number = (Number) expression;
			print(Instruction.MOVQ, Register.R14, String.valueOf(number.getConstant().getValue()));
		}
		return Register.R14.toString();
	}

	/**
	 * Parsers a binary expression, putting the result in R13 if using a non-optimized code generator.
	 *
	 * @param binary The binary expression to reduce.
	 */
	@Override
	protected String parseBinaryExpression(String binary) {
		int operatorIndex = getIndexOfOperator(binary);
		String left = getLeftOperand(binary.substring(0, operatorIndex)).trim();
		String right = getRightOperand(binary.substring(operatorIndex + 1)).trim();
		Operator operator = Operator.getOperator(binary.substring(getIndexOfOperator(binary), getIndexOfOperator(binary) + 1));
		switch (operator) {
			case MINUS:
			case PLUS:
				print(Instruction.MOVQ, Register.R13, left);
				print(Instruction.MOVQ, Register.RAX, right);
				if (operator == Operator.PLUS) {
					print(Instruction.ADDQ, Register.R13, Register.RAX);
				} else if (operator == Operator.MINUS) {
					print(Instruction.SUB, Register.R13, Register.RAX);
				}
				break;
			case MULTIPLICATION:
			case DIV:
			case MOD:
				print(Instruction.MOVQ, Register.RDX, "0");
				print(Instruction.MOVQ, Register.RAX, left);
				print(Instruction.CQO);
				print(Instruction.MOVQ, Register.R13, right);
				if (operator == Operator.DIV || operator == Operator.MOD) {
					print(Instruction.IDIV, Register.R13);
				} else {
					print(Instruction.IMUL, Register.R13);
				}
				if (operator == Operator.DIV) {
					print(Instruction.MOVQ, Register.R13, Register.RAX);
				} else if (operator == Operator.MOD) {
					print(Instruction.MOVQ, Register.R13, Register.RDX);
				} else if (operator == Operator.MULTIPLICATION) {
					print(Instruction.MOVQ, Register.R13, Register.RAX);
				}
				break;
		}
		return Register.R13.toString();
	}
}
