package parser.semanticAnalysis.abstractSyntaxTree.expressions;

import parser.semanticAnalysis.abstractSyntaxTree.instructions.Call;

/**
 * Represents a procedure that returns a value.
 */
public class Function extends Expression {

	/**
	 * Gets the call this function encapsulates.
	 */
	public Call getCall() {
		return m_call;
	}

	/**
	 * Represents the call this function encapsulates.
	 */
	private Call m_call;

	/**
	 * Constructs a new function.
	 *
	 * @param call The call the function will encapsulate.
	 */
	public Function(Call call) {
		super(parser.semanticAnalysis.symbolTable.declarations.types.Integer.getInstance());
		m_call = call;
	}

	/**
	 * Clones the expression.
	 */
	@Override
	public Expression clone() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}
}
