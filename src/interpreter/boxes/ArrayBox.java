package interpreter.boxes;

import parser.semanticAnalysis.symbolTable.declarations.types.Integer;
import parser.semanticAnalysis.symbolTable.declarations.types.Type;
import parser.semanticAnalysis.symbolTable.declarations.types.array.Array;
import parser.semanticAnalysis.symbolTable.declarations.types.record.Record;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an array of boxes.
 */
public class ArrayBox extends Box {

	/**
	 * The array of boxes.
	 */
	private List<Box> m_boxes;
	/**
	 * The type of the array.
	 */
	private Type m_type;

	/**
	 * Constructs an array box.
	 *
	 * @param size The size of the array.
	 * @param type The type of the array.
	 */
	public ArrayBox(int size, Type type) {
		m_boxes = new ArrayList<Box>();
		if (type instanceof Integer) {
			for (int i = 0; i < size; i++) {
				m_boxes.add(new IntegerBox());
			}
		} else if (type instanceof Record) {
			Record record = (Record) type;
			for (String variable : record.getScope().getMap().keySet()) {
				if (record.getScope().find(variable) instanceof Record) {
					m_boxes.add(new RecordBox((Record) record.getScope().find(variable)));
				} else {
					Type type1 = ((parser.semanticAnalysis.symbolTable.declarations.variable.Variable) (record.getScope().find(variable))).getType();
					if (type1 instanceof Array) {
						Array array = (Array) type1;
						m_boxes.add(new ArrayBox(array.getLength(), array.getElementType()));
					} else if (type1 instanceof Integer) {
						m_boxes.add(new IntegerBox());
					} else if (type1 instanceof Record) {
						m_boxes.add(new RecordBox((Record) (type1)));
					}
				}
			}
		} else if (type instanceof Array) {
			Array array = (Array) type;
			for (int i = 0; i < size; i++) {
				m_boxes.add(new ArrayBox(array.getLength(), array.getElementType()));
			}
		}
	}

	/**
	 * Clones the box.
	 */
	@Override
	public Box clone() {
		ArrayBox arrayBox = new ArrayBox(m_boxes.size(), m_type);
		List<Box> boxes = new ArrayList<Box>();
		for (int i = 0; i < m_boxes.size(); i++) {
			boxes.add(m_boxes.get(i).clone());
		}
		arrayBox.m_boxes = boxes;
		return arrayBox;
	}

	/**
	 * Clones an array box.
	 *
	 * @param arrayBox The array box to clone.
	 */
	public void clone(ArrayBox arrayBox) {
		m_boxes = arrayBox.getBoxes();
		m_type = arrayBox.getType();
	}

	/**
	 * Gets the type of the array box.
	 */
	public Type getType() {
		return m_type;
	}

	/**
	 * Gets the boxes of the array box.
	 */
	public List<Box> getBoxes() {
		return m_boxes;
	}
}
