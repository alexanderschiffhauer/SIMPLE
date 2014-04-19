package parser.semanticAnalysis.symbolTable.declarations.types.array;

import parser.semanticAnalysis.symbolTable.declarations.DeclarationBuilder;
import parser.semanticAnalysis.symbolTable.declarations.types.Type;
import shared.Token;

import java.util.List;
import java.util.Stack;

/**
 * Builds arrays.
 */
public class ArrayBuilder extends DeclarationBuilder<Array> {

	/**
	 * Represents the instance of the array builder.
	 */
	private static ArrayBuilder m_instance;
	/**
	 * Represents the stack of arrays being built.
	 */
	private Stack<Array> m_stack;

	/**
	 * Constructs the array builder.
	 */
	private ArrayBuilder() {
		m_stack = new Stack<Array>();
	}

	/**
	 * Gets the instance of the array builder.
	 */
	public static ArrayBuilder getInstance() {
		if (m_instance == null) {
			m_instance = new ArrayBuilder();
		}
		return m_instance;
	}

	/**
	 * Starts building a new declaration.
	 */
	@Override
	public void startBuilding() {
		if (m_isEnabled) {
			m_stack.push(new Array());
		}
	}

	/**
	 * Sets the length of the array being built.
	 *
	 * @param length The length of the array being built.
	 */
	public void setLength(int length) {
		if (m_isEnabled) {
			m_stack.peek().setLength(length);
		}
	}

	/**
	 * Sets the name of the declaration being built.
	 *
	 * @param name Represents the name of the declaration being built.
	 */
	@Override
	public void setName(String name) {
		if (m_isEnabled) {
			m_stack.peek().setName(name);
		}
	}

	/**
	 * Sets the tokens associated with the declaration being built.
	 *
	 * @param tokens The tokens associated with the declaration being built.
	 */
	@Override
	public void setTokens(List<Token> tokens) {
		if (m_isEnabled) {
			m_stack.peek().setTokens(tokens);
		}
	}

	/**
	 * Sets the element type of the array being built.
	 *
	 * @param elementType The element type of the array being built.
	 */
	public void setElementType(Type elementType) {
		if (m_isEnabled) {
			m_stack.peek().setElementType(elementType);
		}
	}

	/**
	 * Finishes building a declaration and gets the finished product.
	 */
	@Override
	public Array getDeclaration() {
		if (m_isEnabled) {
			return m_stack.pop();
		}
		return null; //TODO InvalidArray
	}

	/**
	 * Invalidates the declaration, such that any declaration returned will be invalid until the builder is reset.
	 */
	@Override
	public void invalidate() {
		if (m_isEnabled) {
			m_stack.set(m_stack.size() - 1, new InvalidArray(m_stack.peek()));
		}
	}

	/**
	 * Resets the builder.
	 */
	@Override
	public void reset() {
	}
}
