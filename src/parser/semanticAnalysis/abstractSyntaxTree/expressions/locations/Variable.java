package parser.semanticAnalysis.abstractSyntaxTree.expressions.locations;

import parser.semanticAnalysis.abstractSyntaxTree.expressions.Expression;
import parser.semanticAnalysis.symbolTable.declarations.types.Type;

public class Variable extends Location {

	private parser.semanticAnalysis.symbolTable.declarations.variable.Variable m_variable;

	public Variable(parser.semanticAnalysis.symbolTable.declarations.variable.Variable variable, Type type) {
		super(type);
		m_variable = variable;
	}

	public parser.semanticAnalysis.symbolTable.declarations.variable.Variable getVariable() {
		return m_variable;
	}

	public String toString() {
		return m_variable.getName();
	}

	/**
	 * Clones the expression.
	 */
	@SuppressWarnings("CloneDoesntCallSuperClone")
	@Override
	public Expression clone() {
		return new Variable(m_variable, getType());
	}
}
