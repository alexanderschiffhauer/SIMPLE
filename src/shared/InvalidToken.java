package shared;

/**
 * Represents an invalid token (i.e. a token that is used in lieu of a real token because something went wrong).
 */
public class InvalidToken extends Token {

	/**
	 * Constructs a token.
	 *
	 * @param index  The index of the token.
	 * @param length The length of the token.
	 * @param type   The type of the token.
	 * @param value  The value of the token.
	 */
	public InvalidToken(int index, int length, Type type, Object value) {
		super(index, length, type, value);
	}

	/**
	 * Constructs a token.
	 */
	public InvalidToken() {
		this(-1, -1, Type.INVALID, new Object());
	}
}
