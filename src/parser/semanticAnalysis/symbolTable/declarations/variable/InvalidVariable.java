package parser.semanticAnalysis.symbolTable.declarations.variable;

import parser.semanticAnalysis.symbolTable.declarations.InvalidDeclaration;
import parser.semanticAnalysis.symbolTable.declarations.types.InvalidType;

/**
 * Represents an invalid variable (i.e. a variable that is created because something went wrong).
 */
public class InvalidVariable extends Variable {

	/**
	 * Constructs a new invalid variable.
	 *
	 * @param variable The variable from which to retrieve information.
	 */
	public InvalidVariable(Variable variable) {
		this();
		InvalidDeclaration.initializeInvalidDeclaration(this, variable);
		if (variable.getType() != null) {
			setType(variable.getType());
		}
	}

	/**
	 * Constructs a new invalid variable.
	 */
	public InvalidVariable() {
		InvalidDeclaration.initializeInvalidDeclaration(this);
		setType(new InvalidType());
	}
}
