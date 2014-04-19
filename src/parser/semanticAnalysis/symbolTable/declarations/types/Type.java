package parser.semanticAnalysis.symbolTable.declarations.types;

import parser.semanticAnalysis.symbolTable.declarations.Declaration;
import parser.semanticAnalysis.symbolTable.declarations.types.array.Array;

/**
 * Represents a type declaration.
 */
public abstract class Type extends Declaration {

	/**
	 * Represents the size of the type in memory.
	 */
	protected int m_size;

	/**
	 * Constructs a new type.
	 *
	 * @param name The name of the type.
	 */
	public Type(String name) {
		super(name);
		setSize();
	}

	/**
	 * Constructs a new type.
	 */
	public Type() {
		setSize();
	}

	/**
	 * Queries if this type is numeric.
	 */
	public boolean isNumeric() {
		if (this instanceof Integer) {
			return true;
		} else if (this instanceof Array) {
			return ((Array) this).getElementType().isNumeric();
		} else {
			return false;
		}
	}

	/**
	 * Gets the size of the type in memory.
	 */
	public int getSize() {
		return m_size;
	}

	/**
	 * Sets the size of the type in memory.
	 */
	protected abstract void setSize();
}
