package parser.semanticAnalysis.symbolTable.declarations;

import shared.InvalidToken;
import shared.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent an invalid declaration (i.e. a declaration that was built because something went wrong).
 */
public class InvalidDeclaration extends Declaration {

	/**
	 * Represents the unique name of the invalid declaration.
	 */
	private static int UNIQUE_NAME = Integer.MIN_VALUE;

	/**
	 * Constructs a new invalid declaration.
	 */
	public InvalidDeclaration() {
		this(getUniqueName());
	}

	/**
	 * Constructs a new invalid declaration.
	 *
	 * @param name The name of the declaration.
	 */
	public InvalidDeclaration(String name) {
		super(name);
		initializeInvalidDeclaration(this);
		setName(name);
	}

	/**
	 * Sets the tokens as a single token for this invalid declaration.
	 *
	 * @param token The token to set.
	 */
	public InvalidDeclaration setToken(Token token) {
		List<Token> tokens = new ArrayList<Token>();
		tokens.add(token);
		setTokens(tokens);
		return this;
	}

	/**
	 * Gets a unique name for invalid declarations.
	 */
	public static String getUniqueName() {
		return Integer.toBinaryString(++UNIQUE_NAME);
	}

	/**
	 * Initializes an invalid declaration.
	 *
	 * @param declaration The invalid declaration to invalidate.
	 */
	public static void initializeInvalidDeclaration(Declaration declaration) {
		declaration.setName(getUniqueName());
		List<Token> tokens = new ArrayList<Token>();
		tokens.add(new InvalidToken());
		tokens.add(new InvalidToken());
		declaration.setTokens(tokens);
	}

	/**
	 * Initializes an invalid declaration with a declaration's existing values.
	 *
	 * @param invalidDeclaration The invalid declaration to populate.
	 * @param declaration        The declaration from which information will be copied.
	 */
	public static void initializeInvalidDeclaration(Declaration invalidDeclaration, Declaration declaration) {
		if (declaration != null) {
			if (declaration.getName() != null) {
				invalidDeclaration.setName(declaration.getName());
			}
			if (!declaration.getTokens().isEmpty()) {
				invalidDeclaration.setTokens(declaration.getTokens());
			}
		}
	}
}
