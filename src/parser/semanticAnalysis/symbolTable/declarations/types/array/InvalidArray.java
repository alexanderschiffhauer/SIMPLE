package parser.semanticAnalysis.symbolTable.declarations.types.array;

import parser.semanticAnalysis.symbolTable.declarations.InvalidDeclaration;
import parser.semanticAnalysis.symbolTable.declarations.types.InvalidType;

/**
 * Represents an invalid array (i.e. an array that was built because something went wrong).
 */
public class InvalidArray extends Array {

	/**
	 * Constructs a new invalid array.
	 */
	public InvalidArray() {
		InvalidDeclaration.initializeInvalidDeclaration(this);
		setElementType(new InvalidType());
		setLength(Integer.MAX_VALUE);
	}

	/**
	 * Constructs a new invalid array.
	 *
	 * @param array The array from which to retrieve information.
	 */
	public InvalidArray(Array array) {
		this();
		InvalidDeclaration.initializeInvalidDeclaration(this, array);
		if (array.getElementType() != null) {
			setElementType(array.getElementType());
		}
		if (array.getLength() != Integer.MAX_VALUE) {
			setLength(array.getLength());
		}
	}
}
