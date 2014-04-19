package scanner;

import exception.Exception;
import exception.ExceptionHandler;
import exception.ExceptionStrength;
import scanner.utilities.CharacterIterator;
import scanner.utilities.CharacterUtilities;
import shared.Reserved;
import shared.Token;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Scans the source code of a program as a sequence of characters and recognizes tokens.
 */
@SuppressWarnings("all")
public class Scanner {

	/**
	 * Represents the instance of the scanner.
	 */
	private static Scanner m_instance;
	/**
	 * Represents the character scanner that scans the input from a java.util.Scanner
	 */
	private CharacterIterator m_characterIterator;

	/**
	 * Constructs the scanner.
	 */
	private Scanner() {}

	/**
	 * Gets the instance of the scanner.
	 */
	public static Scanner getInstance() {
		if (m_instance == null) {
			m_instance = new Scanner();
		}
		return m_instance;
	}

	/**
	 * Sets the input stream.
	 *
	 * @param inputStream The input stream.
	 */
	public void setInputStream(InputStream inputStream) {
		m_characterIterator = new CharacterIterator(inputStream);
	}

	/**
	 * Gets the next token from the input stream.
	 */
	public Token getNextToken() {
		if (m_characterIterator == null) {
			return ExceptionHandler.getInstance().throwException(Exception.NO_INPUT_STREAM, ExceptionStrength.STRONG);
		} else {
			while (m_characterIterator.hasNext()) {
				if (CharacterUtilities.isWhitespace(m_characterIterator.peek())) {
					m_characterIterator.getNext();
				} else if (CharacterUtilities.isDigit(m_characterIterator.peek())) {
					return getIntegerToken(m_characterIterator.getNext(), m_characterIterator.getIndex());
				} else if (CharacterUtilities.isLetter(m_characterIterator.peek())) {
					return getKeywordOrIdentifierToken(m_characterIterator.getNext(), m_characterIterator.getIndex());
				} else if (startsWithReservedSymbol(m_characterIterator.peek())) {
					Token token = getSymbolToken(m_characterIterator.getNext(), m_characterIterator.getIndex());
					if (isOpeningComment(token)) {
						skipCommentedCode();
						return getNextToken();
					} else if (isClosingComment(token)) {
						return ExceptionHandler.getInstance().throwException(Exception.IMPROPERLY_FORMATTED_COMMENT, ExceptionStrength.STRONG);
					} else {
						return token;
					}
				} else {
					return ExceptionHandler.getInstance().throwException(Exception.INVALID_CHARACTER, ExceptionStrength.STRONG, m_characterIterator.peek(), m_characterIterator.getIndex() + 1);
				}
			}
			return new Token<String>(m_characterIterator.getIndex(), 1, Token.Type.EOF, Token.Type.EOF.toString());
		}
	}

	/**
	 * Gets all tokens from the input stream.
	 */
	public List<Token> getAllTokens() {
		return getAllTokens(false);
	}

	/**
	 * Prints all tokens from the input stream.
	 */
	public void printAllTokens() {
		getAllTokens(true);
	}

	/**
	 * Gets, and optionally prints, all tokens from the input stream.
	 *
	 * @param shouldPrint Indicates if all the tokens should be print to standard input.
	 */
	private List<Token> getAllTokens(boolean shouldPrint) {
		if (m_characterIterator.getIndex() != -1) {
			return ExceptionHandler.getInstance().throwException(Exception.INPUT_STREAM_PARTIALLY_SCANNED, ExceptionStrength.STRONG);
		} else {
			List<Token> tokens = new ArrayList<Token>();
			Token token;
			while (tokens.add((token = getNextToken()))) {
				if (shouldPrint) {
					System.out.println(token);
				}
				if (token.getType() == Token.Type.EOF) {
					return tokens;
				}
			}
			return tokens;
		}
	}

	/**
	 * Gets an integer token from the current position of the character scanner.
	 *
	 * @param string The beginning of the token in its string-based representation.
	 * @param index  The index of the token in its string-based representation.
	 */
	private Token getIntegerToken(String string, int index) {
		while (m_characterIterator.hasNext() && CharacterUtilities.isDigit(m_characterIterator.peek())) {
			string += m_characterIterator.getNext();
		}
		return new Token<Integer>(index, string.length(), Token.Type.INTEGER, new Integer(string));
	}

	/**
	 * Gets a keyword or identifier token from the current position of the character scanner.
	 *
	 * @param string The beginning of the token in its string-based representation.
	 * @param index  The index of the token in its string-based representation.
	 */
	private Token getKeywordOrIdentifierToken(String string, int index) {
		while (m_characterIterator.hasNext() && CharacterUtilities.isLetterOrDigit(m_characterIterator.peek())) {
			string += m_characterIterator.getNext();
		}
		if (Reserved.isKeyword(string)) {
			return new Token<Reserved.Keyword>(index, string.length(), Token.Type.KEYWORD, Reserved.getKeyword(string));
		} else {
			return new Token<String>(index, string.length(), Token.Type.IDENTIFIER, string);
		}
	}

	/**
	 * Gets a symbol token from the current position of the character scanner.
	 *
	 * @param string The beginning of the token in its string-based representation.
	 * @param index  The index of the token in its string-based representation.
	 */
	private Token getSymbolToken(String string, int index) {
		if (Reserved.isSymbol(string + m_characterIterator.peek())) {
			string += m_characterIterator.getNext();
		} else if (!Reserved.isSymbol(string)) {
			return ExceptionHandler.getInstance().throwException(Exception.INVALID_CHARACTER, ExceptionStrength.STRONG, m_characterIterator.peek(), m_characterIterator.getIndex() + 1);
		}
		return new Token<Reserved.Symbol>(index, string.length(), Token.Type.SYMBOL, Reserved.getSymbol(string));
	}

	/**
	 * Queries if the character is the first character of a reserved symbol.
	 *
	 * @param character The character to query.
	 */
	private boolean startsWithReservedSymbol(String character) {
		assert character.length() == 1;
		for (Reserved.Symbol symbol : Reserved.Symbol.values()) {
			if (symbol.toString().charAt(0) == character.charAt(0)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Queries if a token is the opening comment token.
	 *
	 * @param token The token to query.
	 */
	private boolean isOpeningComment(Token token) {
		return token.getValue().toString().equals(Reserved.Symbol.OPENING_COMMENTS.toString());
	}

	/**
	 * Skips all subsequent source code until a closing comment token is encountered.
	 */
	private void skipCommentedCode() {
		while (m_characterIterator.hasNext()) {
			if (m_characterIterator.getNext().equals("*") && m_characterIterator.peek().equals(")")) {
				m_characterIterator.getNext();
				return;
			}
		}
	}

	/**
	 * Queries if a token is the closing comment token.
	 *
	 * @param token The token to query.
	 */
	private boolean isClosingComment(Token token) {
		return token.getValue().toString().equals(Reserved.Symbol.CLOSING_COMMENTS.toString());
	}
}
