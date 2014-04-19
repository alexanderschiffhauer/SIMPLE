package parser.semanticAnalysis.abstractSyntaxTree.conditions.relation;

public enum Relation implements IRelation {
	EQUALITY {
		/** {@inheritDoc} */
		@Override
		public String toString() {
			return "=";
		}

		/**
		 * Gets the negation of the relation.
		 */
		@Override
		public Relation getNegation() {
			return INEQUALITY;
		}
	},
	INEQUALITY {
		/** {@inheritDoc} */
		@Override
		public String toString() {
			return "#";
		}

		/**
		 * Gets the negation of the relation.
		 */
		@Override
		public Relation getNegation() {
			return EQUALITY;
		}
	},
	LESS_THAN {
		/** {@inheritDoc} */
		@Override
		public String toString() {
			return "<";
		}

		/**
		 * Gets the negation of the relation.
		 */
		@Override
		public Relation getNegation() {
			return GREATER_THAN_OR_EQUAL_TO;
		}
	},
	GREATER_THAN {
		/** {@inheritDoc} */
		@Override
		public String toString() {
			return ">";
		}

		/**
		 * Gets the negation of the relation.
		 */
		@Override
		public Relation getNegation() {
			return LESS_THAN_OR_EQUAL_TO;
		}
	},
	LESS_THAN_OR_EQUAL_TO {
		/** {@inheritDoc} */
		@Override
		public String toString() {
			return "<=";
		}

		/**
		 * Gets the negation of the relation.
		 */
		@Override
		public Relation getNegation() {
			return GREATER_THAN;
		}
	},
	GREATER_THAN_OR_EQUAL_TO {
		/** {@inheritDoc} */
		@Override
		public String toString() {
			return ">=";
		}

		/**
		 * Gets the negation of the relation.
		 */
		@Override
		public Relation getNegation() {
			return LESS_THAN;
		}
	};

	/**
	 * Gets a relation from its stringified representation.
	 *
	 * @param string The stringified operator.
	 */
	public static Relation getRelation(String string) {
		for (Relation relation : Relation.values()) {
			if (relation.toString().equals(string)) {
				return relation;
			}
		}
		return null;
	}
}
