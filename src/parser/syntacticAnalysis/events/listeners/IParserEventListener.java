package parser.syntacticAnalysis.events.listeners;

import shared.Token;

/**
 * Defines an object that listens to parsing events.
 */
@SuppressWarnings("all")
public interface IParserEventListener {

	/**
	 * Handles when a non-terminal symbol begins to be parsed.
	 *
	 * @param nonTerminalSymbol The non-terminal symbol that will be parsed.
	 */
	void onBeganParsingNonTerminalSymbol(String nonTerminalSymbol);

	/**
	 * Handles when a non-terminal symbol is finished being parsed.
	 *
	 * @param nonTerminalSymbol The non-terminal symbol that is finished being parsed.
	 */
	void onFinishedParsingNonTerminalSymbol(String nonTerminalSymbol);

	/**
	 * Handles when a terminal symbol was parsed.
	 *
	 * @param terminalSymbol The terminal symbol that was parsed.
	 */
	void onParsedTerminalSymbol(Token terminalSymbol);
}
