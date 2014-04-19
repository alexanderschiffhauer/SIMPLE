package scanner.utilities;

import shared.utilities.iterator.AbstractIterator;

import java.io.InputStream;

/**
 * Iterates over the characters of a scanner's buffered input.
 */
@SuppressWarnings("all")
public class CharacterIterator extends AbstractIterator<String> {

	/**
	 * Represents the EOF character.
	 */
	private static final String EOF = "";
	/**
	 * Represents the builder for the input that is being iterated.
	 */
	private StringBuilder m_stringBuilder;
	/**
	 * Represents the reader whose buffered input's characters will be iterated.
	 */
	private BufferedInputReader m_bufferedInputReader;

	/**
	 * Constructs a character iterator.
	 *
	 * @param inputStream The input stream reader whose buffered input's characters will be iterated.
	 */
	public CharacterIterator(InputStream inputStream) {
		m_bufferedInputReader = new BufferedInputReader(inputStream);
		m_stringBuilder = new StringBuilder();
	}

	/**
	 * Appends the next line from the scanner's buffered input into the string builder, returning true if successful; false indicates EOF.
	 */
	private boolean appendNextLine() {
		String nextLine = m_bufferedInputReader.readLine();
		if (nextLine != null) {
			m_stringBuilder.append(nextLine);
			return true;
		} else {
			m_stringBuilder.append(EOF);
			return false;
		}
	}

	/**
	 * Queries whether there exists a next value.
	 */
	@Override
	public boolean hasNext() {
		boolean hasNextCharacter = !peek().equals(EOF);
		if (!hasNextCharacter && m_index == -1) {
			m_index = 0;
		}
		return hasNextCharacter;
	}

	/**
	 * Advances index, returning the next object.
	 */
	@Override
	public String getNext() {
		if (m_index >= m_stringBuilder.length() - 1) {
			if (appendNextLine()) {
				return getNext();
			} else {
				if (m_index == -1) {
					m_index = 0;
				} else {
					m_index = m_stringBuilder.length();
				}
				m_index++;
				return EOF;
			}
		} else {
			m_index++;
			return getCurrent();
		}
	}

	/**
	 * Gets the current object without advancing the index.
	 */
	@Override
	public String getCurrent() {
		return Character.toString(m_stringBuilder.charAt(m_index));
	}
}