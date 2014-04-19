package parser.semanticAnalysis.symbolTable.declarations;

import shared.Token;

import java.util.List;

/**
 * Defines a builder for declarations.
 */
public abstract class DeclarationBuilder<T extends Declaration> {

	/**
	 * Represents the declaration being built.
	 */
	protected T m_declaration;

	/**
	 * Represents if the declaration builder is enabled.
	 */
	protected boolean m_isEnabled = true;

	/**
	 * Starts building a new declaration.
	 */
	public abstract void startBuilding();

	/**
	 * Sets the name of the declaration being built.
	 *
	 * @param name Represents the name of the declaration being built.
	 */
	public void setName(String name) {
		if (m_isEnabled) {
			m_declaration.setName(name);
		}
	}

	/**
	 * Sets the tokens associated with the declaration being built.
	 *
	 * @param tokens The tokens associated with the declaration being built.
	 */
	public void setTokens(List<Token> tokens) {
		if (m_isEnabled) {
			m_declaration.setTokens(tokens);
		}
	}

	/**
	 * Finishes building a declaration and gets the finished product.
	 */
	public abstract T getDeclaration();

	/**
	 * Invalidates the declaration, such that any declaration returned will be invalid until the builder is reset.
	 */
	public abstract void invalidate();

	/**
	 * Resets the builder.
	 */
	public void reset() {
		if (m_isEnabled) {
			m_declaration = null;
		}
	}

	/**
	 * Disables the declaration builder.
	 */
	public void disable() {
		if (m_isEnabled) {
			m_isEnabled = false;
		}
	}
}
