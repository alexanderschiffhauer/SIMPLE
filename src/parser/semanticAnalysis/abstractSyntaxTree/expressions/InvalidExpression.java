package parser.semanticAnalysis.abstractSyntaxTree.expressions;

import parser.semanticAnalysis.symbolTable.declarations.types.InvalidType;

/**
 * Represents an invalid expression.
 */
public class InvalidExpression extends Expression {

	/**
	 * Constructs an expression.
	 */
	public InvalidExpression() {
		super(new InvalidType());
	}

	public String toString() {
		return "InvalidExpression";
	}

	/**
	 * Clones the expression.
	 */
	@SuppressWarnings("CloneDoesntCallSuperClone")
	@Override
	public Expression clone() {
		return new InvalidExpression();
	}
}
