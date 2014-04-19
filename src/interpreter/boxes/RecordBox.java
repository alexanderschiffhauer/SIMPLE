package interpreter.boxes;

import parser.semanticAnalysis.symbolTable.declarations.types.Integer;
import parser.semanticAnalysis.symbolTable.declarations.types.Type;
import parser.semanticAnalysis.symbolTable.declarations.types.array.Array;
import parser.semanticAnalysis.symbolTable.declarations.types.record.Record;
import parser.semanticAnalysis.symbolTable.declarations.variable.Variable;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a record, which contains many boxes.
 */
public class RecordBox extends Box {

	/**
	 * The record of the box.
	 */
	private Record m_record;
	/**
	 * Represents a map of field names to boxes.
	 */
	private Map<String, Box> m_map;

	/**
	 * Constructs a new record box.
	 *
	 * @param record The record with which to use for construction.
	 */
	public RecordBox(Record record) {
		copy(record);
	}

	/**
	 * Copies a record's contents.
	 *
	 * @param record The record whose data will be copied into this record.
	 */
	public void copy(Record record) {
		m_map = new HashMap<String, Box>();
		m_record = record;
		for (String field : record.getScope().getMap().keySet()) {
			Variable variable = (Variable) record.getScope().find(field);
			Type type = variable.getType();
			Box box = null;
			if (type instanceof Array) {
				Array array = (Array) type;
				box = new ArrayBox(array.getLength(), array.getElementType());
			} else if (type instanceof Record) {
				Record record1 = (Record) type;
				box = new RecordBox(record1);
			} else if (type instanceof Integer) {
				box = new IntegerBox();
			}
			m_map.put(field, box);
		}
	}

	/**
	 * Gets the map of the record box.
	 */
	public Map<String, Box> getMap() {
		return m_map;
	}

	/**
	 * Clones the box.
	 */
	@Override
	public Box clone() {
		RecordBox recordBox = new RecordBox(m_record);
		Map<String, Box> map = new HashMap<String, Box>();
		for (String key : m_map.keySet()) {
			map.put(key, m_map.get(key).clone());
		}
		recordBox.m_map = map;
		return recordBox;
	}

	/**
	 * Clones a record box.
	 *
	 * @param recordBox The record box to clone.
	 */
	public void clone(RecordBox recordBox) {
		m_map = recordBox.m_map;
		m_record = recordBox.m_record;
	}
}
