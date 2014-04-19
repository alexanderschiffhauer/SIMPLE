package shared;

/**
 * Encapsulates SIMPLE's reserved keywords and symbols.
 */
@SuppressWarnings("all")
public class Reserved {


	/**
	 * Queries if the string is a reserved keyword.
	 *
	 * @param string The string to query.
	 */
	public static boolean isKeyword(String string) {
		return getKeyword(string) != null;
	}

	/**
	 * Gets the reserved keyword from a string if it is a reserved keyword; otherwise, null.
	 *
	 * @param string The reserved keyword's string-based representation.
	 */
	public static Keyword getKeyword(String string) {
		assert string.length() > 0;
		for (Reserved.Keyword keyword : Reserved.Keyword.values()) {
			if (keyword.toString().equals(string)) {
				return keyword;
			}
		}
		return null;
	}

	/**
	 * Queries if the string is a reserved symbol.
	 *
	 * @param string The string to query.
	 */
	public static boolean isSymbol(String string) {
		return getSymbol(string) != null;
	}

	/**
	 * Gets the reserved symbol from a string if it is a reserved symbol; otherwise, null.
	 *
	 * @param string The reserved symbol's string-based representation.
	 */
	public static Symbol getSymbol(String string) {
		assert string.length() == 1 || string.length() == 2;
		for (Reserved.Symbol symbol : Reserved.Symbol.values()) {
			if (symbol.toString().equals(string)) {
				return symbol;
			}
		}
		return null;
	}

	/**
	 * Defines SIMPLE's reserved keywords.
	 */
	public enum Keyword {
		PROGRAM {
			/** {@inheritDoc} */
			public String toString() {
				return "PROGRAM";
			}
		},
		BEGIN {
			/** {@inheritDoc} */
			public String toString() {
				return "BEGIN";
			}
		},
		END {
			/** {@inheritDoc} */
			public String toString() {
				return "END";
			}
		},
		CONST {
			/** {@inheritDoc} */
			public String toString() {
				return "CONST";
			}
		},
		TYPE {
			/** {@inheritDoc} */
			public String toString() {
				return "TYPE";
			}
		}, PROCEDURE {
			/** {@inheritDoc} */
			public String toString() {
				return "PROCEDURE";
			}
		},
		VAR {
			/** {@inheritDoc} */
			public String toString() {
				return "VAR";
			}
		},
		ARRAY {
			/** {@inheritDoc} */
			public String toString() {
				return "ARRAY";
			}
		},
		OF {
			/** {@inheritDoc} */
			public String toString() {
				return "OF";
			}
		},
		RECORD {
			/** {@inheritDoc} */
			public String toString() {
				return "RECORD";
			}
		},
		DIV {
			/** {@inheritDoc} */
			public String toString() {
				return "DIV";
			}
		},
		MOD {
			/** {@inheritDoc} */
			public String toString() {
				return "MOD";
			}
		},
		IF {
			/** {@inheritDoc} */
			public String toString() {
				return "IF";
			}
		},
		THEN {
			/** {@inheritDoc} */
			public String toString() {
				return "THEN";
			}
		},
		ELSE {
			/** {@inheritDoc} */
			public String toString() {
				return "ELSE";
			}
		},
		REPEAT {
			/** {@inheritDoc} */
			public String toString() {
				return "REPEAT";
			}
		},
		UNTIL {
			/** {@inheritDoc} */
			public String toString() {
				return "UNTIL";
			}
		},
		WHILE {
			/** {@inheritDoc} */
			public String toString() {
				return "WHILE";
			}
		},
		DO {
			/** {@inheritDoc} */
			public String toString() {
				return "DO";
			}
		},
		WRITE {
			/** {@inheritDoc} */
			public String toString() {
				return "WRITE";
			}
		},
		READ {
			/** {@inheritDoc} */
			public String toString() {
				return "READ";
			}
		},
		RETURN {
			@Override
			public String toString() {
				return "RETURN";
			}
		}
	}


	/**
	 * Defines SIMPLE's reserved symbols.
	 */
	public enum Symbol {
		SEMICOLON {
			/** {@inheritDoc} */
			public String toString() {
				return ";";
			}
		},
		EQUALS {
			/** {@inheritDoc} */
			public String toString() {
				return "=";
			}
		},
		COLON {
			/** {@inheritDoc} */
			public String toString() {
				return ":";
			}
		},
		PLUS {
			/** {@inheritDoc} */
			public String toString() {
				return "+";
			}
		},
		ASTERISK {
			/** {@inheritDoc} */
			public String toString() {
				return "*";
			}
		},
		MINUS {
			/** {@inheritDoc} */
			public String toString() {
				return "-";
			}
		},
		OPENING_PARENTHESIS {
			/** {@inheritDoc} */
			public String toString() {
				return "(";
			}
		},
		CLOSING_PARENTHESIS {
			/** {@inheritDoc} */
			public String toString() {
				return ")";
			}
		},
		POUND {
			/** {@inheritDoc} */
			public String toString() {
				return "#";
			}
		},
		LESS_THAN {
			/** {@inheritDoc} */
			public String toString() {
				return "<";
			}
		},
		GREATER_THAN {
			/** {@inheritDoc} */
			public String toString() {
				return ">";
			}
		},
		OPENING_BRACKET {
			/** {@inheritDoc} */
			public String toString() {
				return "[";
			}
		},
		CLOSING_BRACKET {
			/** {@inheritDoc} */
			public String toString() {
				return "]";
			}
		},
		PERIOD {
			/** {@inheritDoc} */
			public String toString() {
				return ".";
			}
		},
		COMMA {
			/** {@inheritDoc} */
			public String toString() {
				return ",";
			}
		},
		COLON_EQUALS {
			/** {@inheritDoc} */
			public String toString() {
				return ":=";
			}
		},
		LESS_THAN_OR_EQUAL_TO {
			/** {@inheritDoc} */
			public String toString() {
				return "<=";
			}
		},
		GREATER_THAN_OR_EQUAL_TO {
			/** {@inheritDoc} */
			public String toString() {
				return ">=";
			}
		},
		OPENING_COMMENTS {
			/** {@inheritDoc} */
			public String toString() {
				return "(*";
			}
		},
		CLOSING_COMMENTS {
			/** {@inheritDoc} */
			public String toString() {
				return "*)";
			}
		}
	}
}
