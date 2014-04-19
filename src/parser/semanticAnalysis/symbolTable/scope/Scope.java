package parser.semanticAnalysis.symbolTable.scope;

import parser.semanticAnalysis.SemanticValidator;
import parser.semanticAnalysis.symbolTable.declarations.Declaration;
import parser.semanticAnalysis.symbolTable.declarations.InvalidDeclaration;

import java.util.LinkedHashMap;

/**
 * Represents the scope of a symbol table that keeps track of declarations.
 */
public class Scope {

	/**
	 * Represents the symbol table of declarations-to-values.
	 */
	LinkedHashMap<String, Declaration> m_map;
	/**
	 * Represents this scope's outer scope.
	 */
	private Scope m_outerScope;


	/**
	 * Constructs a new scope.
	 *
	 * @param outerScope The scope's outer scope.
	 */
	public Scope(Scope outerScope) {
		this();
		m_outerScope = outerScope;
	}

	/**
	 * Constructs a new scope.
	 */
	public Scope() {
		m_map = new LinkedHashMap<String, Declaration>();
	}

	/**
	 * Inserts a declaration into the symbol table.
	 *
	 * @param value       The value to insert.
	 * @param declaration The declaration to insert.
	 */
	public void insert(String value, Declaration declaration) {
		if (SemanticValidator.getInstance().isEnabled() && SemanticValidator.getInstance().validateDeclarationWasNotPreviouslyDeclared(declaration)) {
			m_map.put(value, declaration);
		}
	}

	/**
	 * Finds a declaration's value.
	 *
	 * @param name The declaration's name.
	 */
	public Declaration find(String name) {
		if (isLocal(name)) {
			return m_map.get(name);
		} else if (m_outerScope != null) {
			return m_outerScope.find(name);
		} else {
			return new InvalidDeclaration(name);
		}
	}

	/**
	 * Queries if the declaration is in this scope.
	 *
	 * @param name The name of the declaration for which to test this condition.
	 */
	public boolean isLocal(String name) {
		return m_map.containsKey(name);
	}

	/**
	 * Removes this scope's outer scope.
	 */
	void removeOuterScope() {
		m_outerScope = null;
	}

	/**
	 * Gets the map of declarations for this scope.
	 */
	public LinkedHashMap<String, Declaration> getMap() {
		return m_map;
	}
}
