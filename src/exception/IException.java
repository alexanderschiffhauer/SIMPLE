package exception;

/**
 * Defines an exception.
 */
public interface IException {

	/**
	 * Stringifies the exception with various arguments.
	 *
	 * @param objects The arguments with which to create the stringified version of the exception.
	 */
	String toString(Object... objects);
}
