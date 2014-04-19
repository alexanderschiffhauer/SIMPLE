package parser.semanticAnalysis.abstractSyntaxTree.expressions;

import exception.Exception;
import exception.ExceptionHandler;
import exception.ExceptionStrength;
import parser.semanticAnalysis.abstractSyntaxTree.Node;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Field;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Index;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Location;
import parser.semanticAnalysis.abstractSyntaxTree.expressions.locations.Variable;
import parser.semanticAnalysis.symbolTable.declarations.types.Integer;
import parser.semanticAnalysis.symbolTable.declarations.types.Type;
import parser.semanticAnalysis.symbolTable.declarations.types.array.Array;

public abstract class Expression extends Node {

	/**
	 * Represents the type of the expression.
	 */
	private Type m_type;

	/**
	 * Constructs an expression.
	 *
	 * @param type The type of the expression.
	 */
	public Expression(Type type) {
		m_type = type;
	}

	/**
	 * Gets the type of the expression.
	 */
	public Type getType() {
		return m_type;
	}

	/**
	 * Clones the expression.
	 */
	public abstract Expression clone();

	/**
	 * Queries if this expression is numeric.
	 */
	public boolean isNumeric() {
		if (this instanceof Binary) {
			return true;
		} else if (this instanceof Number) {
			return true;
		} else if (this instanceof Variable) {
			return ((Variable) this).getVariable().getType().isNumeric();
		} else if (this instanceof Field) {
			return ((Field) this).getType().isNumeric();
		} else if (this instanceof Index) {
			return ((Index) this).getType().isNumeric();
		} else if (this instanceof Function) {
			return ((Function) this).getType().isNumeric();
		} else {
			return false;
		}
	}

	/**
	 * Queries if this expression is a number.  This differs from "isNumeric" in the sense that if "a" is an array of 10 integers, "a" is numeric.  However, "a" is not an integer.
	 */
	public boolean isANumber() {
		if (this instanceof Binary) {
			return true;
		} else if (this instanceof Number) {
			return true;
		} else if (this instanceof Location) {
			Location location = (Location) this;
			if (location instanceof Variable) {
				Variable variable = (Variable) location;
				if (variable.getVariable().getType() instanceof parser.semanticAnalysis.symbolTable.declarations.types.Integer) {
					return true;
				} else {
					return false;
				}
			} else if (location instanceof Field) {
				Field field = (Field) location;
				if (field.getSelection().getType() instanceof parser.semanticAnalysis.symbolTable.declarations.types.Integer) {
					return true;
				} else {
					return false;
				}
			} else if (location instanceof Index) {
				Index index = (Index) location;
				int depthCounter = 0;
				while (index.getVariable() instanceof Index) {
					index = (Index) index.getVariable();
					depthCounter++;
				}
				if (index.getVariable() instanceof Variable) {
					Variable variable = (Variable) index.getVariable();
					Array array = (Array) variable.getVariable().getType();
					for (int i = 0; i < depthCounter; i++) {
						try {
							array = (Array) array.getElementType();
						} catch (java.lang.Exception exception) {
							ExceptionHandler.getInstance().throwException(Exception.VARIABLE_IS_NOT_A_NESTED_ARRAY, ExceptionStrength.STRONG, variable.getVariable(), java.lang.Integer.toString(depthCounter + 1));
						}
					}
					if (array.getElementType() instanceof parser.semanticAnalysis.symbolTable.declarations.types.Integer) {
						return true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else if (this instanceof Function) {
			Function function = (Function) this;
			if (function.getCall().getProcedure().getReturnType() instanceof Integer) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
