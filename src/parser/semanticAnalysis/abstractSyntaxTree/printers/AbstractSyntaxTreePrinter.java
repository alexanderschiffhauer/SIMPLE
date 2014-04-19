package parser.semanticAnalysis.abstractSyntaxTree.printers;

import parser.Parser;
import parser.semanticAnalysis.abstractSyntaxTree.conditions.Condition;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.Binary;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.Expression;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.Number;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Field;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Index;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Location;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Variable;
import parser.semanticAnalysis.abstractSyntaxTree.instructions.*;
import parser.semanticAnalysis.symbolTable.declarations.Declaration;
import parser.semanticAnalysis.symbolTable.declarations.constants.constant.Constant;
import parser.semanticAnalysis.symbolTable.declarations.types.Type;
import parser.semanticAnalysis.symbolTable.declarations.types.array.Array;
import parser.semanticAnalysis.symbolTable.declarations.types.record.Record;
import parser.semanticAnalysis.symbolTable.scope.Scope;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Prints abstract syntax trees.
 */
public class AbstractSyntaxTreePrinter implements IAbstractSyntaxTreePrinter {

	/**
	 * Represents the instance of abstract syntax tree printer.
	 */
	private static AbstractSyntaxTreePrinter m_instance;
	/**
	 * Represents the spaces to add in front of each line.
	 */
	private String m_spaces = "";

	/**
	 * Gets the instance of abstract syntax tree printer.
	 */
	public static AbstractSyntaxTreePrinter getInstance() {
		if (m_instance == null) {
			m_instance = new AbstractSyntaxTreePrinter();
		}
		return m_instance;
	}

	/**
	 * Constructs the abstract syntax tree printer.
	 */
	private AbstractSyntaxTreePrinter() {
	}

	/**
	 * Prints an abstract syntax tree.
	 */
	@Override
	public void printAbstractSyntaxTree() {
		printInstructions(Parser.getInstance().getAbstractSyntaxTree());
	}

	/**
	 * Prints instructions.
	 *
	 * @param instructions The instructions to print.
	 */
	private void printInstructions(List<Instruction> instructions) {
		printInstructions(instructions, "");
	}

	/**
	 * Prints instructions.
	 *
	 * @param instructions The instructions to print.
	 * @param prefix       The prefix to print.
	 */
	private void printInstructions(List<Instruction> instructions, String prefix) {
		if (instructions != null) {
			if (prefix.equals("")) {
				print("instructions =>");
			} else {
				print(prefix);
			}
			incrementIndent();
			for (Instruction instruction : instructions) {
				printInstruction(instruction);
			}
		}
	}

	/**
	 * Prints an instruction.
	 *
	 * @param instruction The instruction to print.
	 */
	private void printInstruction(Instruction instruction) {
		if (instruction instanceof Assign) {
			Assign assignInstruction = (Assign) instruction;

			print("Assign:");
			printExpression(assignInstruction.getLocation());
			printExpression(assignInstruction.getExpression());
		} else if (instruction instanceof If) {
			If ifInstruction = (If) instruction;

			print("If:");
			printCondition(ifInstruction.getCondition());
			printInstructions(ifInstruction.getTrueInstructions(), "true =>");
			printInstructions(ifInstruction.getFalseInstructions(), "false =>");
		} else if (instruction instanceof Read) {
			Read readInstruction = (Read) instruction;

			print("Read:");
			printExpression(readInstruction.getLocation());
		} else if (instruction instanceof Repeat) {
			Repeat repeatInstruction = (Repeat) instruction;

			print("Repeat:");
			printCondition(repeatInstruction.getCondition());
			printInstructions(repeatInstruction.getInstructions());
		} else if (instruction instanceof Write) {
			Write writeInstruction = (Write) instruction;

			print("Write:");
			printExpression(writeInstruction.getExpression());
		}
	}

	/**
	 * Prints an expression.
	 *
	 * @param expression The expression to print.
	 */
	private void printExpression(Expression expression) {
		printExpression(expression, true, "");
	}

	/**
	 * Prints an expression.
	 *
	 * @param expression The expression to print.
	 * @param prefix     The custom string to print as the prefix.
	 */
	private void printExpression(Expression expression, String prefix) {
		printExpression(expression, true, prefix);
	}

	/**
	 * Prints an expression.
	 *
	 * @param expression   The expression to print.
	 * @param appendPrefix Whether to append "location =>" to the print.
	 * @param prefix       The custom string to print as the prefix.
	 */
	private void printExpression(Expression expression, boolean appendPrefix, String prefix) {
		if (expression instanceof Location) {
			if (appendPrefix) {
				if (prefix.equals("")) {
					print("location =>");
				} else {
					print(prefix);
				}
				incrementIndent();
			}
			Location location = (Location) expression;
			if (location instanceof Field) {
				Field field = (Field) location;

				print("Field:");
				printExpression(field.getVariable());
				if (field.getSelection() instanceof Variable) {
					printExpression(field.getSelection(), "variable =>");
				} else {
					printExpression(field.getSelection());
				}
			} else if (location instanceof Index) {
				Index index = (Index) location;

				print("Index:");
				printExpression(index.getVariable());
				printExpression(index.getExpression(), "expression =>");
			} else if (location instanceof Variable) {
				Variable variable = (Variable) location;

				print("Variable:");
				print("variable =>");
				printVariable(variable.getVariable());
			}
			if (appendPrefix) {
				decrementIndent();
			}
		} else {
			if (appendPrefix) {
				if (prefix.equals("")) {
					print("expression =>");
				} else {
					print(prefix);
				}
				incrementIndent();
			}
			if (expression instanceof Binary) {
				Binary binary = (Binary) expression;
				printBinaryExpression(binary);
			} else if (expression instanceof Number) {
				Number number = (Number) expression;

				print("Number:");
				print("value =>");
				printConstant(number.getConstant());
			}
			if (appendPrefix) {
				decrementIndent();
			}
		}
	}


	/**
	 * Prints a condition.
	 *
	 * @param condition The condition to print.
	 */
	private void printCondition(Condition condition) {
		print("condition =>");
		incrementIndent();
		print("Condition (" + condition.getRelation().toString() + "):");
		print("left =>");
		incrementIndent();
		printExpression(condition.getLeftExpression(), false, "");
		decrementIndent();
		print("right =>");
		incrementIndent();
		printExpression(condition.getRightExpression(), false, "");
		decrementIndent();
		decrementIndent();
	}

	/**
	 * Prints a binary condition.
	 *
	 * @param expression The expression to print.
	 */
	private void printBinaryExpression(Binary expression) {
		print("Binary (" + expression.getOperator().toString() + "):");
		print("left =>");
		incrementIndent();
		printExpression(expression.getLeftExpression(), false, "");
		decrementIndent();
		print("right =>");
		incrementIndent();
		printExpression(expression.getRightExpression(), false, "");
		decrementIndent();
	}

	/**
	 * Increases the amount of spaces in each line by two.
	 */
	private void incrementIndent() {
		m_spaces += "  ";
	}

	/**
	 * Decreases the amount of spaces in each line by two.
	 */
	private void decrementIndent() {
		if (m_spaces.length() >= 2) {
			m_spaces = m_spaces.substring(0, m_spaces.length() - 2);
		}
	}

	/**
	 * Prints a string.
	 *
	 * @param string The string to print.
	 */
	private void print(String string) {
		System.out.println(m_spaces + string);
	}

	//<editor-fold desc="HACKERY">

	/**
	 * Prints a variable.
	 *
	 * @param variable The variable to print.
	 */
	private void printVariable(parser.semanticAnalysis.symbolTable.declarations.variable.Variable variable) {
		incrementIndent();
		print("VAR BEGIN");
		incrementIndent();
		print("type:");
		incrementIndent();
		printTypeRecursively(variable.getType());
		decrementIndent();
		decrementIndent();
		print("END VAR");
		decrementIndent();
	}

	/**
	 * Prints a type recursively.
	 *
	 * @param type The type to print recursively.
	 */
	private void printTypeRecursively(Type type) {
		print(type.getName());
	}

	/**
	 * Prints a type.
	 *
	 * @param name The name of the type.
	 * @param type The type to print.
	 */
	private void printType(String name, Type type) {
		if (type instanceof Array) {
			printArray(name, (Array) type);
		} else if (type instanceof Record) {
			printRecord(name, (Record) type);
		}
	}

	/**
	 * Prints an array.
	 *
	 * @param array The array to print.
	 */
	private void printArray(Array array) {
		print("ARRAY BEGIN");
		incrementIndent();
		print("type:");
		incrementIndent();
		printTypeRecursively(array.getElementType());
		decrementIndent();
		print("length:");
		incrementIndent();
		print(String.valueOf(array.getLength()));
		decrementIndent();
		decrementIndent();
		print("END ARRAY");
	}

	/**
	 * Prints an array.
	 *
	 * @param name  The name of the array.
	 * @param array The array to print.
	 */
	private void printArray(String name, Array array) {
		print(name + " =>");
		incrementIndent();
		printArray(array);
		decrementIndent();
	}

	/**
	 * Prints a record.
	 *
	 * @param name   The name of the array.
	 * @param record The record to print.
	 */
	private void printRecord(String name, Record record) {
		print(name + " =>");
		incrementIndent();
		printRecord(record);
		decrementIndent();
	}

	/**
	 * Prints a record.
	 *
	 * @param record The record to print.
	 */
	private void printRecord(Record record) {
		print("RECORD BEGIN");
		incrementIndent();
		printScope(record.getScope());
		decrementIndent();
		print("END RECORD");
	}

	/**
	 * Prints the scope.
	 */
	private void printScope(Scope scope) {
		Map<String, Declaration> map = scope.getMap();
		List<String> names = new ArrayList<String>(map.keySet());
		Collections.sort(names);
		print("SCOPE BEGIN");
		incrementIndent();
		for (String name : names) {
			Declaration declaration = map.get(name);
			if (declaration instanceof Constant) {
				printConstant(name, (Constant) declaration);
			} else if (declaration instanceof parser.semanticAnalysis.symbolTable.declarations.variable.Variable) {
				print(name + " =>");
				printVariable((parser.semanticAnalysis.symbolTable.declarations.variable.Variable) declaration);
			} else if (declaration instanceof Type) {
				printType(name, (Type) declaration);
			}
		}
		decrementIndent();
		print("END SCOPE");
	}

	/**
	 * Prints a constant.
	 *
	 * @param name     The name of the array.
	 * @param constant The constant to print.
	 */
	private void printConstant(String name, Constant constant) {
		print(name + " =>");
		incrementIndent();
		printConstant(constant);
		decrementIndent();
	}

	/**
	 * Prints a constant.
	 *
	 * @param constant The constant to print.
	 */
	private void printConstant(Constant constant) {
		incrementIndent();
		print("CONST BEGIN");
		incrementIndent();
		print("type:");
		incrementIndent();
		printTypeRecursively(parser.semanticAnalysis.symbolTable.declarations.types.Integer.getInstance());
		decrementIndent();
		print("value:");
		incrementIndent();
		print(String.valueOf((java.lang.Integer) constant.getValue()));
		decrementIndent();
		decrementIndent();
		print("END CONST");
		decrementIndent();
	}
	//</editor-fold>
}
