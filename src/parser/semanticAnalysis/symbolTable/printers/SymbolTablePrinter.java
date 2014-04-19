package parser.semanticAnalysis.symbolTable.printers;

import parser.semanticAnalysis.symbolTable.declarations.Declaration;
import parser.semanticAnalysis.symbolTable.declarations.constants.constant.Constant;
import parser.semanticAnalysis.symbolTable.declarations.types.Integer;
import parser.semanticAnalysis.symbolTable.declarations.types.Type;
import parser.semanticAnalysis.symbolTable.declarations.types.array.Array;
import parser.semanticAnalysis.symbolTable.declarations.types.record.Record;
import parser.semanticAnalysis.symbolTable.declarations.variable.Variable;
import parser.semanticAnalysis.symbolTable.scope.Scope;
import parser.semanticAnalysis.symbolTable.scope.ScopeManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Prints symbol tables.
 */
public class SymbolTablePrinter implements ISymbolTablePrinter {

	/**
	 * Represents the instance of symbol table printer.
	 */
	private static SymbolTablePrinter m_instance;
	/**
	 * Represents the spaces to add in front of each line.
	 */
	private String m_spaces;

	/**
	 * Constructs the symbol table printer.
	 */
	private SymbolTablePrinter() {
		m_spaces = "";
	}

	/**
	 * Gets the instance of the symbol table printer.
	 */
	public static SymbolTablePrinter getInstance() {
		if (m_instance == null) {
			m_instance = new SymbolTablePrinter();
		}
		return m_instance;
	}

	/**
	 * Prints all symbol tables that have been created.
	 */
	public void printSymbolTables() {
		printScope(ScopeManager.getInstance().getCurrentScope());
	}

	/**
	 * Prints the scope.
	 */
	private void printScope(Scope scope) {
		Map<String, Declaration> map = scope.getMap();
		List<String> names = new ArrayList<String>(map.keySet());
		Collections.sort(names);
		print("SCOPE BEGIN");
		incrementSpaces();
		for (String name : names) {
			Declaration declaration = map.get(name);
			if (declaration instanceof Constant) {
				printConstant(name, (Constant) declaration);
			} else if (declaration instanceof Variable) {
				printVariable(name, (Variable) declaration);
			} else if (declaration instanceof Type) {
				printType(name, (Type) declaration);
			}
		}
		decrementSpaces();
		print("END SCOPE");
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
	 * Prints a type.
	 *
	 * @param type The type to print.
	 */
	private void printType(Type type) {
		if (type instanceof Array) {
			printArray((Array) type);
		} else if (type instanceof Record) {
			printRecord((Record) type);
		}
	}

	/**
	 * Prints a constant.
	 *
	 * @param name     The name of the array.
	 * @param constant The constant to print.
	 */
	private void printConstant(String name, Constant constant) {
		print(name + " =>");
		incrementSpaces();
		printConstant(constant);
		decrementSpaces();
	}

	/**
	 * Prints a constant.
	 *
	 * @param constant The constant to print.
	 */
	private void printConstant(Constant constant) {
		print("CONST BEGIN");
		incrementSpaces();
		print("type:");
		incrementSpaces();
		print(Integer.getInstance().getName());
		decrementSpaces();
		print("value:");
		incrementSpaces();
		print(String.valueOf((java.lang.Integer) constant.getValue()));
		decrementSpaces();
		decrementSpaces();
		print("END CONST");
	}

	/**
	 * Prints an array.
	 *
	 * @param name  The name of the array.
	 * @param array The array to print.
	 */
	private void printArray(String name, Array array) {
		print(name + " =>");
		incrementSpaces();
		printArray(array);
		decrementSpaces();
	}

	/**
	 * Prints an array.
	 *
	 * @param array The array to print.
	 */
	private void printArray(Array array) {
		print("ARRAY BEGIN");
		incrementSpaces();
		print("type:");
		incrementSpaces();
		printType(array.getElementType());
		decrementSpaces();
		print("length:");
		incrementSpaces();
		print(String.valueOf(array.getLength()));
		decrementSpaces();
		decrementSpaces();
		print("END ARRAY");
	}

	/**
	 * Prints a record.
	 *
	 * @param name   The name of the array.
	 * @param record The record to print.
	 */
	private void printRecord(String name, Record record) {
		print(name + " =>");
		incrementSpaces();
		printRecord(record);
		decrementSpaces();
	}

	/**
	 * Prints a record.
	 *
	 * @param record The record to print.
	 */
	private void printRecord(Record record) {
		print("RECORD BEGIN");
		incrementSpaces();
		printScope(record.getScope());
		decrementSpaces();
		print("END RECORD");
	}

	/**
	 * Prints a variable.
	 *
	 * @param name     The name of the variable.
	 * @param variable The variable to print.
	 */
	private void printVariable(String name, Variable variable) {
		print(name + " =>");
		incrementSpaces();
		print("VAR BEGIN");
		incrementSpaces();
		print("type:");
		incrementSpaces();
		printType(variable.getType());
		decrementSpaces();
		decrementSpaces();
		print("END VAR");
		decrementSpaces();
	}

	/**
	 * Prints a string with the proper amount of indentation.
	 *
	 * @param string The string to print.
	 */
	private void print(String string) {
		System.out.println(m_spaces + string);
	}

	/**
	 * Prints a type recursively.
	 *
	 * @param type The type to print recursively.
	 *
	private void printTypeRecursively(Type type) {
	if (type instanceof Identifier) {
	Identifier identifier = (Identifier) type;
	if (identifier.getReturnType() instanceof Array) {
	printArray((Array) identifier.getReturnType());
	} else if (identifier.getReturnType() instanceof Record) {
	printRecord((Record) identifier.getReturnType());
	} else if (identifier.getReturnType() instanceof Identifier) {
	printTypeRecursively((Identifier) identifier.getReturnType());
	} else {
	print(type.getName());
	}
	} else if (type.getName() == null) {
	printType(type);
	} else {
	print(type.getName());
	}
	}*/

	/**
	 * Decreases the amount of spaces in each line by two.
	 */
	private void decrementSpaces() {
		m_spaces = m_spaces.substring(0, m_spaces.length() - 2);
	}

	/**
	 * Increases the amount of spaces in each line by two.
	 */
	private void incrementSpaces() {
		m_spaces += "  ";
	}
}
