package parser.syntacticAnalysis;

import exception.Exception;
import exception.ExceptionHandler;
import exception.ExceptionStrength;
import parser.utilities.TokenIterator;
import shared.Reserved;

/**
 * Handles syntax-related exceptions.
 */
@SuppressWarnings("all")
class SyntacticExceptionHandler {

	/**
	 * Represents the syntactic exception handler.
	 */
	private static SyntacticExceptionHandler m_instance;
	/**
	 * Represents the maximum number of steps for which the parser will suppress exceptions.
	 */
	private static final int MAXIMUM_SUPPRESSED_STEPS = 8;
	/**
	 * Represents the remaining number of steps for which parser will suppress exceptions.
	 */
	private int m_remainingSuppressedSteps;

	/**
	 * Constructs the syntactic exception handler.
	 */
	private SyntacticExceptionHandler() {
		m_remainingSuppressedSteps = -1;
	}

	/**
	 * Gets the syntactic exception handler.
	 */
	public static SyntacticExceptionHandler getInstance() {
		if (m_instance == null) {
			m_instance = new SyntacticExceptionHandler();
		}
		return m_instance;
	}

	/**
	 * Queries whether an exception was encountered.
	 */
	public boolean encounteredExceptions() {
		return m_remainingSuppressedSteps != -1;
	}

	/**
	 * Queries if throwing exceptions is permitted.
	 */
	public boolean canThrowExceptions() {
		return m_remainingSuppressedSteps <= 0;
	}

	/**
	 * Decreases the remaining number of steps for which parser will suppress exceptions.
	 */
	public void decrementRemainingSuppressedSteps() {
		if (!SyntacticSynchronizationCoordinator.getInstance().isSynchronizing()) {
			if (m_remainingSuppressedSteps > 0) {
				m_remainingSuppressedSteps--;
			}
		}
	}

	/**
	 * Handles missing required production rules
	 *
	 * @param requirements The requirements that were not met.
	 * @return true if the parser is able to continue; otherwise, this method will abort the compiler.
	 */
	public Boolean handleMissingRequiredProductionRules(Object... requirements) {
		if (canThrowExceptions()) {
			ExceptionHandler.getInstance().throwException(Exception.ENCOUNTERED_TOKEN_DID_NOT_MEET_EXPECTATIONS, ExceptionStrength.WEAK, TokenIterator.getInstance().peek().getValue(), TokenIterator.getInstance().peek().getStartingIndex(), TokenIterator.getInstance().peek().getEndingIndex(), requirements);
		}

		if (m_remainingSuppressedSteps <= 0) {
			m_remainingSuppressedSteps = MAXIMUM_SUPPRESSED_STEPS + 1;
		}

		if (!SyntacticSynchronizationCoordinator.getInstance().isSynchronizing() && !isMissingWeakSymbol(requirements)) {
			SyntacticSynchronizationCoordinator.getInstance().synchronize();
		}

		return true;
	}

	/**
	 * Queries if the missing requirement is a weak symbol.
	 *
	 * @param requirements The requirements that were not met.
	 */
	private Boolean isMissingWeakSymbol(Object... requirements) {
		if (requirements.length == 1) {
			Object requirement = requirements[0];
			if (requirement.equals(Reserved.Symbol.CLOSING_BRACKET) ||
					requirement.equals(Reserved.Symbol.CLOSING_PARENTHESIS) ||
					requirement.equals(Reserved.Symbol.SEMICOLON) ||
					requirement.equals(Reserved.Symbol.COLON) ||
					requirement.equals(Reserved.Symbol.PERIOD)) {
				return true;
			}
		}
		return false;
	}
}
