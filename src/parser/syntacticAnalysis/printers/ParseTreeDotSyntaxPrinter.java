package parser.syntacticAnalysis.printers;

import parser.syntacticAnalysis.events.listeners.IParserEventListener;
import shared.Token;

import java.util.Stack;

/**
 * Represents the dot syntax printer for a parse tree.
 */
@SuppressWarnings("all")
public class ParseTreeDotSyntaxPrinter implements IParserEventListener, IParseTreePrinter {

	/**
	 * The instance of the parse tree dot syntax printer.
	 */
	private static ParseTreeDotSyntaxPrinter m_instance;
	/**
	 * Represents the builder for the string representation of the parse tree.
	 */
	private StringBuilder m_stringBuilder;
	/**
	 * Represents the queue of root nodes.
	 */
	private Stack<Integer> m_rootNodes;
	/**
	 * Represents the current node of the parse tree.
	 */
	private int m_currentNode;

	/**
	 * Constructs the parse tree dot syntax printer.
	 */
	private ParseTreeDotSyntaxPrinter() {
		m_stringBuilder = new StringBuilder();
		m_rootNodes = new Stack<Integer>();

		m_currentNode = -1;

		m_stringBuilder.append("strict digraph CST {\n");
	}

	/**
	 * Gets the instance of the parse tree dot syntax printer.
	 */
	public static ParseTreeDotSyntaxPrinter getInstance() {
		if (m_instance == null) {
			m_instance = new ParseTreeDotSyntaxPrinter();
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
		m_currentNode++;

		String string = "L" + m_currentNode + " [label=\"" + nonTerminalSymbol + "\",shape=box]\n";
		if (m_currentNode - 1 > -1) {
			string += "L" + (m_rootNodes.peek()) + " -> " + "L" + m_currentNode + "\n";
		}

		m_stringBuilder.append(string);
		m_rootNodes.add(m_currentNode);
	}

	/**
	 * Handles when a non-terminal symbol is finished being parsed.
	 *
	 * @param nonTerminalSymbol The non-terminal symbol that is finished being parsed.
	 */
	@Override
	public void onFinishedParsingNonTerminalSymbol(String nonTerminalSymbol) {
		m_rootNodes.pop();
	}

	/**
	 * Handles when a terminal symbol was parsed.
	 *
	 * @param terminalSymbol The terminal symbol that was parsed.
	 */
	@Override
	public void onParsedTerminalSymbol(Token terminalSymbol) {
		String string = "L" + ++m_currentNode + " [label=\"" + terminalSymbol.getValue().toString() + "\",shape=diamond]\n";
		string += "L" + m_rootNodes.peek() + " -> " + "L" + m_currentNode + "\n";
		m_stringBuilder.append(string);

	}

	/**
	 * Prints the parse tree.
	 */
	@Override
	public void printParseTree() {
		m_stringBuilder.append("}");
		System.out.println(m_stringBuilder);
	}
}
