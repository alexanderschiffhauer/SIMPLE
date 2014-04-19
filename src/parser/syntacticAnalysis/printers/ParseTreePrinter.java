package parser.syntacticAnalysis.printers;

import parser.syntacticAnalysis.events.listeners.IParserEventListener;
import shared.Token;

/**
 * Represents the printer for a parse tree.
 */
@SuppressWarnings("all")
public class ParseTreePrinter implements IParserEventListener, IParseTreePrinter {

	/**
	 * The instance of the parse tree string printer.
	 */
	private static ParseTreePrinter m_instance;
	/**
	 * Represents the builder for the string representation of the parse tree.
	 */
	private StringBuilder m_stringBuilder;
	/**
	 * The spaces to add to each line of the string builder.
	 */
	private String m_spaces;

	/**
	 * Constructs the parse tree string printer.
	 */
	private ParseTreePrinter() {
		m_stringBuilder = new StringBuilder();
		m_spaces = "";
	}

	/**
	 * Gets the instance of the parse tree string printer.
	 */
	public static ParseTreePrinter getInstance() {
		if (m_instance == null) {
			m_instance = new ParseTreePrinter();
		}
		return m_instance;
	}

	/**
	 * Handles when a non-terminal symbol begins to be parsed.
	 *
	 * @param nonTerminalSymbol The non-terminal symbol that will be parsed.
	 */
	@Override
	public void onBeganParsingNonTerminalSymbol(String nonTerminalSymbol) {
		String string = m_spaces + nonTerminalSymbol + '\n';
		m_stringBuilder.append(string);
		m_spaces += "  ";
	}

	/**
	 * Handles when a non-terminal symbol is finished being parsed.
	 *
	 * @param nonTerminalSymbol The non-terminal symbol that is finished being parsed.
	 */
	@Override
	public void onFinishedParsingNonTerminalSymbol(String nonTerminalSymbol) {
		m_spaces = m_spaces.substring(0, m_spaces.length() - 2);
	}

	/**
	 * Handles when a terminal symbol was parsed.
	 *
	 * @param terminalSymbol The terminal symbol that was parsed.
	 */
	@Override
	public void onParsedTerminalSymbol(Token terminalSymbol) {
		String string = m_spaces + terminalSymbol.toString() + '\n';
		m_stringBuilder.append(string);
	}

	/**
	 * Prints the parse tree.
	 */
	@Override
	public void printParseTree() {
		System.out.println(m_stringBuilder.toString());
	}
}
