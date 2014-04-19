package parser.semanticAnalysis.symbolTable.declarations.variable;

import parser.semanticAnalysis.symbolTable.declarations.Declaration;
import parser.semanticAnalysis.symbolTable.declarations.types.Type;

/**
 * Represents a variable declaration.
 */
public class Variable extends Declaration {

	/**
	 * The offset in memory of the variable.
	 */

	private int m_offset;

	/**
	 * Represents the type of the variable.
	 */
	private Type m_type;

	/**
	 * Sets the type of the variable.
	 *
	 * @param type The type of the variable.
	 */
	public void setType(Type type) {
		m_type = type;
	}

	/**
	 * Gets the type of variable.
	 */
	public Type getType() {
		return m_type;
	}

	/**
	 * Gets the offset of this variable.
	 */
	public int getOffset() {
		return m_offset;
	}


	/**
	 * Sets the offset of the variable.
	 *
	 * @param address The address of the variable.
	 */
	public void setOffset(int address) {
		m_offset = address;
	}
}
