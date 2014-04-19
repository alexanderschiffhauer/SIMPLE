package parser.semanticAnalysis.symbolTable.declarations.types.record;

import parser.semanticAnalysis.symbolTable.declarations.InvalidDeclaration;
import parser.semanticAnalysis.symbolTable.scope.Scope;

/**
 * Represents an invalid record (i.e. a record that was built because something went wrong).
 */
public class InvalidRecord extends Record {

	/**
	 * Constructs a new invalid record.
	 */
	public InvalidRecord() {
		InvalidDeclaration.initializeInvalidDeclaration(this);
		setScope(new Scope());
	}

	/**
	 * Constructs a new invalid record.
	 *
	 * @param record The record from which to retrieve information.
	 */
	public InvalidRecord(Record record) {
		this();
		InvalidDeclaration.initializeInvalidDeclaration(this, record);
		if (record.getScope() != null) {
			setScope(record.getScope());
		}
	}
}
