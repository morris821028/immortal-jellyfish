package persistent;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import persistent.stack.PersistStack;

/**
 * Persistent/Immutable Stack
 * 
 * @author morrsy
 *
 * @param <T> The type of element
 */
public abstract class PStack<T> implements Iterable<T> {
	public abstract boolean isEmpty();

	public abstract int size();

	public abstract T top();

	public abstract PStack<T> push(T value);

	public abstract PStack<T> pop();

	/**
	 * Returns an iterator over the elements in this stack from top to bottom.
	 * 
	 * @return an iterator over the elements in this stack from top to bottom.
	 */
	@Override
	public Iterator<T> iterator() {
		return new StackIterator<>(this);
	}

	/**
	 * Returns an iterator over the elements in this stack from bottom to top.
	 * 
	 * @return an iterator over the elements in this stack from bottom to top.
	 */
	public Iterator<T> descendingIterator() {
		return toArrayDeque(this).descendingIterator();
	}

	static class StackIterator<T> implements Iterator<T> {
		private PStack<T> current;

		public StackIterator(PStack<T> topmost) {
			current = topmost;
		}

		@Override
		public boolean hasNext() {
			return !current.isEmpty();
		}

		@Override
		public T next() {
			if (!hasNext())
				throw new NoSuchElementException();
			T val = current.top();
			current = current.pop();
			return val;
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Stack {");
		sb.append("size = " + size() + ", [");
		PStack<T> u = this;
		boolean first = true;
		while (u != null && !u.isEmpty()) {
			if (!first)
				sb.append(", ");
			sb.append(u.top());
			first = false;
			u = u.pop();
		}
		sb.append("]}");
		return sb.toString();
	}

	public static <T> PStack<T> of(T value) {
		return PersistStack.<T>create().push(value);
	}

	public static <T> PStack<T> of(T bottom, T top) {
		return PersistStack.<T>create().push(bottom).push(top);
	}

	public static <T> ArrayList<T> toArrayList(PStack<T> stk) {
		ArrayList<T> ret = new ArrayList<>(stk.size());
		for (T val : stk)
			ret.add(val);
		return ret;
	}

	private static <T> ArrayDeque<T> toArrayDeque(PStack<T> stk) {
		ArrayDeque<T> ret = new ArrayDeque<>(stk.size());
		for (T val : stk)
			ret.add(val);
		return ret;
	}
}