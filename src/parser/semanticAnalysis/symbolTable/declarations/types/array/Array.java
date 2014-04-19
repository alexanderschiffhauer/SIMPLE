package parser.semanticAnalysis.symbolTable.declarations.types.array;

import parser.semanticAnalysis.symbolTable.declarations.types.Type;

/**
 * Represents an array type.
 */

public class Array extends Type {

	/**
	 * Represents the element type of the array.
	 */
	private Type m_elementType;
	/**
	 * Represents the length of the array.
	 */
	private int m_length;

	/**
	 * Gets the length of the array.
	 */
	public int getLength() {
		return m_length;
	}

	/**
	 * Sets the length of the array.
	 *
	 * @param length The length of the array.
	 */
	void setLength(int length) {
		m_length = length;
		setSize();
	}

	/**
	 * Gets the element type of the array.
	 */
	public Type getElementType() {
		return m_elementType;
	}

	/**
	 * Sets the element type of the array.
	 *
	 * @param elementType The element type of the array.
	 */
	void setElementType(Type elementType) {
		m_elementType = elementType;
		setSize();
	}

	/**
	 * Sets the size of the type in memory.
	 */
	@Override
	protected void setSize() {
		m_size = 0;
		if (m_elementType != null) {
			for (int i = 0; i < m_length; i++) {
				m_size += m_elementType.getSize();
			}
		}
	}
}
