package parser.semanticAnalysis.symbolTable.declarations.procedures;

import parser.semanticAnalysis.symbolTable.declarations.types.Type;

/**
 * Represents the parameters of a procedure.
 */
public class Parameter extends LocalVariable {

	/**
	 * Constructs a new parameter.
	 *
	 * @param name The name of the parameter.
	 * @param type The type of the parameter.
	 */
	public Parameter(String name, Type type) {
		setName(name);
		setType(type);
	}
}
