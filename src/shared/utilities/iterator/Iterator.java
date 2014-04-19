package shared.utilities.iterator;

/**
 * Iterates over a collection of values.
 *
 * @param <V>
 */
@SuppressWarnings("all")
public interface Iterator<V> {

	/**
	 * Gets the index.
	 */
	public int getIndex();

	/**
	 * Sets the index;
	 *
	 * @param index The index.
	 */
	public void setIndex(int index);

	/**
	 * Queries whether there exists a next value.
	 */
	public boolean hasNext();

	/**
	 * Advances index, returning the next object.
	 */
	public V getNext();

	/**
	 * Gets the current object without advancing the index.
	 */
	public V getCurrent();

	/**
	 * Returns the next object without advancing the index.
	 */
	public V peek();

	/**
	 * Returns the next object without advancing the index.
	 *
	 * @param The number of steps to peek forward.
	 */
	public V peek(int steps);
}
