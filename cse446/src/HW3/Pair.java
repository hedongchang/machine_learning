package HW3;

/**
 * A pair of values
 * @author Dongchang
 *
 * @param <A> the first value
 * @param <B> the second value
 */
public class Pair<A, B> {
	public A a;
	public B b;
	
	/**
	 * Construct a pair of values
	 * @param a the first value
	 * @param b the second value
	 */
	public Pair(A a, B b) {
	  this.a = a;
	  this.b = b;
	}
}