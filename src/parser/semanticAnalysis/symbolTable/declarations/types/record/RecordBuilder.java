package parser.semanticAnalysis.symbolTable.declarations.types.record;

import parser.semanticAnalysis.symbolTable.declarations.DeclarationBuilder;
import parser.semanticAnalysis.symbolTable.scope.Scope;
import parser.semanticAnalysis.symbolTable.scope.ScopeManager;

import java.util.Stack;

/**
 * Builds records.
 */
public class RecordBuilder extends DeclarationBuilder<Record> {

	/**
	 * Represents the instance of the record builder.
	 */
	private static RecordBuilder m_instance;
	/**
	 * Represents the stack of scopes being built for each record.
	 */
	private Stack<Scope> m_stack;

	/**
	 * Constructs the record builder.
	 */
	private RecordBuilder() {
		m_stack = new Stack<Scope>();
	}

	/**
	 * Gets the instance of the record builder.
	 */
	public static RecordBuilder getInstance() {
		if (m_instance == null) {
			m_instance = new RecordBuilder();
		}
		return m_instance;
	}

	/**
	 * Starts building a new declaration.
	 */
	@Override
	public void startBuilding() {
		if (m_isEnabled) {
			if (m_declaration == null) {
				m_declaration = new Record();
			}
			m_stack.add(ScopeManager.getInstance().getForwardScope());
		}
	}

	/**
	 * Finishes building a declaration and gets the finished product.
	 */
	@Override
	public Record getDeclaration() {
		if (m_isEnabled) {
			m_declaration.setScope(m_stack.pop());
			ScopeManager.getInstance().removeOuterScope();

			Record record = m_declaration;
			m_declaration = new Record();

			return record;
		}
		return new InvalidRecord();
	}

	/**
	 * Invalidates the declaration, such that any declaration returned will be invalid until the builder is reset.
	 */
	@Override
	public void invalidate() {
		if (m_isEnabled) {
			m_declaration = new InvalidRecord(m_declaration);
		}
	}

	/**
	 * Resets the builder.
	 */
	@Override
	public void reset() {}
}
