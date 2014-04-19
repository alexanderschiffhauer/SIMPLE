package parser.semanticAnalysis.symbolTable.declarations.types;

import parser.semanticAnalysis.symbolTable.declarations.InvalidDeclaration;

/**
 * Represents an invalid type (i.e. a type that was built because something went wrong).
 */
public class InvalidType extends Type {

	/**
	 * Constructs a new invalid type.
	 */
	public InvalidType() {
		this(InvalidDeclaration.getUniqueName());
	}

	/**
	 * Sets the size of the type in memory.
	 */
	@Override
	protected void setSize() {
		m_size = 0;
	}

	/**
	 * Constructs a new invalid type.
	 *
	 * @param name The name of the declaration.
	 */
	public InvalidType(String name) {
		super(name);
		InvalidDeclaration.initializeInvalidDeclaration(this);
	}
}
