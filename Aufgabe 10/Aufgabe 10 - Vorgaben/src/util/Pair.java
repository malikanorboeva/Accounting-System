package util;


public class Pair<U,V> {

	public final U first;
	public final V second;

	public Pair(U first, V second) {
		this.first = first;
		this.second = second;
	}

	public U getFirst() {
		return first;
	}

	public V getSecond() {
		return second;
	}

	@Override
	public String toString() {
		return "(" + first + ", " + second + ")";
	}

	public U getKey() {
		return getFirst();
	}

	public V getValue() {
		return getSecond();
	}
}
