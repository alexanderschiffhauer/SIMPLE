package parser.semanticAnalysis.symbolTable.declarations.procedures;

import parser.semanticAnalysis.abstractSyntaxTree.expressions.Expression;
import parser.semanticAnalysis.abstractSyntaxTree.instructions.Instruction;
import parser.semanticAnalysis.symbolTable.declarations.Declaration;
import parser.semanticAnalysis.symbolTable.declarations.types.Type;
import parser.semanticAnalysis.symbolTable.scope.Scope;

import java.util.ArrayList;
import java.util.List;

public class Procedure extends Declaration {

	/**
	 * Represents the type of the procedure.
	 */
	private Type m_returnType = parser.semanticAnalysis.symbolTable.declarations.types.Void.getInstance();
	/**
	 * Represents the parameters of the procedure.
	 */
	private List<Parameter> m_parameters = new ArrayList<Parameter>();
	/**
	 * Represents the local variables (excluding the parameters) of the procedure.
	 */
	private Scope m_localVariables;
	/**
	 * Represents the instructions of the procedure.
	 */
	private List<Instruction> m_instructions = new ArrayList<Instruction>();
	/**
	 * Represents the return expression of the procedure.
	 */
	private Expression m_returnExpression;

	/**
	 * Gets the return expression of the procedure.
	 */
	public Expression getReturnExpression() {
		return m_returnExpression;
	}

	/**
	 * Sets the return returnExpression of the procedure.
	 *
	 * @param returnExpression The return returnExpression.
	 */
	void setReturnExpression(Expression returnExpression) {
		m_returnExpression = returnExpression;
	}

	/**
	 * Gets the instructions of the procedure.
	 */
	public List<Instruction> getInstructions() {
		return m_instructions;
	}

	/**
	 * Sets the instructions of the procedure.
	 *
	 * @param instructions The instructions of the procedure.
	 */
	void setInstructions(List<Instruction> instructions) {
		m_instructions = instructions;
	}

	/**
	 * Gets the local variables of the procedure.
	 */
	public Scope getLocalVariables() {
		return m_localVariables;
	}

	/**
	 * Sets the local variables of the procedure.
	 *
	 * @param localVariables The local variables of the procedure.
	 */
	void setLocalVariables(Scope localVariables) {
		m_localVariables = localVariables;
	}

	/**
	 * Gets the parameters of the procedure.
	 */
	public List<Parameter> getParameters() {
		return m_parameters;
	}

	/**
	 * Sets the parameters of the procedure.
	 *
	 * @param parameters The parameters of the procedure.
	 */
	void setParameters(List<Parameter> parameters) {
		m_parameters = parameters;
	}

	/**
	 * Gets the type of this procedure.
	 */
	public Type getReturnType() {
		return m_returnType;
	}

	/**
	 * Sets the returnType of this procedure
	 *
	 * @param returnType The returnType of this procedure.
	 */
	void setReturnType(Type returnType) {
		m_returnType = returnType;
	}
}
