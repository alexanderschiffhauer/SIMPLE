package parser.syntacticAnalysis;

import exception.ExceptionHandler;
import parser.Parser;
import parser.utilities.TokenIterator;
import shared.Reserved;
import shared.Token;

/**
 * Coordinates the synchronization of syntax-related exceptions.
 */
@SuppressWarnings("all")
public class SyntacticSynchronizationCoordinator {

	/**
	 * Represents the syntactic synchronization coordinator.
	 */
	private static SyntacticSynchronizationCoordinator m_instance;
	/**
	 * Represents the out-of-sync token.
	 */
	private Token m_outOfSyncToken;
	/**
	 * Represents the in-sync token.
	 */
	private Token m_inSyncToken;
	/**
	 * Represents whether the token synchronization coordinator is ignoring production rules.
	 */
	private boolean m_isIgnoringProductionRules;

	/**
	 * Constructs the syntactic synchronization coordinator.
	 */
	private SyntacticSynchronizationCoordinator() {}

	/**
	 * Gets the syntactic synchronization coordinator.
	 */
	public static SyntacticSynchronizationCoordinator getInstance() {
		if (m_instance == null) {
			m_instance = new SyntacticSynchronizationCoordinator();
		}
		return m_instance;
	}

	/**
	 * Gets the in-sync token.
	 */
	public Token getInSyncToken() {
		return m_inSyncToken;
	}

	/**
	 * Gets the out-of-sync token.
	 */
	public Token getOutOfSyncToken() {
		return m_outOfSyncToken;
	}

	/**
	 * Queries whether the token synchronization coordinator is currently synchronizing tokens.
	 */
	public boolean isSynchronizing() {
		return (m_inSyncToken != null && m_outOfSyncToken != null);
	}

	/**
	 * Queries whether the token synchronization coordinator is ignoring production rules.
	 */
	public boolean isIgnoringProductionRules() {
		return m_isIgnoringProductionRules;
	}

	/**
	 * Ignores all future production rules until the token synchronization coordinator is finished synchronizing tokens.
	 */
	public void ignoreProductionRules() {
		m_isIgnoringProductionRules = true;
	}

	/**
	 * Skips the token iterator to the in-sync token.
	 */
	public void skipToInSyncToken() {
		while (TokenIterator.getInstance().peek() != SyntacticSynchronizationCoordinator.getInstance().getInSyncToken()) {
			TokenIterator.getInstance().getNext();
		}
	}

	/**
	 * Completes the synchronization of tokens by obeying all production rules and removing references to any tokens.
	 */
	public void obeyProductionRules() {
		m_isIgnoringProductionRules = false;
		m_outOfSyncToken = null;
		m_inSyncToken = null;
	}

	/**
	 * Begins synchronizing the token iterator to the next strong symbol.
	 */
	public void synchronize() {
		m_outOfSyncToken = TokenIterator.getInstance().getNext();
		Token currentToken = TokenIterator.getInstance().getNext();
		if (currentToken.getValue() != Reserved.Keyword.BEGIN &&
				currentToken.getValue() != Reserved.Keyword.CONST &&
				currentToken.getValue() != Reserved.Keyword.TYPE &&
				currentToken.getValue() != Reserved.Keyword.VAR &&
				currentToken.getValue() != Reserved.Keyword.IF &&
				currentToken.getValue() != Reserved.Keyword.REPEAT &&
				currentToken.getValue() != Reserved.Keyword.WHILE &&
				currentToken.getValue() != Reserved.Keyword.WRITE &&
				currentToken.getValue() != Reserved.Keyword.READ) {
			while (!SyntacticValidator.getInstance().isNextToken(
					ProductionRule.OPTIONAL,
					Reserved.Keyword.BEGIN,
					Reserved.Keyword.CONST,
					Reserved.Keyword.TYPE,
					Reserved.Keyword.VAR,
					Reserved.Keyword.IF,
					Reserved.Keyword.REPEAT,
					Reserved.Keyword.WHILE,
					Reserved.Keyword.WRITE,
					Reserved.Keyword.READ
			)) {
				if (SyntacticValidator.getInstance().isNextToken(ProductionRule.OPTIONAL, Token.Type.EOF)) {
					ExceptionHandler.getInstance().abort();
				} else {
					TokenIterator.getInstance().getNext();
				}
			}
		}
		m_inSyncToken = TokenIterator.getInstance().peek();
		TokenIterator.getInstance().setIndex(-1);
		Parser.getInstance().parseTokens();
		ExceptionHandler.getInstance().abort();
	}
}