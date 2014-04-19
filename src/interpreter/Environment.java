package interpreter;

import interpreter.boxes.ArrayBox;
import interpreter.boxes.Box;
import interpreter.boxes.IntegerBox;
import interpreter.boxes.RecordBox;
import parser.semanticAnalysis.symbolTable.declarations.Declaration;
import parser.semanticAnalysis.symbolTable.declarations.types.Integer;
import parser.semanticAnalysis.symbolTable.declarations.types.Type;
import parser.semanticAnalysis.symbolTable.declarations.types.array.Array;
import parser.semanticAnalysis.symbolTable.declarations.types.record.Record;
import parser.semanticAnalysis.symbolTable.declarations.variable.Variable;
import parser.semanticAnalysis.symbolTable.scope.Scope;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Represents a run-time environment of variable names to boxes.
 */
public class Environment {

	/**
	 * The singleton instance of the environment.
	 */
	private static Environment s_instance;
	/**
	 * The maps of variables to boxes.
	 */
	private Stack<Map<String, Box>> m_maps;

	/**
	 * Constructs the environment.
	 */
	private Environment() {
		m_maps = new Stack<Map<String, Box>>();
	}

	/**
	 * Gets the singleton instance of the environment.
	 */
	public static Environment getInstance() {
		if (s_instance == null) {
			s_instance = new Environment();
		}
		return s_instance;
	}

	/**
	 * Finds a box from a string, recursively getting values.
	 *
	 * @param value The value to get.
	 */
	public Box find(String value) {
		if (m_maps.size() - 1 >= 0) {
			for (int i = m_maps.size() - 1; i > -1; i--) {
				if (m_maps.get(i).get(value) != null) {
					return m_maps.get(i).get(value);
				}
			}
		}
		return null;
	}

	/**
	 * Merges a map with the current environment by replacement.
	 *
	 * @param map The map, whose values will replace the existing.
	 */
	public void replace(LinkedHashMap<String, Box> map) {
		for (String key : map.keySet()) {
			m_maps.peek().put(key, map.get(key));
		}
	}

	/**
	 * Environmentalizes the symbol table.
	 *
	 * @param scope The scope to environmentalize.
	 */
	public void environmentalize(Scope scope) {
		HashMap<String, Box> map = new HashMap<String, Box>();
		for (Declaration declaration : scope.getMap().values()) {
			if (declaration instanceof Variable) {
				Type type = ((Variable) declaration).getType();
				if (type instanceof Array) {
					Array array = (Array) type;
					map.put(declaration.getName(), new ArrayBox(array.getLength(), array.getElementType()));
				} else if (type instanceof Record) {
					Record record = (Record) type;
					map.put(declaration.getName(), new RecordBox(record));
				} else if (type instanceof Integer) {
					map.put(declaration.getName(), new IntegerBox());
				}
			}
		}
		m_maps.push(map);
	}

	/**
	 * Pops the current environment..
	 */
	public void pop() {
		m_maps.pop();
	}
}
