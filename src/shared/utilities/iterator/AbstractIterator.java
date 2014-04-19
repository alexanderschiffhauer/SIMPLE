package shared.utilities.iterator;

/**
 * Implements shared functionality for an iterator.
 */
@SuppressWarnings("all")
abstract public class AbstractIterator<V> implements Iterator<V> {

	/**
	 * Represents the index.
	 */
	protected int m_index;

	/**
	 * Constructs an abstract iterator.
	 */
	public AbstractIterator() {
		m_index = -1;
	}

	/**
	 * Gets the index.
	 */
	@Override
	public int getIndex() {
		return m_index;
	}

	/**
	 * Sets the index;
	 *
	 * @param index The index.
	 */
	@Override
	public void setIndex(int index) {
		m_index = index;
	}

	/**
	 * Queries whether there exists a next value.
	 */
	@Override
	abstract public boolean hasNext();

	/**
	 * Advances index, returning the next object.
	 */
	@Override
	abstract public V getNext();

	/**
	 * Gets the current object without advancing the index.
	 */
	@Override
	abstract public V getCurrent();

	/**
	 * Returns the next object without advancing the index.
	 */
	@Override
	public V peek() {
		V value = getNext();
		m_index--;
		return value;
	}

	/**
	 * Returns the next object without advancing the index.
	 *
	 * @param The number of steps to peek forward.
	 */
	public V peek(int steps) {
		V value = getCurrent();
		for (int i = 0; i < steps; i++) {
			value = getNext();
		}
		m_index = m_index - steps;
		return value;
	}
}
