package exception;

import parser.semanticAnalysis.abstractSyntaxTree.expressions.Expression;
import parser.semanticAnalysis.symbolTable.declarations.Declaration;
import parser.semanticAnalysis.symbolTable.declarations.types.record.Record;
import parser.semanticAnalysis.symbolTable.declarations.variable.Variable;
import shared.Token;

/**
 * Represents the types of exceptions in the SIMPLE compiler.
 */
public enum Exception implements IException {

	NUMBER_OF_ARGS {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			return appendError("An invalid number of arguments was supplied to the SIMPLE compiler.");
		}

		/** {@inheritDoc} */
		@Override
		public String toString() {
			return toString(new Object());
		}
	},
	INVALID_ARGS {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			return appendError("Invalid arguments were supplied to the SIMPLE compiler.");
		}

		/** {@inheritDoc} */
		public String toString() {
			return toString(new Object());
		}
	},
	UNSUPPORTED_OPERATION {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			return appendError("The SIMPLE compiler does not support this functionality yet.");
		}

		/** {@inheritDoc} */
		public String toString() {
			return toString(new Object());
		}
	},
	FILE_NOT_FOUND {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			return appendError("The specified file could not be found.");
		}

		/** {@inheritDoc} */
		public String toString() {
			return toString(new Object());
		}
	},
	NO_INPUT_STREAM {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			return appendError("No input stream.");
		}

		/** {@inheritDoc} */
		public String toString() {
			return toString(new Object());
		}
	},
	INPUT_STREAM_PARTIALLY_SCANNED {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			return appendError("The input stream is already partially scanned.");
		}

		/** {@inheritDoc} */
		public String toString() {
			return toString(new Object());
		}
	},
	IO_ERROR {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			return appendError("The input stream reported an IO error.");
		}

		/** {@inheritDoc} */
		public String toString() {
			return toString(new Object());
		}
	},
	IMPROPERLY_FORMATTED_COMMENT {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			return appendError("A comment is improperly formatted.");
		}

		/** {@inheritDoc} */
		public String toString() {
			return toString(new Object());
		}
	},
	EVENT_DISPATCHING_ERROR {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			return appendError("An event of an improper type was dispatched.");
		}

		/** {@inheritDoc} */
		public String toString() {
			return toString(new Object());
		}
	},
	INVALID_CHARACTER {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			assert objects.length == 2;
			return appendError("Encountered an invalid character \"" + objects[0] + "\" at position " + objects[1] + ".");
		}
	},
	PROGRAM_IDENTIFIERS_DO_NOT_MATCH {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			assert objects.length == 2;
			return appendError("Expected \"" + objects[0] + "\" but encountered \"" + objects[1] + "\".");
		}
	},
	PROCEDURE_IDENTIFIERS_DO_NOT_MATCH {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			assert objects.length == 2;
			return appendError("Expected \"" + objects[0] + "\" but encountered \"" + objects[1] + "\".");
		}
	},
	ENCOUNTERED_TOKEN_DID_NOT_MEET_EXPECTATIONS {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			assert objects.length > 4;
			Object[] requirements = (Object[]) objects[3];
			return appendError("Expected " + getExpectedObjectsExceptionMessage(requirements) + " but encountered \"" + objects[0] + "\" @(" + objects[1] + ", " + objects[2] + ").");
		}
	},
	DECLARATION_NOT_PREVIOUSLY_DECLARED {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			assert objects.length == 1;
			Token token = (Token) objects[0];
			return appendError("The declaration, \"" + token.getValue().toString() + "\", @" + token.getPosition() + " was used, but it was never declared.");
		}
	},
	DECLARATION_NOT_PREVIOUSLY_DECLARED_SIMPLE {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			assert objects.length == 1;
			return appendError("The declaration, \"" + objects[0] + "\" was used, but it was never declared.");
		}
	},
	DECLARATION_PREVIOUSLY_DECLARED {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			assert objects.length == 2;
			Declaration declaration = (Declaration) objects[0];
			Declaration previousDeclaration = (Declaration) objects[1];
			return appendError("The declaration, \"" + declaration.getName() + "\", @" + declaration.getPosition() + " was already declared @" + previousDeclaration.getPosition() + " in this scope.");
		}
	},
	DECLARATION_USED_AS_A_TYPE_BUT_DECLARED_DIFFERENTLY {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			assert objects.length == 4;
			return appendError("The declaration, \"" + objects[0] + "\", @(" + objects[1] + ", " + objects[2] + ") was used as Type but it was declared as " + objects[3] + ".");
		}
	}, AMBIGUOUS_DECLARATION {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			assert objects.length == 3;
			return appendError("Unable to determine the declaration for " + objects[0] + " at (" + objects[1] + ", " + objects[2] + ").");
		}
	}, EXPRESSION_IS_NOT_A_LOCATION {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			assert objects.length == 1;
			Expression expression = (Expression) objects[0];
			return appendError("The expression, \"" + expression.toString() + "\", @" + expression.getType().getPosition() + " is not a location.");
		}
	}, DECLARATION_IS_NOT_AN_ARRAY {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			assert objects.length == 1;
			Declaration declaration = (Declaration) objects[0];
			return appendError("The declaration, \"" + declaration.getName() + "\", @" + declaration.getPosition() + " is not an array.");
		}
	}, VARIABLE_IS_NOT_A_NESTED_ARRAY {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			assert objects.length == 2;
			Variable variable = (Variable) objects[0];
			return appendError("The variable, \"" + variable.getName() + "\", @" + variable.getPosition() + " is not an array with nested-level " + objects[1] + ".");
		}
	}, VARIABLE_IS_NOT_A_RECORD {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			assert objects.length == 1;
			Variable variable = (Variable) objects[0];
			return appendError("The variable, \"" + variable.getName() + "\", @" + variable.getPosition() + " is not a record.");
		}
	}, RECORD_DOES_NOT_CONTAIN_FIELD {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			assert objects.length == 2;
			Record record = (Record) objects[0];
			return appendError("The record, \"" + record.getName() + "\", @" + record.getPosition() + " does not contain the field, \"" + objects[1] + "\".");
		}
	}, EXPRESSION_IS_NOT_A_CONSTANT {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			assert objects.length == 1;
			Expression expression = (Expression) objects[0];
			return appendError("The expression, \"" + expression + "\", @" + expression.getType().getPosition() + " is not a constant.");
		}
	}, EXPRESSION_IS_NOT_A_POSITIVE_CONSTANT {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			assert objects.length == 1;
			Expression expression = (Expression) objects[0];
			return appendError("The expression, \"" + expression + "\", @" + expression.getType().getPosition() + " is not a positive constant.");
		}
	}, EXPRESSION_IS_NOT_NUMERIC {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			assert objects.length == 1;
			Expression expression = (Expression) objects[0];
			return appendError("The expression, \"" + expression + "\", @" + expression.getType().getPosition() + " is not numeric.");
		}
	}, DECLARATION_IS_A_TYPE {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			assert objects.length == 1;
			Declaration declaration = (Declaration) objects[0];
			return appendError("The declaration, \"" + declaration.getName() + "\", @" + declaration.getPosition() + " is a type.");
		}
	}, DECLARATION_IS_NOT_A_TYPE {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			assert objects.length == 1;
			Declaration declaration = (Declaration) objects[0];
			return appendError("The declaration, \"" + declaration.getName() + "\", @" + declaration.getPosition() + " is not a type.");
		}
	}, DECLARATION_IS_NOT_A_VARIABLE {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			assert objects.length == 1;
			Declaration declaration = (Declaration) objects[0];
			return appendError("The declaration, \"" + declaration.getName() + "\", @" + declaration.getPosition() + " is not a variable.");
		}
	}, DECLARATION_IS_NOT_A_VARIABLE_OF_TYPE_INTEGER {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			assert objects.length == 1;
			Declaration declaration = (Declaration) objects[0];
			return appendError("The declaration, \"" + declaration.getName() + "\", @" + declaration.getPosition() + " is not a variable of type Integer.");
		}
	}, EXPRESSION_IS_NOT_A_VARIABLE_OF_TYPE_INTEGER {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			assert objects.length == 1;
			Expression expression = (Expression) objects[0];
			return appendError("The expression, \"" + expression + "\", @" + expression.getType().getPosition() + " is not a variable of type Integer.");
		}
	}, ENCOUNTERED_A_BUG {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			return appendError("There is a bug in the code, and I apologize that you're seeing this error :-D");
		}

		/** {@inheritDoc} */
		public String toString() {
			return toString(new Object());
		}
	}, READ_INSTRUCTION_REQUIRES_AN_INTEGER {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			assert objects.length == 1;
			return appendError("The \"READ\" instruction requires an integer. The input, \"" + objects[0] + "\" is not an integer.");
		}

		/** {@inheritDoc} */
		public String toString() {
			return toString(new Object());
		}
	}, DIVIDE_BY_ZERO {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			assert objects.length == 1;
			return appendError("The expression @" + objects[0] + " divides by zero.");
		}

		/** {@inheritDoc} */
		public String toString() {
			return toString(new Object());
		}
	}, MOD_BY_ZERO {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			assert objects.length == 1;
			return appendError("The expression @" + objects[0] + " mods by zero.");
		}

		/** {@inheritDoc} */
		public String toString() {
			return toString(new Object());
		}
	}, CONSTANT_DIVIDE_BY_ZERO {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			assert objects.length == 1;
			return appendError("The expression " + objects[0] + " divides by zero.");
		}

		/** {@inheritDoc} */
		public String toString() {
			return toString(new Object());
		}
	}, CONSTANT_MOD_BY_ZERO {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			assert objects.length == 1;
			return appendError("The expression " + objects[0] + " mods by zero.");
		}

		/** {@inheritDoc} */
		public String toString() {
			return toString(new Object());
		}
	}, INDEX_OUT_OF_RANGE {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			assert objects.length == 3;
			return appendError("The index, " + objects[0] + " is out of range for the array, \"" + objects[1] + "\" @" + objects[2] + ".");
		}

		/** {@inheritDoc} */
		public String toString() {
			return toString(new Object());
		}
	}, INDEX_OUT_OF_RANGE_COMPILE_TIME {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			assert objects.length == 2;
			return appendError("The index, " + objects[0] + " is out of range for the array @" + objects[1] + ".");
		}

		/** {@inheritDoc} */
		public String toString() {
			return toString(new Object());
		}
	}, NOT_A_PROCEDURE {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			assert objects.length == 2;
			return appendError("The identifier, " + objects[0] + " @" + objects[1] + " is not a procedure.");
		}

		/** {@inheritDoc} */
		public String toString() {
			return toString(new Object());
		}
	}, IDENTIFIER_NOT_AN_INTEGER {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			assert objects.length == 2;
			return appendError("The identifier, " + objects[0] + " @" + objects[1] + " is not an integer.");
		}

		/** {@inheritDoc} */
		public String toString() {
			return toString(new Object());
		}
	}, EXPRESSION_NOT_AN_INTEGER {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			assert objects.length == 2;
			return appendError("The expression, " + objects[0] + " @" + objects[1] + " is not an integer.");
		}

		/** {@inheritDoc} */
		public String toString() {
			return toString(new Object());
		}
	}, PROCEDURE_DOES_NOT_HAVE_RETURN {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			assert objects.length == 2;
			return appendError("The procedure, " + objects[0] + " @" + objects[1] + " specified a return type but failed to include one.");
		}

		/** {@inheritDoc} */
		public String toString() {
			return toString(new Object());
		}
	}, PROCEDURE_DOES_HAVE_RETURN {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			assert objects.length == 2;
			return appendError("The procedure, " + objects[0] + " @" + objects[1] + " specified no return type but includes one.");
		}

		/** {@inheritDoc} */
		public String toString() {
			return toString(new Object());
		}
	}, ARGUMENTS_DO_NOT_MATCH_PROCEDURE {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			assert objects.length == 3;
			return appendError("The arguments, " + objects[0] + ", do not match the procedure, " + objects[1] + "@" + objects[2] + ".");
		}

		/** {@inheritDoc} */
		public String toString() {
			return toString(new Object());
		}
	}, INVALID_ARGUMENT {
		/** {@inheritDoc} */
		public String toString(Object... objects) {
			assert objects.length == 2;
			return appendError("The arguments, " + objects[0] + "@" + objects[2] + " is not a declaration.");
		}

		/** {@inheritDoc} */
		public String toString() {
			return toString(new Object());
		}
	};

	/**
	 * Formats an error message by appending "error: " to the start of the supplied error message.
	 *
	 * @param errorMessage The error message to format.
	 */
	private static String appendError(String errorMessage) {
		return ERROR_PREFIX + errorMessage;
	}

	/**
	 * Gets an exception message of an array of expected objects.
	 *
	 * @param objects The expected objects,
	 */
	private static String getExpectedObjectsExceptionMessage(Object... objects) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < objects.length; i++) {
			stringBuilder.append("\"").append(objects[i].toString()).append("\"");
			if (objects.length > 1 && i < objects.length - 2) {
				stringBuilder.append(", ");
			} else if (objects.length > 1 && i == objects.length - 2) {
				stringBuilder.append(", or ");
			}
		}
		return stringBuilder.toString();
	}

	/**
	 * The prefix before every error.
	 */
	private static final String ERROR_PREFIX = "error: ";
}
