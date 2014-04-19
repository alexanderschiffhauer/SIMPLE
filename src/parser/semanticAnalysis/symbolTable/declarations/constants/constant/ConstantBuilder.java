package parser.semanticAnalysis.symbolTable.declarations.constants.constant;

import parser.semanticAnalysis.symbolTable.declarations.DeclarationBuilder;

/**
 * Builds constants.
 */
public class ConstantBuilder extends DeclarationBuilder<Constant> {

	/**
	 * Represents the instance of the constant builder.
	 */
	private static ConstantBuilder m_instance;

	/**
	 * Constructs the constant builder.
	 */
	protected ConstantBuilder() {}

	/**
	 * Gets the instance of the constant builder.
	 */
	public static ConstantBuilder getInstance() {
		if (m_instance == null) {
			m_instance = new ConstantBuilder();
		}
		return m_instance;
	}

	/**
	 * Starts building a new constant.
	 */
	public void startBuilding() {
		if (m_isEnabled) {
			m_declaration = new Constant();
		}
	}

	/**
	 * Finishes building a declaration and gets the finished product.
	 */
	@Override
	public Constant getDeclaration() {
		if (m_isEnabled) {
			return m_declaration;
		}
		return new InvalidConstant();
	}

	/**
	 * Invalidates the declaration, such that any declaration returned will be invalid until the builder is reset.
	 */
	@Override
	public void invalidate() {
		if (m_isEnabled) {
			m_declaration = new InvalidConstant(m_declaration);
		}
	}

	/**
	 * Sets the value of the constant being built.
	 *
	 * @param value The value of the constant being built.
	 */
	public void setValue(int value) {
		if (m_isEnabled) {
			m_declaration.setValue(value);
		}
	}
}
