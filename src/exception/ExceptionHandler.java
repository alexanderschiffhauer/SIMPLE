package exception;

/**
 * Handles exceptions in the SIMPLE compiler.
 */
public class ExceptionHandler {

	/**
	 * Represents the instance of the exception handler.
	 */
	private static ExceptionHandler m_instance;

	/**
	 * Represents the string builder used to for printing to standard error.
	 * This string builder logs every error and also prevents the same error from being printed twice.
	 */
	private StringBuilder m_stringBuilder;

	/**
	 * Constructs the exception handler.
	 */
	private ExceptionHandler() {
		m_stringBuilder = new StringBuilder();
	}

	/**
	 * Gets the instance of the exception handler.
	 */
	public static ExceptionHandler getInstance() {
		if (m_instance == null) {
			m_instance = new ExceptionHandler();
		}
		return m_instance;
	}

	/**
	 * Aborts the SIMPLE compiler by throwing an exception.
	 *
	 * @param exception         The type of exception that was encountered.
	 * @param exceptionStrength The strength of the exception.
	 */
	public <T> T throwException(Exception exception, ExceptionStrength exceptionStrength) {
		print(exception.toString());
		return abort(exceptionStrength);
	}

	/**
	 * Aborts the SIMPLE compiler by throwing an exception.
	 *
	 * @param exception         The type of exception that was encountered.
	 * @param exceptionStrength The strength of the exception.
	 * @param objects           The objects that correspond to the exception.
	 */
	@SuppressWarnings("all")
	// Because of a bug in JDK 6, we need to explicitly cast enums to their interfaces to invoke any promises.  We suppress any warnings pertinent to this issue.
	public <T> T throwException(Exception exception, ExceptionStrength exceptionStrength, Object... objects) {
		print(((IException) exception).toString(objects));
		return abort(exceptionStrength);
	}

	/**
	 * Aborts the SIMPLE compiler by throwing an exception.
	 *
	 * @param message           The message to write to standard error.
	 * @param exceptionStrength The strength of the exception.
	 */
	public <T> T throwException(String message, ExceptionStrength exceptionStrength) {
		print("error: " + message);
		return abort(exceptionStrength);
	}


	/**
	 * Prints the exception to standard input.
	 *
	 * @param string The string to print to standard error.
	 */
	private void print(String string) {
		if (m_stringBuilder.indexOf(string) == -1) { // && string.indexOf("\"Invalid") == -1 && string.indexOf("\"1") == -1 && string.indexOf("\0") == -1) {
			m_stringBuilder.append(string).append('\n');
			System.err.println(string);
		}
	}

	/**
	 * Aborts the SIMPLE compiler.
	 */
	public <T> T abort() {
		System.exit(1);
		return null;
	}

	/**
	 * Aborts the SIMPLE compiler.
	 *
	 * @param exceptionStrength The strength of the exception.
	 */
	public <T> T abort(ExceptionStrength exceptionStrength) {
		if (exceptionStrength == ExceptionStrength.STRONG) {
			System.exit(1);
		}
		return null;
	}
}
