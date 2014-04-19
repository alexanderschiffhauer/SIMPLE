package interpreter.boxes;

/**
 * Represents an integer box (i.e. holds a mutable integer value);
 */
public class IntegerBox extends Box {

	/**
	 * Represents the value of the integer.
	 */
	int m_value;

	/**
	 * Constructs a new integer box.
	 */
	public IntegerBox() {
		this(0);
	}

	/**
	 * Constructs a new integer box.
	 *
	 * @param value The value of the integer box.
	 */
	public IntegerBox(int value) {
		m_value = value;
	}

	/**
	 * Gets the value of the integer box.
	 */
	public int getValue() {
		return m_value;
	}

	/**
	 * Sets the value of the integer box.
	 *
	 * @param value The value of the integer box.
	 */
	public void setValue(int value) {
		m_value = value;
	}

	/**
	 * Clones the box.
	 */
	@Override
	public Box clone() {
		return new IntegerBox(m_value);
	}

	/**
	 * Clones a box.
	 *
	 * @param box The box to clone.
	 */
	public void clone(IntegerBox box) {
		m_value = box.getValue();
	}
}
