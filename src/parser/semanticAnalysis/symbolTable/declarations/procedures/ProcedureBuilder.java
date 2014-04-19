package parser.semanticAnalysis.symbolTable.declarations.procedures;

import parser.semanticAnalysis.SemanticValidator;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.Expression;
import parser.semanticAnalysis.abstractSyntaxTree.instructions.Instruction;
import parser.semanticAnalysis.symbolTable.declarations.DeclarationBuilder;
import parser.semanticAnalysis.symbolTable.declarations.types.Type;
import parser.semanticAnalysis.symbolTable.scope.Scope;
import parser.semanticAnalysis.symbolTable.scope.ScopeManager;
import parser.utilities.Tuple;
import shared.Token;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Stack;

/**
 * Builds procedures.
 */
public class ProcedureBuilder extends DeclarationBuilder<Procedure> {

	/**
	 * Represents the instance of ProcedureBuilder.
	 */
	private static ProcedureBuilder m_instance;
	/**
	 * Represents the scope of the procedure being built.
	 */
	private Scope m_scope;

	/**
	 * Gets the promised (i.e. the used, but not yet declared) procedures.
	 */
	public List<Procedure> getPromises() {
		return m_promises;
	}

	/**
	 * Represents the procedures that have been forward used (i.e. used, but not declared).
	 */
	private List<Procedure> m_promises;

	public List<Tuple<Procedure, List<Argument>>> getCallPromises() {
		return m_callPromises;
	}

	public boolean containsPromise(String procedureName) {
		for (Procedure procedure : m_promises) {
			if (procedureName.equals(procedure.getName())) {
				return true;
			}
		}
		return false;
	}

	public Procedure getPromise(String procedureName) {
		for (Procedure procedure : m_promises) {
			if (procedureName.equals(procedure.getName())) {
				return procedure;
			}
		}
		return null;
	}

	public void swap(Procedure procedure) {
		m_declaration = procedure;
		m_declaration.setLocalVariables(m_scope);
	}

	public void finalizeSwap(Procedure procedure) {
		m_promises.remove(procedure);
		for (Tuple<Procedure, List<Argument>> tuple : getCallPromises()) {
			if (tuple.x == procedure) {
				SemanticValidator.getInstance().validateProcedureArguments(tuple.x, tuple.y);
			}
		}
	}

	private Stack<Tuple<Procedure, Scope>> m_stack;

	/**
	 * Represents the procedures that have been forward used (i.e. used, but not declared).
	 */
	private List<Tuple<Procedure, List<Argument>>> m_callPromises = new ArrayList<Tuple<Procedure, List<Argument>>>();

	/**
	 * Gets the instance of ProcedureBuilder.
	 */
	public static ProcedureBuilder getInstance() {
		if (m_instance == null) {
			m_instance = new ProcedureBuilder();
		}
		return m_instance;
	}

	/**
	 * Constructs the ProcedureBuilder.
	 */
	private ProcedureBuilder() {
		m_promises = new ArrayList<Procedure>();
		m_stack = new Stack<Tuple<Procedure, Scope>>();
	}

	/**
	 * Starts building a new declaration.
	 */
	@Override
	public void startBuilding() {
		if (m_declaration != null && m_scope != null) {
			m_stack.push(new Tuple<Procedure, Scope>(m_declaration, m_scope));
		}
		m_scope = ScopeManager.getInstance().getForwardScope();
		m_declaration = new Procedure();
		m_declaration.setLocalVariables(m_scope);
	}

	/**
	 * Finishes building a declaration and gets the finished product.
	 */
	@Override
	public Procedure getDeclaration() {
		ScopeManager.getInstance().removeOuterScope();
		return m_declaration;
	}

	/**
	 * Peeks at the non-yet finished declaration.
	 */
	public Procedure peekDeclaration() {
		return m_declaration;
	}

	/**
	 * Sets the parameters of the procedure being built.
	 *
	 * @param parameters The parameters of the procedure being built.
	 */
	public void setParameters(LinkedHashMap<List<Token>, Type> parameters) {
		if (m_isEnabled) {
			List<Parameter> parameters1 = new ArrayList<Parameter>();
			for (List<Token> tokens : parameters.keySet()) {
				for (Token parameter : tokens) {
					Parameter parameter1 = new Parameter(parameter.getValue().toString(), parameters.get(tokens));
					List<Token> tokens1 = new ArrayList<Token>();
					tokens1.add(parameter);
					parameter1.setTokens(tokens1);
					parameters1.add(parameter1);

					m_scope.insert(parameter1.getName(), parameter1);
				}
			}
			m_declaration.setParameters(parameters1);
		}
	}

	/**
	 * Sets the return type of the procedure being built.
	 *
	 * @param returnType The return type of the procedure being built.
	 */
	public void setReturnType(Type returnType) {
		if (m_isEnabled) {
			m_declaration.setReturnType(returnType);
		}
	}

	/**
	 * Sets the local variables of the procedure being built.
	 *
	 * @param localVariables The local variables of the procedure being built.
	 */
	public void setLocalVariables(LinkedHashMap<List<Token>, Type> localVariables) {
		if (m_isEnabled) {
			for (List<Token> tokens : localVariables.keySet()) {
				for (Token localVariable : tokens) {
					LocalVariable localVariable1 = new LocalVariable();
					List<Token> tokens1 = new ArrayList<Token>();
					tokens1.add(localVariable);
					localVariable1.setTokens(tokens1);
					localVariable1.setName(localVariable.getValue().toString());
					localVariable1.setType(localVariables.get(tokens));

					m_scope.insert(localVariable1.getName(), localVariable1);
				}
			}
		}
	}

	/**
	 * Sets the instructions of the procedure being built.
	 *
	 * @param instructions The instructions of the procedure being built.
	 */
	public void setInstructions(List<Instruction> instructions) {
		if (m_isEnabled) {
			m_declaration.setInstructions(instructions);
		}
	}

	/**
	 * Sets the return expression of the procedure being built.
	 *
	 * @param expression The return expression of the procedure being built.
	 */
	public void setReturnExpression(Expression expression) {
		if (m_isEnabled) {
			m_declaration.setReturnExpression(expression);
		}
	}

	/**
	 * Invalidates the declaration, such that any declaration returned will be invalid until the builder is reset.
	 */
	@Override
	public void invalidate() {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	/**
	 * Resets the builder.
	 */
	@Override
	public void reset() {
		if (m_isEnabled && !m_stack.isEmpty()) {
			Tuple<Procedure, Scope> tuple = m_stack.pop();
			m_declaration = tuple.x;
			m_scope = tuple.y;
		}
	}
}
