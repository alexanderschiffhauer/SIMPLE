package parser.semanticAnalysis.abstractSyntaxTree.expressions;

import parser.semanticAnalysis.symbolTable.declarations.constants.constant.Constant;

public class Number extends Expression {

	Constant m_constant;

	public Constant getConstant() {
		return m_constant;
	}

	public Number(Constant constant) {
		super(parser.semanticAnalysis.symbolTable.declarations.types.Integer.getInstance());
		m_constant = constant;
	}

	public String toString() {
		return Integer.toString(m_constant.getValue());
	}

	/**
	 * Clones the expression.
	 */
	@SuppressWarnings("CloneDoesntCallSuperClone")
	@Override
	public Expression clone() {
		return new Number(new Constant(m_constant.getValue()));
	}
}
