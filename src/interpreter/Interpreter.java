package interpreter;

import exception.Exception;
import exception.ExceptionHandler;
import exception.ExceptionStrength;
import interpreter.boxes.ArrayBox;
import interpreter.boxes.Box;
import interpreter.boxes.IntegerBox;
import interpreter.boxes.RecordBox;
import parser.semanticAnalysis.abstractSyntaxTree.conditions.Condition;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.Binary;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.Expression;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.Function;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.Number;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Field;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Index;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Location;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Variable;
import parser.semanticAnalysis.abstractSyntaxTree.instructions.*;
import parser.semanticAnalysis.symbolTable.declarations.procedures.Argument;
import parser.semanticAnalysis.symbolTable.declarations.procedures.Parameter;
import parser.semanticAnalysis.symbolTable.declarations.types.array.Array;
import parser.semanticAnalysis.symbolTable.scope.ScopeManager;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

/**
 * Represents the interpreter, which interprets the abstract syntax tree and environment.
 */
public class Interpreter {

	/**
	 * Represents the instance of the interpreter.
	 */
	private static Interpreter m_instance;

	/**
	 * Constructs the interpreter.
	 */
	private Interpreter() {
	}

	/**
	 * Gets the instance of the interpreter.
	 */
	public static Interpreter getInstance() {
		if (m_instance == null) {
			m_instance = new Interpreter();
		}
		return m_instance;
	}

	/**
	 * Interprets the abstract syntax tree and environment.
	 *
	 * @param instructions The instructions to interpret.
	 */
	public void interpret(List<Instruction> instructions) {
		Environment.getInstance().environmentalize(ScopeManager.getInstance().getProgramScope());
		interpretInstructions(instructions);
	}

	/**
	 * Interprets a list of instructions.
	 *
	 * @param instructions The instructions to interpret.
	 */
	private void interpretInstructions(List<Instruction> instructions) {
		for (Instruction instruction : instructions) {
			if (instruction instanceof Assign) {
				interpretAssign((Assign) instruction);
			} else if (instruction instanceof If) {
				interpretIf((If) instruction);
			} else if (instruction instanceof Repeat) {
				interpretRepeat((Repeat) instruction);
			} else if (instruction instanceof Read) {
				interpretRead((Read) instruction);
			} else if (instruction instanceof Write) {
				interpretWrite((Write) instruction);
			} else if (instruction instanceof Call) {
				interpretCall((Call) instruction);
				Environment.getInstance().pop();
			}
		}
	}

	/**
	 * Interprets an assign instruction.
	 *
	 * @param instruction The assign instruction to interpret.
	 */
	private void interpretAssign(Assign instruction) {
		Location location = instruction.getLocation();
		Expression expression = instruction.getExpression();

		Box box = getBox(location);
		if (box instanceof IntegerBox) {
			((IntegerBox) box).setValue(reduceIntegralExpression(expression));
		} else if (box instanceof ArrayBox) {
			ArrayBox arrayBox = (ArrayBox) getBox(location);
			ArrayBox clonedArrayBox = (ArrayBox) getBox((Location) expression).clone();
			arrayBox.clone(clonedArrayBox);
		} else if (box instanceof RecordBox) {
			RecordBox recordBox = (RecordBox) getBox(location);
			RecordBox clonedRecordBox = (RecordBox) getBox((Location) expression).clone();
			recordBox.clone(clonedRecordBox);
		}
	}

	/**
	 * Gets the box from the location recursively (i.e. the variable, field, or index).
	 *
	 * @param location The location, whose box to get.
	 */
	private Box getBox(Location location) {
		if (location instanceof Field) {
			Field field = (Field) location;
			Box box = getBox(field.getVariable());
			try {
				RecordBox variable = (RecordBox) box;
				Box selection;
				if (field.getSelection() instanceof Variable) {
					selection = variable.getMap().get(((Variable) (field.getSelection())).getVariable().getName());
				} else {
					selection = variable.getMap().get(field.getSelection().getType().getName());
				}
				return selection;
			} catch (java.lang.Exception exception) {
				try {
					IntegerBox integerBox = (IntegerBox) box;
					return integerBox;
				} catch (java.lang.Exception exception1) {
					ArrayBox arrayBox = (ArrayBox) box;
					return arrayBox;
				}
			}
		} else if (location instanceof Index) {
			Index array = (Index) location;
			int index = reduceIntegralExpression(array.getExpression());
			ArrayBox variable = (ArrayBox) getBox(array.getVariable());
			if (index >= variable.getBoxes().size() || index < 0) {
				Array array1 = (Array) array.getVariable().getType();
				ExceptionHandler.getInstance().throwException(Exception.INDEX_OUT_OF_RANGE, ExceptionStrength.STRONG, index, array1.getName(), array1.getPosition());
			}
			return variable.getBoxes().get(index);
		} else if (location instanceof Variable) {
			Variable variable = (Variable) location;
			return Environment.getInstance().find(variable.getVariable().getName());
		}
		return ExceptionHandler.getInstance().throwException(Exception.ENCOUNTERED_A_BUG, ExceptionStrength.STRONG);
	}

	/**
	 * Reduces an expression to an integer.
	 *
	 * @param expression The expression to reduce.
	 */
	private java.lang.Integer reduceIntegralExpression(Expression expression) {
		if (expression.isNumeric()) {
			if (expression instanceof Number) {
				Number number = (Number) expression;
				return number.getConstant().getValue();
			} else if (expression instanceof Binary) {
				Binary binary = (Binary) expression;
				int leftExpression = reduceIntegralExpression(binary.getLeftExpression());
				int rightExpression = reduceIntegralExpression(binary.getRightExpression());
				switch (binary.getOperator()) {
					case PLUS:
						return leftExpression + rightExpression;
					case MINUS:
						return leftExpression - rightExpression;
					case MULTIPLICATION:
						return leftExpression * rightExpression;
					case DIV:
						if (rightExpression == 0) {
							return ExceptionHandler.getInstance().throwException(Exception.DIVIDE_BY_ZERO, ExceptionStrength.STRONG, expression.getType().getPosition());
						}
						return leftExpression / rightExpression;
					case MOD:
						if (rightExpression == 0) {
							return ExceptionHandler.getInstance().throwException(Exception.MOD_BY_ZERO, ExceptionStrength.STRONG, expression.getType().getPosition());
						}
						return leftExpression % rightExpression;
					default:
						return ExceptionHandler.getInstance().throwException(Exception.ENCOUNTERED_A_BUG, ExceptionStrength.STRONG);
				}
			} else if (expression instanceof Location) {
				return ((IntegerBox) getBox((Location) expression)).getValue();
			} else if (expression instanceof Function) {
				Function function = (Function) expression;
				interpretCall(function.getCall());
				java.lang.Integer integer = reduceIntegralExpression(function.getCall().getProcedure().getReturnExpression());
				Environment.getInstance().pop();
				return integer;
			} else {
				return ExceptionHandler.getInstance().throwException(Exception.ENCOUNTERED_A_BUG, ExceptionStrength.STRONG);
			}
		} else {
			return ExceptionHandler.getInstance().throwException(Exception.ENCOUNTERED_A_BUG, ExceptionStrength.STRONG);
		}
	}

	/**
	 * Interprets an if instruction.
	 *
	 * @param instruction The if instruction to interpret.
	 */
	private void interpretIf(If instruction) {
		if (interpretCondition(instruction.getCondition())) {
			interpretInstructions(instruction.getTrueInstructions());
		} else if (instruction.falseInstructionsExist()) {
			interpretInstructions(instruction.getFalseInstructions());
		}
	}

	/**
	 * Interprets a repeat instruction.
	 *
	 * @param instruction The repeat instruction to interpret.
	 */
	private void interpretRepeat(Repeat instruction) {
		do {
			interpretInstructions(instruction.getInstructions());
		} while (interpretCondition(instruction.getCondition()));
	}

	/**
	 * Interprets a condition.
	 *
	 * @param condition The condition to interpret
	 */
	private boolean interpretCondition(Condition condition) {
		int leftExpression = reduceIntegralExpression(condition.getLeftExpression());
		int rightExpression = reduceIntegralExpression(condition.getRightExpression());
		switch (condition.getRelation()) {
			case EQUALITY:
				if (leftExpression == rightExpression) {
					return true;
				}
				break;
			case INEQUALITY:
				if (leftExpression != rightExpression) {
					return true;
				}
				break;
			case LESS_THAN:
				if (leftExpression < rightExpression) {
					return true;
				}
				break;
			case GREATER_THAN:
				if (leftExpression > rightExpression) {
					return true;
				}
				break;
			case LESS_THAN_OR_EQUAL_TO:
				if (leftExpression <= rightExpression) {
					return true;
				}
				break;
			case GREATER_THAN_OR_EQUAL_TO:
				if (leftExpression >= rightExpression) {
					return true;
				}
				break;
		}
		return false;
	}

	/**
	 * Interprets a read instruction.
	 *
	 * @param instruction The read instruction to interpret.
	 */
	private void interpretRead(Read instruction) {
		Scanner inputScanner = new Scanner(System.in);
		if (inputScanner.hasNextLine()) {
			String input = inputScanner.nextLine();
			try {
				int value = java.lang.Integer.parseInt(input);
				((IntegerBox) getBox(instruction.getLocation())).setValue(value);
			} catch (java.lang.Exception exception) {
				ExceptionHandler.getInstance().throwException(Exception.READ_INSTRUCTION_REQUIRES_AN_INTEGER, ExceptionStrength.WEAK, input);
				interpretRead(instruction);
			}
		}
	}

	/**
	 * Interprets a write instruction.
	 *
	 * @param instruction The write instruction to interpret.
	 */
	private void interpretWrite(Write instruction) {
		System.out.println(java.lang.Integer.toString(reduceIntegralExpression(instruction.getExpression())));
	}

	/**
	 * Interprets a call instruction.
	 *
	 * @param call The call to interpret.
	 */
	private void interpretCall(Call call) {
		LinkedHashMap<String, Box> map = new LinkedHashMap<String, Box>();
		for (int i = 0; i < call.getProcedure().getParameters().size(); i++) {
			Parameter parameter = call.getProcedure().getParameters().get(i);
			Argument argument = call.getArguments().get(i);
			if (argument.getExpression() instanceof Location) {
				map.put(parameter.getName(), getBox((Location) argument.getExpression()));
			} else {
				map.put(parameter.getName(), new IntegerBox(reduceIntegralExpression(argument.getExpression())));
			}
		}

		Environment.getInstance().environmentalize(call.getProcedure().getLocalVariables());
		Environment.getInstance().replace(map);

		interpretInstructions(call.getProcedure().getInstructions());
	}
}
