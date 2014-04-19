package parser.syntacticAnalysis.events;

/**
 * Defines the parser's events.
 */
@SuppressWarnings("all")
public enum ParserEvent {
	BEGAN_PARSING_NON_TERMINAL_SYMBOL,
	FINISHED_PARSING_NON_TERMINAL_SYMBOL,
	PARSED_TERMINAL_SYMBOL
}