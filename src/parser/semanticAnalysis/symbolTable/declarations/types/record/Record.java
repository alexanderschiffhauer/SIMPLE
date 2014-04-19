package parser.semanticAnalysis.symbolTable.declarations.types.record;

import parser.semanticAnalysis.symbolTable.declarations.types.Type;
import parser.semanticAnalysis.symbolTable.declarations.variable.Variable;
import parser.semanticAnalysis.symbolTable.scope.Scope;

/**
 * Represents the record type.
 */
public class Record extends Type {

	/**
	 * Represents the scope to which this record belongs.
	 */
	private Scope m_scope;

	/**
	 * Gets the scope to which this record belongs.
	 */
	public Scope getScope() {
		return m_scope;
	}

	/**
	 * Sets the scope to which this record belongs.
	 *
	 * @param scope The scope to which this record belongs.
	 */
	void setScope(Scope scope) {
		m_scope = scope;
		setSize();
	}

	/**
	 * Sets the size of the type in memory.
	 */
	@Override
	protected void setSize() {
		m_size = 0;
		if (m_scope != null) {
			for (String variableName : m_scope.getMap().keySet()) {
				Variable variable = (Variable) m_scope.find(variableName);
				m_size += variable.getType().getSize();
			}
		}
	}
}
