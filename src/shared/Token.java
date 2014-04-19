package shared;

/**
 * Represents a semantic unit for the SIMPLE compiler.
 */
@SuppressWarnings("all")
public class Token<V> {

	/**
	 * Represents the value of this token.
	 */
	private V m_value;
	/**
	 * Represents the type of this token.
	 */
	private Type m_type;
	/**
	 * The index of the token.
	 */
	private int m_index;
	/**
	 * The length of the token.
	 */
	private int m_length;

	/**
	 * Constructs a token.
	 *
	 * @param index  The index of the token.
	 * @param length The length of the token.
	 * @param type   The type of the token.
	 * @param value  The value of the token.
	 */
	public Token(int index, int length, Type type, V value) {
		assert index >= 0 && length >= 0;
		m_index = index;
		m_length = length;
		m_type = type;
		m_value = value;
	}

	/**
	 * Constructs a token.
	 */
	// Package-level scope allows InvalidToken to create a fake token.
	Token() {}

	/**
	 * Gets the value of this token.
	 */
	public V getValue() {
		return m_value;
	}

	/**
	 * Gets the type of this token.
	 */
	public Type getType() {
		return m_type;
	}

	/**
	 * Gets the starting index of the token.
	 */
	public int getStartingIndex() {
		return m_index;
	}

	/**
	 * Gets the ending index of the token.
	 */
	public int getEndingIndex() {
		return m_index + m_length - 1;
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		if (getType() == Type.KEYWORD || getType() == Type.SYMBOL) {
			return getValue().toString() + "@" + getPosition();
		} else if (getType() == Type.IDENTIFIER || getType() == Type.INTEGER) {
			return getType().toString() + "<" + getValue().toString() + ">" + "@(" + getPosition();
		} else {
			return getType().toString() + "@" + getPosition();
		}
	}

	/**
	 * Gets the position of the token.
	 */
	public String getPosition() {
		return "(" + getStartingIndex() + ", " + getEndingIndex() + ")";
	}

	/**
	 * Represents the types of tokens.
	 */
	public enum Type {
		INTEGER {
			/** {@inheritDoc} */
			public String toString() {
				return "integer";
			}
		},
		KEYWORD {
			/** {@inheritDoc} */
			public String toString() {
				return "keyword";
			}
		},
		SYMBOL {
			/** {@inheritDoc} */
			public String toString() {
				return "symbol";
			}
		},
		IDENTIFIER {
			/** {@inheritDoc} */
			public String toString() {
				return "identifier";
			}
		},
		EOF {
			/** {@inheritDoc} */
			public String toString() {
				return "eof";
			}
		},
		INVALID {
			/** {@inheritDoc} */
			public String toString() {
				return "invalid";
			}
		}
	}
}
