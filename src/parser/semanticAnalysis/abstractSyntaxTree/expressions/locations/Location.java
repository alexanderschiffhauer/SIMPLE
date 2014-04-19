package parser.semanticAnalysis.abstractSyntaxTree.expressions.locations;

import parser.semanticAnalysis.abstractSyntaxTree.expressions.Expression;
import parser.semanticAnalysis.symbolTable.declarations.types.Type;

public abstract class Location extends Expression {

	/**
	 * Constructs an expression.
	 *
	 * @param type The type of the expression.
	 */
	public Location(Type type) {
		super(type);
	}
}
