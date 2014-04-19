package parser.utilities;

import shared.InvalidToken;
import shared.Token;
import shared.utilities.iterator.AbstractIterator;

import java.util.ArrayList;
import java.util.List;

/**
 * Iterates over tokens.
 */
public class TokenIterator extends AbstractIterator<Token> {

	/**
	 * Represents the tokens that will be iterated.
	 */
	private List<Token> m_tokens;

	/**
	 * The token iterator.
	 */
	private static TokenIterator s_instance;

	/**
	 * Constructs the token iterator.
	 */
	private TokenIterator() {
	}

	/**
	 * Gets the token iterator.
	 */
	public static TokenIterator getInstance() {
		if (s_instance == null) {
			s_instance = new TokenIterator();
		}
		return s_instance;
	}

	/**
	 * Sets the tokens that will be iterated.
	 *
	 * @param tokens The tokens that will be iterated.
	 */
	public void setTokens(List<Token> tokens) {
		m_tokens = tokens;
	}

	/**
	 * Queries whether there exists a next value.
	 */
	@Override
	public boolean hasNext() {
		return (m_index == -1) || (m_tokens.get(m_index).getType() != Token.Type.EOF);
	}

	/**
	 * Advances index, returning the next object.
	 */
	@Override
	public Token getNext() {
		if (hasNext()) {
			return m_tokens.get(++m_index);
		} else {
			return m_tokens.get(m_tokens.size() - 1);
		}
	}

	/**
	 * Gets the current object without advancing the index.
	 */
	@Override
	public Token getCurrent() {
		return m_tokens.get(m_index);
	}

	/**
	 * Gets a list of tokens from the starting and ending index.
	 *
	 * @param startingIndex The starting index from which to start listing tokens.
	 * @param endingIndex   The ending index from which to end listing tokens.
	 */
	public List<Token> getRange(int startingIndex, int endingIndex) {
		List<Token> tokens = new ArrayList<Token>();
		try {
			for (int i = startingIndex; i < endingIndex; i++) {
				tokens.add(m_tokens.get(i));
			}
		} catch (IndexOutOfBoundsException exception) {
			tokens = new ArrayList<Token>();
			m_tokens.add(new InvalidToken());
			m_tokens.add(new InvalidToken());
		}
		return tokens;
	}

	/**
	 * Gets the name from a list of tokens.
	 *
	 * @param tokens The tokens from which to get a name.
	 */
	public String getName(List<Token> tokens) {
		String name = "";
		for (Token token : tokens) {
			name += token.getValue().toString() + " ";
		}
		return name.substring(0, name.length() - 1);
	}
}
