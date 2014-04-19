package parser.semanticAnalysis.symbolTable.declarations.types;

import parser.utilities.TokenIterator;

/**
 * Represents the integer type.
 */
public class Integer extends Type {

	/**
	 * Represents the instance of the integer.
	 */
	private static Integer m_instance;

	/**
	 * Represents the identifier of the integer.
	 */
	public static final String IDENTIFIER = "INTEGER";

	/**
	 * Constructs the instance of the integer.
	 */
	private Integer() {
		super(IDENTIFIER);
		setTokens(TokenIterator.getInstance().getRange(-1, -1));
	}

	/**
	 * Sets the size of the type in memory.
	 */
	@Override
	protected void setSize() {
		m_size = 8;
	}

	/**
	 * Gets the instance of the integer.
	 */
	public static Integer getInstance() {
		if (m_instance == null) {
			m_instance = new Integer();
		}
		return m_instance;
	}
}
