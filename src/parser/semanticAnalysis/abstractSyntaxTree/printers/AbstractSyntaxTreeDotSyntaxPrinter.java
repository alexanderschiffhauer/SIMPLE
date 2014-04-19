package parser.semanticAnalysis.abstractSyntaxTree.printers;

import parser.Parser;
import parser.semanticAnalysis.abstractSyntaxTree.Node;
import parser.semanticAnalysis.abstractSyntaxTree.conditions.Condition;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.Binary;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.Expression;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.Number;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Field;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Index;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Location;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Variable;
import parser.semanticAnalysis.abstractSyntaxTree.instructions.*;
import parser.semanticAnalysis.symbolTable.declarations.constants.constant.Constant;

import java.util.List;

/**
 * Prints abstract syntax trees using dot syntax.
 */
public class AbstractSyntaxTreeDotSyntaxPrinter implements IAbstractSyntaxTreePrinter {

	/**
	 * Represents the instance of the abstract syntax tree dot syntax printer.
	 */
	private static AbstractSyntaxTreeDotSyntaxPrinter m_instance;

	/**
	 * Gets the instance of the abstract syntax tree dot syntax printer.
	 */
	public static AbstractSyntaxTreeDotSyntaxPrinter getInstance() {
		if (m_instance == null) {
			m_instance = new AbstractSyntaxTreeDotSyntaxPrinter();
		}
		return m_instance;
	}

	/**
	 * Constructs the abstract syntax tree dot syntax printer.
	 */
	private AbstractSyntaxTreeDotSyntaxPrinter() {}

	/**
	 * Prints an abstract syntax tree.
	 */
	@Override
	public void printAbstractSyntaxTree() {
		print("digraph X {");
		printInstructions(Parser.getInstance().getAbstractSyntaxTree());
		print("}");
	}

	void printInstructions(List<Instruction> instructions) {
		String previousInstruction = "";
		for (Instruction instruction : instructions) {
			printInstructionNode(instruction);
			printInstruction(instruction);
			if (previousInstruction.equals("")) {
				previousInstruction = getUniqueString(instruction);
			} else {
				print("{rank=same; " + previousInstruction + " -> " + getUniqueString(instruction) + " [label=next];}");
				previousInstruction = getUniqueString(instruction);
			}
		}
	}

	void printInstruction(Instruction instruction) {
		if (instruction instanceof Assign) {
			Assign assignInstruction = (Assign) instruction;

			printExpressionNode(assignInstruction.getLocation());
			printConnection(instruction, assignInstruction.getLocation(), printExpression(assignInstruction.getLocation()));

			printExpressionNode(assignInstruction.getExpression());
			printConnection(instruction, assignInstruction.getExpression(), printExpression(assignInstruction.getExpression()));
		} else if (instruction instanceof If) {
			If ifInstruction = (If) instruction;

			printCondition(ifInstruction, ifInstruction.getCondition());
			if (ifInstruction.getTrueInstructions().size() > 0) {
				printInstructions(ifInstruction.getTrueInstructions());
				printConnection(ifInstruction, ifInstruction.getTrueInstructions().get(0), "true");
			}
			if (ifInstruction.getFalseInstructions().size() > 0) {
				printInstructions(ifInstruction.getFalseInstructions());
				printConnection(ifInstruction, ifInstruction.getFalseInstructions().get(0), "false");
			}

		} else if (instruction instanceof Read) {
			Read readInstruction = (Read) instruction;

			printExpressionNode(readInstruction.getLocation());
			printConnection(instruction, readInstruction.getLocation(), printExpression(readInstruction.getLocation()));

		} else if (instruction instanceof Repeat) {
			Repeat repeatInstruction = (Repeat) instruction;

			printCondition(repeatInstruction, repeatInstruction.getCondition());
			if (repeatInstruction.getInstructions().size() > 0) {
				printInstructions(repeatInstruction.getInstructions());
				printConnection(repeatInstruction, repeatInstruction.getInstructions().get(0), "instructions");
			}

		} else if (instruction instanceof Write) {
			Write writeInstruction = (Write) instruction;

			printExpressionNode(writeInstruction.getExpression());
			printConnection(writeInstruction, writeInstruction.getExpression(), printExpression(writeInstruction.getExpression()));
		}
	}

	String printExpression(Expression expression) {
		String connection = "location";
		if (expression instanceof Location) {
			Location location = (Location) expression;
			connection = "location";

			if (location instanceof Field) {
				Field field = (Field) location;

				printExpressionNode(field.getVariable());
				printConnection(field, field.getVariable(), printExpression(field.getVariable()));

				printExpressionNode(field.getSelection());
				printConnection(field, field.getSelection(), printExpression(field.getSelection()));
			} else if (location instanceof Index) {
				Index index = (Index) location;

				printExpressionNode(index.getVariable());
				printConnection(index, index.getVariable(), printExpression(index.getVariable()));

				printExpressionNode(index.getExpression());
				printExpression(index.getExpression());
				printConnection(index, index.getExpression(), "expression");
			} else if (location instanceof Variable) {
				Variable variable = (Variable) location;
				parser.semanticAnalysis.symbolTable.declarations.variable.Variable uniqueVariable = new parser.semanticAnalysis.symbolTable.declarations.variable.Variable();
				uniqueVariable.setName(variable.getVariable().getName());

				print(getUniqueString(uniqueVariable) + " [label=\"" + variable.getVariable().getName() + "\",shape=circle];");
				printConnection(variable, uniqueVariable, "ST");
			}
		} else {
			if (expression instanceof Binary) {
				Binary binary = (Binary) expression;
				connection = "expression";

				printExpressionNode(binary.getLeftExpression());
				printExpression(binary.getLeftExpression());

				printExpressionNode(binary.getRightExpression());
				printExpression(binary.getRightExpression());

				printConnection(binary, binary.getLeftExpression(), "left");
				printConnection(binary, binary.getRightExpression(), "right");
			} else if (expression instanceof Number) {
				Number number = (Number) expression;
				connection = "expression";

				Number uniqueNumber = new Number(new Constant(number.getConstant().getValue()));
				print(getUniqueString(uniqueNumber.getConstant()) + " [label=\"" + number.getConstant().getValue() + "\",shape=diamond];");
				printConnection(number, uniqueNumber.getConstant(), "ST");
			}
		}
		return connection;
	}

	Condition printCondition(Node root, Condition condition) {
		print(getUniqueString(condition) + " [label=\"" + condition.getRelation().toString() + "\",shape=box];");
		printConnection(root, condition, "condition");

		printExpressionNode(condition.getLeftExpression());
		printExpression(condition.getLeftExpression());
		printConnection(condition, condition.getLeftExpression(), "left");

		printExpressionNode(condition.getRightExpression());
		printExpression(condition.getRightExpression());
		printConnection(condition, condition.getRightExpression(), "right");

		return condition;
	}

	/**
	 * Prints a string.
	 *
	 * @param string The string to print.
	 */
	private String print(String string) {
		System.out.println(string);
		return string;
	}


	void printInstructionNode(Instruction instruction) {
		String displayedName = instruction.getClass().getSimpleName();
		if (instruction instanceof Assign) {
			displayedName = ":=";
		}
		print(getUniqueString(instruction) + " [label=\"" + displayedName + "\",shape=box];");
	}

	private void printConnection(Object root, Object source, String label) {
		print(getUniqueString(root) + " -> " + getUniqueString(source) + " [label=" + label + "];");
	}

	void printExpressionNode(Expression expression) {
		String displayedName = expression.getClass().getSimpleName();
		if (expression instanceof Binary) {
			displayedName = ((Binary) expression).getOperator().toString();
		}
		print(getUniqueString(expression) + " [label=\"" + displayedName + "\",shape=box];");
	}

	private String getUniqueString(Object object) {
		return Integer.toString(java.lang.System.identityHashCode(object));
	}
}
