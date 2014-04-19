package parser.semanticAnalysis.symbolTable.scope;

import parser.semanticAnalysis.symbolTable.declarations.Declaration;
import parser.semanticAnalysis.symbolTable.declarations.types.Integer;

import java.util.*;

/**
 * Manages the initialization, instantiation, and linearity of scopes.
 */
@SuppressWarnings("all")
public class ScopeManager {

	/**
	 * Represents the instance of the scope manager.
	 */
	private static ScopeManager m_instance;
	/**
	 * Represents the list of scopes.
	 */
	private List<Scope> m_scopes;
	/**
	 * Represents the index of the scopes.
	 */
	private int m_index;

	/**
	 * Represents the list of universally defined declarations.
	 */
	public static final Map<String, Declaration> UNIVERSALLY_DEFINED_DECLARATIONS;

	/**
	 * Using the static initializer, initialize the UNIVERSALLY_DEFINED_DECLARATIONS map.
	 */
	static {
		Map<String, Declaration> universallyDefinedDeclarations = new HashMap<String, Declaration>();
		universallyDefinedDeclarations.put(Integer.getInstance().getName(), Integer.getInstance());
		UNIVERSALLY_DEFINED_DECLARATIONS = Collections.unmodifiableMap(universallyDefinedDeclarations);
	}

	/**
	 * Constructs the scope manager.
	 */
	private ScopeManager() {
		m_scopes = new ArrayList<Scope>();
		Scope universalScope = new Scope();
		for (Declaration universallyDefinedDeclaration : UNIVERSALLY_DEFINED_DECLARATIONS.values()) {
			universalScope.m_map.put(universallyDefinedDeclaration.getName(), universallyDefinedDeclaration);
		}
		m_scopes.add(universalScope);
		m_scopes.add(new Scope(universalScope));
		m_index = 1;
	}

	/**
	 * Gets the instance of the scope manager.
	 */
	public static ScopeManager getInstance() {
		if (m_instance == null) {
			m_instance = new ScopeManager();
		}
		return m_instance;
	}

	public Scope getProgramScope() {
		return m_scopes.get(1);
	}

	/**
	 * Gets the current scope.
	 */
	public Scope getCurrentScope() {
		return m_scopes.get(m_index);
	}

	/**
	 * Gets the next scope, creating a new scope, such that the previous scope is its outer scope, if no upcoming scope exists.
	 */
	public Scope getForwardScope() {
		if (m_index == m_scopes.size() - 1) {
			m_scopes.add(new Scope(m_scopes.get(m_index)));
		}
		m_index++;
		return m_scopes.get(m_index);
	}

	/**
	 * Gets the previous scope, and the program scope is the furthest one can traverse backward.
	 */
	public Scope getPreviousScope() {
		if (m_index > 1) {
			m_index--;
		}
		return m_scopes.get(m_index);
	}

	/**
	 * Attempts to remove the outer scope from the current scope.  This will only work on non-program and non-universal scopes.
	 * The previous scope becomes the current scope.
	 *
	 * @return True if successful; otherwise, false.
	 */
	public boolean removeOuterScope() {
		if (m_index > 1) {
			m_scopes.get(m_index).removeOuterScope();
			getPreviousScope();
			m_scopes.remove(m_index + 1);
			return true;
		} else {
			return false;
		}
	}
}