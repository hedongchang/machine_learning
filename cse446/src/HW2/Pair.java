package HW2;

public class Pair<A, B> {
	public A a;
	public B b;
	
	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}
	
	@Override
	public int hashCode() {
		int a = (Integer) this.a;
		int b = (Integer) this.b;
		return (a + b) * 100000000 + (a - b);
	}
}
