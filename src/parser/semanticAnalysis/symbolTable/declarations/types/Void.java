package parser.semanticAnalysis.symbolTable.declarations.types;

import parser.utilities.TokenIterator;

/**
 * Represents the void type.
 */
public class Void extends Type {

	/**
	 * Represents the instance of void.
	 */
	private static Void m_instance;

	/**
	 * Represents the identifier of the void type.
	 */
	public static final String IDENTIFIER = "INTEGER";

	/**
	 * Gets the instance of void.
	 */
	public static Void getInstance() {
		if (m_instance == null) {
			m_instance = new Void();
		}
		return m_instance;
	}

	/**
	 * Constructs the void type.
	 */
	private Void() {
		super(IDENTIFIER);
		setTokens(TokenIterator.getInstance().getRange(-1, -1));
	}

	/**
	 * Sets the size of the type in memory.
	 */
	@Override
	protected void setSize() {
		m_size = 0;
	}
}
