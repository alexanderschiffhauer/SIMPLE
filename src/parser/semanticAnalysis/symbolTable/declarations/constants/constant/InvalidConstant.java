package parser.semanticAnalysis.symbolTable.declarations.constants.constant;

import parser.semanticAnalysis.symbolTable.declarations.InvalidDeclaration;

/**
 * Represents an invalid constant (i.e. a constant that was built improperly).
 */
public class InvalidConstant extends Constant {

	/**
	 * Constructs a new invalid constant.
	 */
	public InvalidConstant() {
		InvalidDeclaration.initializeInvalidDeclaration(this);
		setValue(Integer.MAX_VALUE);
	}

	/**
	 * Constructs a new invalid constant.
	 *
	 * @param constant The constant from which to retrieve information.
	 */
	public InvalidConstant(Constant constant) {
		this();
		InvalidDeclaration.initializeInvalidDeclaration(this, constant);
		if (constant.getValue() != Integer.MAX_VALUE) {
			setValue(constant.getValue());
		}
	}
}
