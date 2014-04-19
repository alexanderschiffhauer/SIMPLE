package parser.semanticAnalysis.abstractSyntaxTree.expressions;

public enum Operator {
	PLUS {
		/** {@inheritDoc} */
		@Override
		public String toString() {
			return "+";
		}
	}, MINUS {
		/** {@inheritDoc} */
		@Override
		public String toString() {
			return "-";
		}
	}, MULTIPLICATION {
		/** {@inheritDoc} */
		@Override
		public String toString() {
			return "*";
		}
	}, DIV {
		/** {@inheritDoc} */
		@Override
		public String toString() {
			return "DIV";
		}
	}, MOD {
		/** {@inheritDoc} */
		@Override
		public String toString() {
			return "MOD";
		}
	};

	/**
	 * Gets an operator from its stringified representation.
	 *
	 * @param string The stringified operator.
	 */
	public static Operator getOperator(String string) {
		if (string.equals("%")) {
			return MOD;
		} else if (string.equals("/")) {
			return DIV;
		}
		for (Operator operator : Operator.values()) {
			if (operator.toString().equals(string)) {
				return operator;
			}
		}
		return null;
	}

	/**
	 * Gets an interpreter-friendly stringified representation of an operator.
	 *
	 * @param operator The operator.
	 */
	public static String getOperator(Operator operator) {
		if (operator == MOD) {
			return "%";
		} else if (operator == DIV) {
			return "/";
		} else {
			return operator.toString();
		}
	}
}
