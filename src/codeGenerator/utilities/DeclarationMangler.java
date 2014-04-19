package codeGenerator.utilities;

import parser.semanticAnalysis.symbolTable.declarations.types.Integer;
import parser.semanticAnalysis.symbolTable.declarations.types.array.Array;
import parser.semanticAnalysis.symbolTable.declarations.types.record.Record;

import java.util.ArrayList;
import java.util.List;

/**
 * Mangles declarations for use in assembly.
 */
public class DeclarationMangler {

	/**
	 * Represents the instance of the declaration mangler.
	 */
	private static DeclarationMangler m_instance;

	/**
	 * Constructs the declaration mangler.
	 */
	private DeclarationMangler() {
	}

	/**
	 * Gets the instance of the declaration mangler.
	 */
	public static DeclarationMangler getInstance() {
		if (m_instance == null) {
			m_instance = new DeclarationMangler();
		}
		return m_instance;
	}

	/**
	 * Mangles an array.
	 *
	 * @param array The array to mangle.
	 */
	public List<String> mangle(Array array) {
		List<String> mangled = new ArrayList<String>();
		for (int i = 0; i < array.getLength(); i++) {
			if (array.getElementType() instanceof Array) {
				for (String nestedArray : mangle((Array) array.getElementType())) {
					mangled.add("_" + i + nestedArray);
				}
			} else if (array.getElementType() instanceof Record) {
				for (String nestedRecord : mangle((Record) array.getElementType())) {
					mangled.add("_" + i + nestedRecord);
				}
			} else if (array.getElementType() instanceof Integer) {
				mangled.add("_" + i);
			}
		}
		return mangled;
	}

	/**
	 * Mangles a record.
	 *
	 * @param record The record to mangle.
	 */
	public List<String> mangle(Record record) {
		List<String> mangled = new ArrayList<String>();
		for (String field : record.getScope().getMap().keySet()) {
			parser.semanticAnalysis.symbolTable.declarations.variable.Variable variable = (parser.semanticAnalysis.symbolTable.declarations.variable.Variable) record.getScope().find(field);
			if (variable.getType() instanceof Array) {
				for (String array : mangle((Array) variable.getType())) {
					mangled.add("." + field + array);
				}
			} else if (variable.getType() instanceof Record) {
				for (String nestedRecord : mangle((Record) variable.getType())) {
					mangled.add("." + field + nestedRecord);
				}
			} else if (variable.getType() instanceof Integer) {
				mangled.add("." + field);
			}
		}
		return mangled;
	}
}
