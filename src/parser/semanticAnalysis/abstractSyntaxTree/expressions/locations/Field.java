package parser.semanticAnalysis.abstractSyntaxTree.expressions.locations;

import parser.semanticAnalysis.abstractSyntaxTree.expressions.Expression;
import parser.semanticAnalysis.symbolTable.declarations.types.Type;

/**
 * Represents a field within a record.
 */
public class Field extends Location {

	private Location m_variable;

	private Location m_selection;

	public Field(Location variable, Location selection, Type type) {
		super(type);
		m_variable = variable;
		m_selection = selection;
	}

	public Location getVariable() {
		return m_variable;
	}

	public Location getSelection() {
		return m_selection;
	}

	public String toString() {
		return m_variable.toString() + "." + m_selection.toString();
	}

	/**
	 * Clones the expression.
	 */
	@SuppressWarnings("CloneDoesntCallSuperClone")
	@Override
	public Expression clone() {
		return new Field((Location) m_variable.clone(), (Location) m_selection.clone(), getType());
	}

	// replace all contents in [..] with 0, calculate offset!
}
