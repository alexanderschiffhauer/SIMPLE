package parser.syntacticAnalysis.events.dispatcher;

import exception.Exception;
import exception.ExceptionHandler;
import exception.ExceptionStrength;
import parser.syntacticAnalysis.events.ParserEvent;
import parser.syntacticAnalysis.events.listeners.IParserEventListener;
import shared.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a dispatcher of parser events.
 */
@SuppressWarnings("all")
public class ParserEventDispatcher implements IParserEventDispatcher {

	/**
	 * Represents the list of parser event listeners.
	 */
	private List<IParserEventListener> m_parserEventListeners;

	/**
	 * Constructs a new parser event dispatcher.
	 */
	public ParserEventDispatcher() {
		m_parserEventListeners = new ArrayList<IParserEventListener>();
	}

	/**
	 * Registers a parser event listener for parser events.
	 *
	 * @param parserEventListener The parser event listener to register.
	 */
	@Override
	public void addEventListener(IParserEventListener parserEventListener) {
		m_parserEventListeners.add(parserEventListener);
	}

	/**
	 * Dispatches an event.
	 *
	 * @param event  The event to dispatch.
	 * @param string The string associated with the event.
	 */
	@Override
	public void dispatchEvent(ParserEvent event, String string) {
		for (IParserEventListener parserListener : m_parserEventListeners) {
			switch (event) {
				case BEGAN_PARSING_NON_TERMINAL_SYMBOL:
					parserListener.onBeganParsingNonTerminalSymbol(string);
					break;
				case FINISHED_PARSING_NON_TERMINAL_SYMBOL:
					parserListener.onFinishedParsingNonTerminalSymbol(string);
					break;
				default:
					ExceptionHandler.getInstance().throwException(Exception.EVENT_DISPATCHING_ERROR, ExceptionStrength.STRONG);
			}
		}
	}

	/**
	 * Dispatches an event.
	 *
	 * @param event The event to dispatch.
	 * @param token The token associated with the event.
	 */
	@Override
	public void dispatchEvent(ParserEvent event, Token token) {
		for (IParserEventListener parserListener : m_parserEventListeners) {
			switch (event) {
				case PARSED_TERMINAL_SYMBOL:
					parserListener.onParsedTerminalSymbol(token);
					break;
				default:
					ExceptionHandler.getInstance().throwException(Exception.EVENT_DISPATCHING_ERROR, ExceptionStrength.STRONG);
			}
		}
	}
}
