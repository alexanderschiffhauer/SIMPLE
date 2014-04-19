package parser.semanticAnalysis.symbolTable.declarations.constants.constant;


import parser.semanticAnalysis.symbolTable.declarations.Declaration;

/**
 * Represents a constant declaration.
 */
public class Constant extends Declaration {

	/**
	 * Represents the value of the constant.
	 */
	protected int m_value;

	/**
	 * Constructs a new constant.
	 */
	public Constant() {}

	/**
	 * Constructs a new constant.
	 *
	 * @param value The value of the constant.
	 */
	public Constant(int value) {
		setValue(value);
	}

	/**
	 * Gets the value of the constant.
	 */
	public int getValue() {
		return m_value;
	}

	/**
	 * Sets the value of the constant.
	 *
	 * @param value The value of the constant.
	 */
	public void setValue(int value) {
		m_value = value;
	}
}
