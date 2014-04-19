package parser.semanticAnalysis.abstractSyntaxTree.expressions.locations;

import parser.semanticAnalysis.abstractSyntaxTree.expressions.Expression;
import parser.semanticAnalysis.symbolTable.declarations.types.InvalidType;

/**
 * Represents an invalid location.
 */
public class InvalidLocation extends Location {

	/**
	 * Constructs an expression.
	 */
	public InvalidLocation() {
		super(new InvalidType());
	}

	public String toString() {
		return "InvalidLocation";
	}

	/**
	 * Clones the expression.
	 */
	@SuppressWarnings("CloneDoesntCallSuperClone")
	@Override
	public Expression clone() {
		return new InvalidLocation();
	}
}
