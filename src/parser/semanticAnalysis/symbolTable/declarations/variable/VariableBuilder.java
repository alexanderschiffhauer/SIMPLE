package parser.semanticAnalysis.symbolTable.declarations.variable;

import parser.semanticAnalysis.symbolTable.declarations.DeclarationBuilder;
import parser.semanticAnalysis.symbolTable.declarations.types.Type;
import parser.semanticAnalysis.symbolTable.scope.ScopeManager;
import shared.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds variables.
 */
public class VariableBuilder extends DeclarationBuilder<Variable> {

	/**
	 * Represents the instance of the variable builder
	 */
	private static VariableBuilder m_instance;


	/**
	 * Constructs the variable builder
	 */
	private VariableBuilder() {
	}

	/**
	 * Gets the instance of the variable builder
	 */
	public static VariableBuilder getInstance() {
		if (m_instance == null) {
			m_instance = new VariableBuilder();
		}
		return m_instance;
	}

	/**
	 * Starts building a new variable.
	 */
	@Override
	public void startBuilding() {
		if (m_isEnabled) {
			m_declaration = new Variable();
		}
	}

	/**
	 * Finishes building a declaration and gets the finished product.
	 */
	@Override
	public Variable getDeclaration() {
		if (m_isEnabled) {
			return m_declaration;
		}
		return new InvalidVariable();
	}

	/**
	 * Invalidates the declaration, such that any declaration returned will be invalid until the builder is reset.
	 */
	@Override
	public void invalidate() {
		if (m_isEnabled) {
			m_declaration = new InvalidVariable(m_declaration);
		}
	}

	/**
	 * Sets the type of the variable being built.
	 *
	 * @param type The type of the variable being built.
	 */
	public void setType(Type type) {
		if (m_isEnabled) {
			m_declaration.setType(type);
		}
	}


	/**
	 * Inserts a list of variables associated with a declaration into the current scope.
	 *
	 * @param tokens The list of variables to insert into the current scope.
	 * @param type   The type to insert into the current scope.
	 */
	public void insertVariablesIntoSymbolTable(List<Token> tokens, Type type) {
		if (m_isEnabled) {
			for (Token token : tokens) {
				startBuilding();
				List<Token> tokensToSet = new ArrayList<Token>();
				tokensToSet.add(token);
				setTokens(tokensToSet);
				setName(token.getValue().toString());
				setType(type);
				ScopeManager.getInstance().getCurrentScope().insert(token.getValue().toString(), getDeclaration());
				reset();
			}
		}
	}
}
