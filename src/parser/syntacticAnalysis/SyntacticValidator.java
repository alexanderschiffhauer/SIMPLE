package parser.syntacticAnalysis;

import parser.Parser;
import parser.syntacticAnalysis.events.ParserEvent;
import parser.utilities.TokenIterator;
import shared.Reserved;
import shared.Token;

/**
 * Matches and queries attributes of a token, thereby validating the syntactic correctness of the input.
 */
@SuppressWarnings("all")
public class SyntacticValidator {

	/**
	 * Represents the syntactic validator.
	 */
	private static SyntacticValidator s_instance;

	/**
	 * Constructs the syntactic validator.
	 */
	private SyntacticValidator() {}

	/**
	 * Gets the syntactic validator.
	 */
	public static SyntacticValidator getInstance() {
		if (s_instance == null) {
			s_instance = new SyntacticValidator();
		}
		return s_instance;
	}

	/**
	 * Matches the next token type or value.
	 *
	 * @param productionRule Determines if an error will be thrown if the matching fails.
	 * @param objects        The types or values of the token for which to test this condition.
	 */
	public void matchNextToken(ProductionRule productionRule, Object... objects) {
		for (Object object : objects) {
			if (isNextToken(ProductionRule.OPTIONAL, object)) {
				SyntacticExceptionHandler.getInstance().decrementRemainingSuppressedSteps(); // Only decremented if not synchronizing.
				Parser.getInstance().dispatchEvent(ParserEvent.PARSED_TERMINAL_SYMBOL, TokenIterator.getInstance().getNext());
				return;
			}
		}
		if (productionRule == ProductionRule.REQUIRED && !SyntacticSynchronizationCoordinator.getInstance().isSynchronizing()) {
			SyntacticExceptionHandler.getInstance().handleMissingRequiredProductionRules(objects);
		}
	}

	/**
	 * Queries whether the next token type or value is as specified.
	 *
	 * @param productionRule Determines if an error will be thrown if the condition fails.
	 * @param objects        The types or values of the token for which to test this condition.
	 */
	@SuppressWarnings("all")
	public Boolean isNextToken(ProductionRule productionRule, Object... objects) {
		if (SyntacticSynchronizationCoordinator.getInstance().isSynchronizing()) {
			if (!SyntacticSynchronizationCoordinator.getInstance().isIgnoringProductionRules() && TokenIterator.getInstance().peek() == SyntacticSynchronizationCoordinator.getInstance().getOutOfSyncToken()) {
				SyntacticSynchronizationCoordinator.getInstance().ignoreProductionRules();
				SyntacticSynchronizationCoordinator.getInstance().skipToInSyncToken();
				for (Object object : objects) {
					try {
						Reserved.Keyword value = (Reserved.Keyword) object;
						if (value.equals(Reserved.Keyword.BEGIN)) {
							return true;
						}
					} catch (ClassCastException exception) {}
				}
				return false;
			} else if (SyntacticSynchronizationCoordinator.getInstance().isIgnoringProductionRules()) {
				for (Object object : objects) {
					try {
						Reserved.Keyword value = (Reserved.Keyword) object;
						if (value.equals(SyntacticSynchronizationCoordinator.getInstance().getInSyncToken().getValue())) {
							SyntacticSynchronizationCoordinator.getInstance().obeyProductionRules();
							return true;
						} else if (value.equals(Reserved.Keyword.BEGIN)) {
							return true;
						}
					} catch (ClassCastException exception) {}
				}
				return false;
			}
		}
		for (Object object : objects) {
			try {
				Token.Type type = (Token.Type) object;
				if (isNextTokenType(type)) {
					return true;
				}
			} catch (ClassCastException exception) {
				if (isNextTokenValue(object)) {
					return true;
				}
			}
		}
		if (productionRule == ProductionRule.REQUIRED && !SyntacticSynchronizationCoordinator.getInstance().isSynchronizing()) {
			return SyntacticExceptionHandler.getInstance().handleMissingRequiredProductionRules(objects);
		} else {
			return false;
		}
	}


	/**
	 * Queries whether the next token is of a given type without advancing to the next token.
	 *
	 * @param tokenTypes The types of token for which to test this condition.
	 */
	private boolean isNextTokenType(Token.Type... tokenTypes) {
		Token.Type nextTokenType = TokenIterator.getInstance().peek().getType();
		for (Token.Type tokenType : tokenTypes) {
			if (nextTokenType == tokenType) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Queries whether the next token's value is a specified value without advancing to the next token.
	 *
	 * @param values The values of the token for which to test this condition
	 */
	private <V> boolean isNextTokenValue(V... values) {
		for (V value : values) {
			if (value.equals(TokenIterator.getInstance().peek().getValue())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Queries whether an exception was encountered.
	 */
	public boolean encounteredExceptions() {
		return SyntacticExceptionHandler.getInstance().encounteredExceptions();
	}
}
