package parser.semanticAnalysis.abstractSyntaxTree.expressions.utilities;

import parser.semanticAnalysis.SemanticValidator;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.Binary;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.Expression;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.Number;
import parser.semanticAnalysis.symbolTable.declarations.constants.constant.Constant;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Folds constants.
 */
public class ConstantFolder {

	/**
	 * Represents the instance of the constant folder.
	 */
	private static ConstantFolder m_instance;

	/**
	 * Represents whether the constant folder is enabled.
	 */
	private boolean m_isEnabled = true;

	/**
	 * Gets the instance of the constant folder.
	 */
	public static ConstantFolder getInstance() {
		if (m_instance == null) {
			m_instance = new ConstantFolder();
		}
		return m_instance;
	}

	/**
	 * Constructs the constant folder.
	 */
	private ConstantFolder() {
	}

	/**
	 * Disables the constant folder.
	 */
	public void disable() {
		if (m_isEnabled) {
			m_isEnabled = false;
		}
	}

	/**
	 * Reduces an expression, if possible.
	 *
	 * @param expression The expression to reduce.
	 */
	public Expression reduceExpression(Expression expression) {
		if (m_isEnabled) {
			if (expression instanceof Binary) {
				Binary binary = (Binary) expression;
				if (binary.canBeFolded()) {
					try {
						ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
						ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("JavaScript");
						SemanticValidator.getInstance().validateDivisionAndModByZero(binary);
						String binaryReduction = binary.toString();
						Double reduction = (Double) scriptEngine.eval(binaryReduction);
						return new Number(new Constant((reduction.intValue())));
					} catch (ScriptException e) {
						return expression;
					}
				} else {
					return expression;
				}
			} else {
				return expression;
			}
		}
		return expression;
	}
}


