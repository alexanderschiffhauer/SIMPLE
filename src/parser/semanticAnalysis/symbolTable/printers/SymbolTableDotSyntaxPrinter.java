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

import java.util.Collection;
import java.util.Map;

/**
 * Prints the dot syntax for the symbol table.
 */
@SuppressWarnings("all")
public class SymbolTableDotSyntaxPrinter implements ISymbolTablePrinter {

	/**
	 * Represents the instance of symbol table dot syntax parser.
	 */
	private static SymbolTableDotSyntaxPrinter m_instance;
	/**
	 * Represents the dot string being built.
	 */
	private StringBuilder m_stringBuilder;

	/**
	 * Constructs the symbol table dot syntax parser.
	 */
	private SymbolTableDotSyntaxPrinter() {
		m_stringBuilder = new StringBuilder();
	}

	/**
	 * Gets the instance of the symbol table dot syntax parser.
	 */
	public static SymbolTableDotSyntaxPrinter getInstance() {
		if (m_instance == null) {
			m_instance = new SymbolTableDotSyntaxPrinter();
		}
		return m_instance;
	}

	/**
	 * Prints all symbol tables that have been created.
	 */
	@Override
	public void printSymbolTables() {
		appendString("digraph X {");
		appendString(getUniqueString(Integer.getInstance(), Symbol.TERMINAL) + " [label=\"Integer\",shape=box,style=rounded]");
		appendScope(ScopeManager.getInstance().getCurrentScope());
		appendString("}");
		System.out.println(m_stringBuilder.toString());
	}

	/**
	 * Appends a scope to the string builder.
	 *
	 * @param scope The scope to appent.
	 */
	private void appendScope(Scope scope) {
		appendString("subgraph cluster_" + getUniqueString(scope, Symbol.TERMINAL) + " {");
		for (String name : getNames(scope)) {
			Declaration declaration = (Declaration) (scope.getMap().get(name));
			appendString(getUniqueString(declaration, Symbol.DECLARATION) + " [label=\"" + name + "\",shape=box,color=white,fontcolor=black]");
		}
		appendString(getUniqueString(scope, Symbol.DECLARATION) + " [label=\"\",style=invis]");
		appendString("}");
		for (Declaration declaration : getDeclarations(scope)) {
			appendDeclaration(declaration);
		}
	}

	/**
	 * Gets a collection of declarations from a scope.
	 *
	 * @param scope The scope from which to get the collection of declarations.
	 */
	private Collection<Declaration> getDeclarations(Scope scope) {
		return (Collection<Declaration>) ((Map<String, Declaration>) scope.getMap()).values();
	}

	/**
	 * Gets a collection of names from a scope.
	 *
	 * @param scope The scope from which to get the collection of names.
	 */
	private Collection<String> getNames(Scope scope) {
		return (Collection<String>) ((Map<String, Declaration>) scope.getMap()).keySet();
	}

	/**
	 * Appends a declaration to the string builder.
	 *
	 * @param declaration The declaration to append.
	 */
	private void appendDeclaration(Declaration declaration) {
		if (declaration instanceof Constant) {
			appendConstant((Constant) declaration);
		} else if (declaration instanceof Variable) {
			appendVariable((Variable) declaration);
		} else if (declaration instanceof Type) {
			appendType((Type) declaration);
		}
	}

	/**
	 * Appends a type to the string builder.
	 *
	 * @param type The type to append.
	 */
	private void appendType(Type type) {
		if (type instanceof Array) {
			appendArray((Array) type);
			appendString(getUniqueString(type, Symbol.DECLARATION) + " -> " + getUniqueString(type, Symbol.TERMINAL));
		} else if (type instanceof Record) {
			appendRecord((Record) type);
		}
	}


	/**
	 * Appends a constant to the string builder.
	 *
	 * @param constant The constant to append.
	 */
	private void appendConstant(Constant constant) {
		appendString(getUniqueString(constant, Symbol.TERMINAL) + " [label=\"" + String.valueOf(constant.getValue()) + "\",shape=diamond]");
		appendString(getUniqueString(constant, Symbol.DECLARATION) + " -> " + getUniqueString(constant, Symbol.TERMINAL));
		appendString(getUniqueString(constant, Symbol.TERMINAL) + " -> " + getUniqueString(Integer.getInstance(), Symbol.TERMINAL));
	}

	/**
	 * Appends a variable to the string builder.
	 *
	 * @param variable The variable to append.
	 */
	private void appendVariable(Variable variable) {
		appendString(getUniqueString(variable, Symbol.TERMINAL) + " [label=\"\",shape=circle]");
		appendString(getUniqueString(variable, Symbol.DECLARATION) + " -> " + getUniqueString(variable, Symbol.TERMINAL));
		Type type = variable.getType();
		if (variable.getType() instanceof Array && m_stringBuilder.indexOf(getUniqueString(variable.getType(), Symbol.TERMINAL)) == -1) {
			appendArray((Array) variable.getType());
		} else if (variable.getType() instanceof Record && m_stringBuilder.indexOf(getUniqueString(variable.getType(), Symbol.TERMINAL)) == -1) {
			appendRecord((Record) variable.getType());
		}
		appendString(getUniqueString(variable, Symbol.TERMINAL) + " -> " + getUniqueString(type, Symbol.TERMINAL));
	}

	/**
	 * Appends an array to the string builder.
	 *
	 * @param array The array to append.
	 */
	private void appendArray(Array array) {
		appendString(getUniqueString(array, Symbol.TERMINAL) + " [label=\"Array\\nlength: " + array.getLength() + "\",shape=box,style=rounded]");
		if (array.getElementType() instanceof Array && m_stringBuilder.indexOf(getUniqueString(array.getElementType(), Symbol.TERMINAL)) == -1) {
			appendArray((Array) array.getElementType());
			appendString(getUniqueString(array, Symbol.TERMINAL) + " -> " + getUniqueString(array.getElementType(), Symbol.TERMINAL));
			return;
		} else if (array.getElementType() instanceof Record && m_stringBuilder.indexOf(getUniqueString(array.getElementType(), Symbol.TERMINAL)) == -1) {
			appendRecord((Record) array.getElementType());
		}
		appendString(getUniqueString(array, Symbol.TERMINAL) + " -> " + getUniqueString(getType(array), Symbol.TERMINAL));
	}

	/**
	 * Appends a record to the string builder.
	 *
	 * @param record The record to append.
	 */
	private void appendRecord(Record record) {
		appendString(getUniqueString(record, Symbol.TERMINAL) + " [label=\"Record\",shape=box,style=rounded]");
		if (m_stringBuilder.indexOf(getUniqueString(record, Symbol.DECLARATION)) != -1) {
			appendString(getUniqueString(record, Symbol.DECLARATION) + " -> " + getUniqueString(record, Symbol.TERMINAL));
		}
		appendScope(record.getScope());
		appendString(getUniqueString(record, Symbol.TERMINAL) + " -> " + getUniqueString(record.getScope(), Symbol.DECLARATION));
	}

	/**
	 * Appends a string to the string builder.
	 *
	 * @param string The string to append.
	 */
	private void appendString(String string) {
		m_stringBuilder.append(string + "\n");
	}

	/**
	 * Gets a unique string for an object that is valid for use in a DOT program.
	 *
	 * @param object The object for which to get the unique string.
	 * @param symbol The symbol for which to get the unique string.
	 */
	private String getUniqueString(Object object, Symbol symbol) {
		String string = object.toString().replace(".", "").replace("@", "");
		if (symbol == Symbol.DECLARATION) {
			string += "DECLARATION";
		} else if (symbol == Symbol.TERMINAL) {
			string += "TERMINAL";
		}
		return string;
	}

	/**
	 * Gets the type from a declaration.
	 *
	 * @param declaration The declaration from which to get the type.
	 */
	private Type getType(Declaration declaration) {
		if (declaration instanceof Constant) {
			return Integer.getInstance();
		} else if (declaration instanceof Array) {
			return ((Array) declaration).getElementType();
		} else if (declaration instanceof Record) {
			return ((Record) declaration);
		} else if (declaration instanceof Variable) {
			return ((Variable) declaration).getType();
		}
		return null;
	}

	/**
	 * Defines position.
	 */
	private enum Symbol {
		DECLARATION,
		TERMINAL
	}
}
