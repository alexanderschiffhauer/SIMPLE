package parser.syntacticAnalysis.events.dispatcher;

import parser.syntacticAnalysis.events.ParserEvent;
import parser.syntacticAnalysis.events.listeners.IParserEventListener;
import shared.Token;

/**
 * Defines an object that dispatches parsing events.
 */
@SuppressWarnings("all")
public interface IParserEventDispatcher {

	/**
	 * Registers a parser event listener for parser events.
	 *
	 * @param parserEventListener The parser event listener to register.
	 */
	void addEventListener(IParserEventListener parserEventListener);

	/**
	 * Dispatches an event.
	 *
	 * @param event  The event to dispatch.
	 * @param string The string associated with the event.
	 */
	void dispatchEvent(ParserEvent event, String string);

	/**
	 * Dispatches an event.
	 *
	 * @param event The event to dispatch.
	 * @param token The token associated with the event.
	 */
	void dispatchEvent(ParserEvent event, Token token);
}