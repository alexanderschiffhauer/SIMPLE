package codeGenerator.optimizedCodeGenerator;

import codeGenerator.AbstractExpressionParser;
import codeGenerator.utilities.Instruction;
import codeGenerator.utilities.Register;
import exception.Exception;
import exception.ExceptionHandler;
import exception.ExceptionStrength;
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
 * Parses expressions in an optimized fashion for AMD64.
 */
public class OptimizedExpressionParser extends AbstractExpressionParser {

	/**
	 * Represents the instance of the optimized expression parser.
	 */
	private static OptimizedExpressionParser m_instance;

	/**
	 * Gets the instance of the optimized expression parser.
	 */
	public static OptimizedExpressionParser getInstance() {
		if (m_instance == null) {
			m_instance = new OptimizedExpressionParser();
		}
		return m_instance;
	}

	/**
	 * Constructs the optimized expression parser.
	 */
	private OptimizedExpressionParser() {
	}

	/**
	 * Gets the value from a location.
	 *
	 * @param location The location whose value will be returned.
	 */
	@Override
	public String getLocationValue(Location location) {
		String address = getLocationAddress(location);
		print(Instruction.MOV, address, "qword ptr[" + address + "]");
		return address;
	}

	/**
	 * Gets the address from a location.  The address will be in a register.
	 *
	 * @param location The location whose address will be returned.
	 */
	@Override
	public String getLocationAddress(Location location) {
		String register;
		// If the location is constantly defined, then we grab its compile-time offset.
		int offset;
		if ((offset = offsetOf(location)) != -1) {
			register = RegisterPool.getInstance().requestNewRegister().toString();
			if (offset != 0) {
				print(Instruction.MOVQ, register, offsetOf(location));
				print(Instruction.ADDQ, register, OptimizedCodeGenerator.OFFSET);
			} else {
				print(Instruction.MOVQ, register, OptimizedCodeGenerator.OFFSET);
			}
			return register;
		}
		// If a location is constantly defined but is not in our map of locations-to-offsets, then the location has an array that's out of bounds.  We throw a compile-time error.
		else if (isConstant(location.toString())) {
			return ExceptionHandler.getInstance().throwException(Exception.INDEX_OUT_OF_RANGE_COMPILE_TIME, ExceptionStrength.STRONG, location.toString(), location.getType().getPosition());
		}
		// If none of our cool "a-ha's!" work, then we simply proceed to evaluate the offset normally.
		else {
			String offset1 = getLocationOffset(location);
			if (Register.contains(offset1)) {
				register = offset1;
				print(Instruction.ADDQ, register, OptimizedCodeGenerator.OFFSET);
			} else {
				register = RegisterPool.getInstance().requestNewRegister().toString();
				print(Instruction.MOVQ, register, offset1);
				print(Instruction.ADDQ, register, OptimizedCodeGenerator.OFFSET);
			}
			return register;
		}
	}

	/**
	 * Gets the offset from a location.  The offset will either be a numerical value or in a register.
	 *
	 * @param location The location whose offset will be returned.
	 */
	@Override
	protected String getLocationOffset(Location location) {
		String register = null;
		if (location instanceof Field) {
			Field field = (Field) location;
			try {
				String variableOffset = getLocationOffset(field.getVariable());
				register = parseBinaryExpression(variableOffset + " + " + String.valueOf(((Variable) field.getSelection()).getVariable().getOffset()));
				RegisterPool.getInstance().freeRegister(variableOffset);
			} catch (java.lang.Exception ignored) {
				Index array = (Index) field.getVariable();
				String expression = getExpressionValue(array.getExpression());
				String expression1 = parseBinaryExpression(expression + " * " + array.getVariable().getType().getSize());
				RegisterPool.getInstance().freeRegister(expression);
				register = parseBinaryExpression(String.valueOf(field.getSelection().getType().getSize()) + " * " + expression1); // Size of the type of the array * index of the array.
				RegisterPool.getInstance().freeRegister(expression1);
			}
		} else if (location instanceof Index) {
			Index index = (Index) location;
			Array array = (Array) index.getVariable().getType();

			String indexValue = getExpressionValue(index.getExpression());
			int multiplicand = array.getElementType().getSize() / 8;
			register = parseBinaryExpression(indexValue + " * " + multiplicand);
			RegisterPool.getInstance().freeRegister(indexValue);
			OptimizedCodeGenerator.getInstance().generateArrayBoundsChecking(location, Register.get(register));

			String variableOffset = getLocationOffset(index.getVariable()); // variableOffset has the address of a, when the expression is a[i].  We need to evaluate the expression, multiplying its value by the size of the type.
			String register1 = parseBinaryExpression(register + " + " + variableOffset);

			RegisterPool.getInstance().freeRegister(variableOffset);
			RegisterPool.getInstance().freeRegister(register);

			register = register1;
		} else if (location instanceof Variable) {
			Variable variable = (Variable) location;
			register = String.valueOf(variable.getVariable().getOffset());
		}
		return register;
	}

	/**
	 * Gets the value from a location, returning either the integral value or register with said value.
	 *
	 * @param expression The expression whose value will be returned.
	 */
	@Override
	public String getExpressionValue(Expression expression) {
		String value = null;
		if (expression instanceof Location) {
			value = getLocationAddress((Location) expression);
			print(Instruction.MOV, value, "qword ptr [" + value + "]");
		} else if (expression instanceof Binary) {
			Binary binary = (Binary) expression;
			String leftExpression, rightExpression;
			if (!(binary.getRightExpression() instanceof Binary)) {
				leftExpression = getExpressionValue(binary.getLeftExpression());
				rightExpression = getExpressionValue(binary.getRightExpression());
			} else {
				rightExpression = getExpressionValue(binary.getRightExpression());
				leftExpression = getExpressionValue(binary.getLeftExpression());
			}
			value = parseBinaryExpression(leftExpression + Operator.getOperator(binary.getOperator()) + rightExpression);
			RegisterPool.getInstance().freeRegister(leftExpression);
			RegisterPool.getInstance().freeRegister(rightExpression);
		} else if (expression instanceof parser.semanticAnalysis.abstractSyntaxTree.expressions.Number) {
			Number number = (Number) expression;
			value = String.valueOf(number.getConstant().getValue());
		}
		return value;
	}

	/**
	 * Gets the value from the stringified representation of a binary expression, putting the result in R13 if using a non-optimized code generator.
	 *
	 * @param binary The binary expression whose value will be returned.
	 */
	@Override
	protected String parseBinaryExpression(String binary) {
		int operatorIndex = getIndexOfOperator(binary);
		String left = getLeftOperand(binary.substring(0, operatorIndex)).trim();
		String right = getRightOperand(binary.substring(operatorIndex + 1)).trim();

		String register = RegisterPool.getInstance().requestNewRegister().toString();
		print(Instruction.MOVQ, register, left);

		Operator operator = Operator.getOperator(binary.substring(getIndexOfOperator(binary), getIndexOfOperator(binary) + 1));
		switch (operator) {
			case MINUS:
			case PLUS:
				print(Instruction.MOVQ, Register.RAX, right);
				if (operator == Operator.PLUS) {
					print(Instruction.ADDQ, register, Register.RAX);
				} else if (operator == Operator.MINUS) {
					print(Instruction.SUB, register, Register.RAX);
				}
				break;
			case MULTIPLICATION:
			case DIV:
			case MOD:
				print(Instruction.MOVQ, Register.RDX, "0");
				print(Instruction.MOVQ, Register.RAX, register);
				print(Instruction.CQO);
				print(Instruction.MOVQ, register, right);
				if (operator == Operator.DIV || operator == Operator.MOD) {
					print(Instruction.IDIV, register);
				} else {
					print(Instruction.IMUL, register);
				}
				if (operator == Operator.DIV) {
					print(Instruction.MOVQ, register, Register.RAX);
				} else if (operator == Operator.MOD) {
					print(Instruction.MOVQ, register, Register.RDX);
				} else if (operator == Operator.MULTIPLICATION) {
					print(Instruction.MOVQ, register, Register.RAX);
				}
				break;
		}
		return register;
	}

	/**
	 * Queries if an expression is constantly defined.
	 *
	 * @param expression The expression to query.
	 */
	private boolean isConstant(String expression) {
		if (expression.contains("[")) {
			String substring;
			int startingIndex, endingIndex;
			do {
				startingIndex = expression.indexOf("[");
				endingIndex = expression.indexOf("]");
				substring = expression.substring(startingIndex + 1, endingIndex);
				if (!isNumeric(substring)) {
					return false;
				}
				expression = expression.substring(endingIndex + 1, expression.length());
			} while (expression.contains("["));
		}
		return true;
	}

	/**
	 * Gets the index of the location in memory.
	 *
	 * @param location The location, whose index will be retrieved.
	 * @return The offset in memory, or -1 if it's not constantly defined.
	 */
	private int offsetOf(Location location) {
		String string = location.toString().replace("[", "_").replaceAll("]", "");
		if (OptimizedCodeGenerator.getInstance().getVariables().containsKey(string)) {
			return OptimizedCodeGenerator.getInstance().getVariables().get(string);
		} else {
			int min = Integer.MAX_VALUE;
			boolean found = false;
			for (String variable : OptimizedCodeGenerator.getInstance().getVariables().keySet()) {
				if (variable.startsWith(string)) {
					found = true;
					if (OptimizedCodeGenerator.getInstance().getVariables().get(variable) < min) {
						min = OptimizedCodeGenerator.getInstance().getVariables().get(variable);
					}
				}
			}
			if (found) {
				return min;
			} else {
				return -1;
			}
		}
	}
}
