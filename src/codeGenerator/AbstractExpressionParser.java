package codeGenerator;

import parser.semanticAnalysis.abstractSyntaxTree.expressions.Expression;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Location;

/**
 * The base class for parsing expressions for AMD64.
 */
public abstract class AbstractExpressionParser extends AbstractAssemblyCodePrinter {

	/**
	 * Gets the value from a location, returning either the mangled name of the variable for use in assembly or the stringified representation of the register that contains the value of the variable.
	 *
	 * @param location The location whose value will be returned.
	 */
	public abstract String getLocationValue(Location location);

	/**
	 * Gets the address from a location.  The address is guaranteed to be in R15 if using a non-optimized code generator.
	 *
	 * @param location The location whose address will be returned.
	 */
	public abstract String getLocationAddress(Location location);

	/**
	 * Gets the value from a location, returning either the mangled name of the variable for use in assembly or the stringified representation of R14 if using a non-optimized code generator, which contains the value of the expression.
	 *
	 * @param expression The expression whose value will be returned.
	 */
	public abstract String getExpressionValue(Expression expression);

	/**
	 * Gets the offset from a location.  The offset is guaranteed to be in R15 if using a non-optimized code generator.
	 *
	 * @param location The location whose offset will be returned.
	 */
	protected abstract String getLocationOffset(Location location);

	/**
	 * Gets the value from the stringified representation of a binary expression, putting the result in R13 if using a non-optimized code generator.
	 *
	 * @param binary The binary expression whose value will be returned.
	 */
	protected abstract String parseBinaryExpression(String binary);

	/**
	 * Gets the left operand from a string.
	 *
	 * @param string The string from which to get the operand.
	 */
	protected String getLeftOperand(String string) {
		if (getIndexOfOperator(string) == -1) {
			return string;
		}
		while (getIndexOfOperator(string) != -1) {
			string = string.substring(getIndexOfOperator(string));
		}
		return string;
	}

	/**
	 * Gets the right operand from a string.
	 *
	 * @param string The string from which to get the operand.
	 */
	protected String getRightOperand(String string) {
		if (getIndexOfOperator(string) != -1) {
			string = string.substring(0, getIndexOfOperator(string));
		}
		return string;
	}

	/**
	 * Queries if a string has an operator.
	 *
	 * @param string The string to query.
	 */
	protected int getIndexOfOperator(String string) {
		int index;
		if ((index = string.indexOf("+")) != -1) {
			return index;
		} else if ((index = string.indexOf("-")) != -1) {
			return index;
		} else if ((index = string.indexOf("*")) != -1) {
			return index;
		} else if ((index = string.indexOf("/")) != -1) {
			return index;
		} else if ((index = string.indexOf("%")) != -1) {
			return index;
		} else {
			return -1;
		}
	}

	/**
	 * Queries if a string is numeric.
	 *
	 * @param string The string to query.
	 */
	protected boolean isNumeric(String string) {
		try {
			double number = Double.parseDouble(string);
			return true;
		} catch (Exception ignored) {
			return false;
		}
	}
}
