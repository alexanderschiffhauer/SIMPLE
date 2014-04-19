package scanner.utilities;

/**
 * Provides utilities for querying attributes of a character.
 */
@SuppressWarnings("all")
public class CharacterUtilities {

	/**
	 * Queries if the character is white space.
	 *
	 * @param character The character to query.
	 */
	public static boolean isWhitespace(String character) {
		assert character.length() == 1;
		return Character.isWhitespace(character.charAt(0));
	}

	/**
	 * Queries if the character is a digit.
	 *
	 * @param character The character to query.
	 */
	public static boolean isDigit(String character) {
		assert character.length() == 1;
		return Character.isDigit(character.charAt(0));
	}

	/**
	 * Queries if the character is a letter.
	 *
	 * @param character The character to query.
	 */
	public static boolean isLetter(String character) {
		assert character.length() == 1;
		return Character.isLetter(character.charAt(0));
	}

	/**
	 * Queries if the character is a letter or digit.
	 *
	 * @param character The character to query.
	 */
	public static boolean isLetterOrDigit(String character) {
		assert character.length() == 1;
		return Character.isLetterOrDigit(character.charAt(0));
	}
}
