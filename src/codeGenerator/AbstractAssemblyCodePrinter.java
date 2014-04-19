package codeGenerator;

import codeGenerator.utilities.Instruction;
import exception.Exception;
import exception.ExceptionHandler;
import exception.ExceptionStrength;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Prints assembly code to either standard-output or a file.
 */
public abstract class AbstractAssemblyCodePrinter {

	/**
	 * Represents the prefix used for instructions.
	 */
	public static final String LABEL_PREFIX = "L";

	/**
	 * Represents the prefix used for array exception-related instructions.
	 */
	public static final String ARRAY_EXCEPTION_PREFIX = "A";

	/**
	 * Represents the prefix used for array exception message-related instructions.
	 */
	public static final String ARRAY_EXCEPTION_MESSAGE_PREFIX = "AEM";

	/**
	 * Represents the string builder used for indentation (tabs).
	 */
	private static StringBuilder s_stringBuilder = new StringBuilder();

	/**
	 * Represents the output stream writer used to write to a file or standard output.
	 */
	private static OutputStreamWriter s_outputStreamWriter = new OutputStreamWriter(System.out);

	/**
	 * Represents the counter for the number of quad words to store in memory.
	 */
	protected int m_memorySize = 0; // _base is the first variable in memory.

	/**
	 * Represents the counter used for array-related error messages.  Quite hacky.
	 */
	protected static int s_arrayCounter;

	/**
	 * Sets the file name of the file in which to write assembly code.
	 *
	 * @param fileName The file name of the file in which to write assembly code.
	 */
	public static void setFileName(String fileName) {
		try {
			s_outputStreamWriter = new OutputStreamWriter(new FileOutputStream(fileName.substring(0, fileName.lastIndexOf('.')) + ".s"));
		} catch (IOException e) {
			ExceptionHandler.getInstance().throwException(Exception.IO_ERROR, ExceptionStrength.STRONG);
		}
	}

	/**
	 * Closes the output stream writer.
	 */
	protected void close() {
		try {
			s_outputStreamWriter.close();
		} catch (IOException e) {
			ExceptionHandler.getInstance().throwException(Exception.IO_ERROR, ExceptionStrength.STRONG);
		}
	}

	/**
	 * Prints a string.
	 *
	 * @param string The string to print.
	 */
	protected void print(String string) {
		try {
			s_outputStreamWriter.write(s_stringBuilder.toString() + string + "\n");
		} catch (IOException e) {
			ExceptionHandler.getInstance().throwException(exception.Exception.IO_ERROR, ExceptionStrength.STRONG);
		}
	}

	/**
	 * Prints assembly code.
	 *
	 * @param instruction The instruction.
	 * @param objects     The registers, addresses, or immediate values to print.
	 */
	protected void print(Instruction instruction, Object... objects) {
		if (objects.length == 0) {
			print(instruction.toString());
		} else if (objects.length == 1) {
			print(instruction + " " + objects[0]);
		} else if (objects.length == 2) {
			print(instruction + " " + objects[0] + ", " + objects[1]);
		} else {
			ExceptionHandler.getInstance().throwException(exception.Exception.ENCOUNTERED_A_BUG, ExceptionStrength.STRONG);
		}
	}

	/**
	 * Prints a new line.
	 */
	protected void printNewLine() {
		print("");
	}

	/**
	 * Tabs the output.
	 */
	protected void indent() {
		s_stringBuilder.append('\t');
	}

	/**
	 * Removes an indent from the output.
	 */
	protected void removeIndent() {
		if (s_stringBuilder.length() > 0) {
			s_stringBuilder.deleteCharAt(s_stringBuilder.length() - 1);
		}
	}
}
