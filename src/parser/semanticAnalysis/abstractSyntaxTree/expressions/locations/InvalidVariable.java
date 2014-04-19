package parser.semanticAnalysis.abstractSyntaxTree.expressions.locations;

import parser.semanticAnalysis.symbolTable.declarations.types.InvalidType;
import parser.semanticAnalysis.symbolTable.declarations.types.Type;

/**
 * Represents an invalid variable (i.e. a variable that was built because something went wrong).
 */
public class InvalidVariable extends Variable {

	/**
	 * Constructs a new invalid variable.
	 */
	public InvalidVariable() {
		this(new parser.semanticAnalysis.symbolTable.declarations.variable.InvalidVariable(), new InvalidType());
	}

	/**
	 * Constructs a new invalid variable.
	 *
	 * @param variable The invalid variable's variable.
	 * @param type     The invalid variable's type.
	 */
	InvalidVariable(parser.semanticAnalysis.symbolTable.declarations.variable.Variable variable, Type type) {
		super(variable, type);
	}
}
