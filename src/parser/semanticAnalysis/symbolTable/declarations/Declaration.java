package parser.semanticAnalysis.symbolTable.declarations;

import parser.semanticAnalysis.symbolTable.declarations.constants.constant.Constant;
import parser.semanticAnalysis.symbolTable.declarations.procedures.Parameter;
import parser.semanticAnalysis.symbolTable.declarations.procedures.Procedure;
import parser.semanticAnalysis.symbolTable.declarations.types.Type;
import parser.semanticAnalysis.symbolTable.declarations.types.array.Array;
import parser.semanticAnalysis.symbolTable.declarations.types.record.Record;
import parser.semanticAnalysis.symbolTable.declarations.variable.Variable;
import shared.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the declarations in a SIMPLE program, which are the entries in a scope.
 */
public abstract class Declaration {

	/**
	 * Represents the name of the declaration.
	 */
	private String m_name;

	/**
	 * Represents the tokens associated with this declaration.
	 */
	private List<Token> m_tokens;

	/**
	 * Constructs a new declaration.
	 *
	 * @param name The name of the declaration.
	 */
	public Declaration(String name) {
		this();
		m_name = name;
	}

	/**
	 * Constructs a new declaration.
	 */
	public Declaration() {
		m_tokens = new ArrayList<Token>();
	}

	/**
	 * Gets the name of the declaration.
	 */
	public String getName() {
		return m_name;
	}

	/**
	 * Sets the name of the declaration.
	 *
	 * @param name The name of the declaration.
	 */
	public void setName(String name) {
		m_name = name;
	}

	/**
	 * Sets the tokens associated with the declaration.
	 *
	 * @param tokens The tokens associated with the declaration.
	 */
	public void setTokens(List<Token> tokens) {
		m_tokens = tokens;
	}

	/**
	 * Gets the token associated with the declaration.
	 */
	public List<Token> getTokens() {
		return m_tokens;
	}

	/**
	 * Gets the position of the declaration.  Possible values are "UNKNOWN POSITION" and "(startingIndex, endingIndex)".
	 */
	public String getPosition() {
		if (m_tokens.isEmpty()) {
			return "UNKNOWN POSITION";
		} else if (m_tokens.size() == 1) {
			Token token = m_tokens.get(0);
			return "(" + token.getStartingIndex() + ", " + token.getEndingIndex() + ")";
		} else {
			Token startingToken = m_tokens.get(0);
			Token endingToken = m_tokens.get(m_tokens.size() - 1);
			return "(" + startingToken.getStartingIndex() + ", " + endingToken.getEndingIndex() + ")";
		}
	}

	/**
	 * Queries this declaration is integral.
	 */
	public boolean isIntegral() {
		if (this instanceof Constant) {
			return true;
		} else if (this instanceof parser.semanticAnalysis.symbolTable.declarations.types.Integer) {
			return true;
		} else if (this instanceof Variable) {
			return ((Variable) this).getType().isIntegral();
		} else if (this instanceof Type) {
			if (this instanceof Array) {
				return ((Array) this).getElementType().isIntegral();
			} else if (this instanceof Record) {
				return false;
			} else {
				return false;
			}
		} else if (this instanceof Procedure) {
			return ((Procedure) this).getReturnType().isIntegral();
		} else if (this instanceof Parameter) {
			return ((Parameter) this).getType().isIntegral();
		} else {
			return false;
		}
	}
}
