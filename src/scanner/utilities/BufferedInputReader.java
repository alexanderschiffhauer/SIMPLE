package scanner.utilities;

import exception.ExceptionHandler;
import exception.ExceptionStrength;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * Represents a buffered input reader.
 */
@SuppressWarnings("all")
public class BufferedInputReader {

	/**
	 * Represents the buffer size for reading from standard input.
	 */
	private static final int STANDARD_INPUT_BUFFER_SIZE = 1048576 * 10;
	/**
	 * Represents a line number reader.
	 */
	private LineNumberReader m_lineNumberReader;
	/**
	 * Represents the input stream from which to read input.
	 */
	private InputStream m_inputStream;

	/**
	 * Constructs a new buffered input reader.
	 *
	 * @param inputStream The input stream from which to read input.
	 */
	public BufferedInputReader(InputStream inputStream) {
		m_inputStream = inputStream;
		m_lineNumberReader = new LineNumberReader(new InputStreamReader(m_inputStream));
	}

	/**
	 * Reads and gets the next line with the line terminator.
	 *
	 * @return The next line if one exists, or null if EOF.
	 */
	public String readLine() {
		try {
			int availableBuffer = m_inputStream.available();
			/**
			 * If the available buffer size (in bytes) is 0, that means the input stream is reading from standard input.
			 * If the input stream is reading from standard input, we limit the buffer size of each line to 10 megabytes.
			 * The reason we have to go through all of this pain in the first place is because Java does not allow one to use
			 * a buffered input reader and read terminating symbols (i.e. \n, \r, etc.).  Instead, Java returns entire lines without
			 * terminal symbols.  To get around this bad design choice, we must "mark" a spot on the buffered input reader, read the line,
			 * go back to the "marked" spot, skip the read characters, and read the omitted terminating symbol.  Lastly, for some
			 * inexplicable reason, Java requires us to specify how many bytes (i.e. characters) we are allowed to go forward
			 * before invalidating the "marked" spot.  If we are reading from a file, this is not an issue, since the entire file is
			 * in RAM, so we can calculate this.  However, reading from stdin means we won't know until the line is ready.  10 megabytes
			 * is huge enough for giant obfuscated code that will be copy-and-pasted and small enough to not make a giant impact on performance.
			 * It just makes scanning that much more resource-intensive.
			 */
			if (availableBuffer == 0) {
				m_lineNumberReader.mark(STANDARD_INPUT_BUFFER_SIZE);
			} else {
				m_lineNumberReader.mark(availableBuffer);
			}
			String line = m_lineNumberReader.readLine();
			if (line == null) {
				return null;
			} else {
				try {
					m_lineNumberReader.reset();
				} catch (IOException e) {
					return line;
				}
				m_lineNumberReader.skip(line.length());
				int terminator = m_lineNumberReader.read();
				if (terminator != -1) {
					line += (char) terminator;
				}
				return line;
			}
		} catch (IOException e) {
			ExceptionHandler.getInstance().throwException(exception.Exception.IO_ERROR, ExceptionStrength.STRONG);
			return null;
		}
	}
}
