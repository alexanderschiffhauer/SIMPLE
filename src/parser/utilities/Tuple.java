package parser.utilities;

/**
 * A very simple and general-purpose tuple class, taken from http://stackoverflow.com/questions/2670982/using-tuples-in-java.
 */
public class Tuple<X, Y> {

	public final X x;
	public final Y y;

	public Tuple(X x, Y y) {
		this.x = x;
		this.y = y;
	}
}