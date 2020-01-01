package persistent;

import java.util.Iterator;
import java.util.NoSuchElementException;

import persistent.queue.RealtimeQueue;

public abstract class PQueue<T> implements Iterable<T> {
	public abstract boolean isEmpty();

	public abstract int size();

	public abstract T front();

	public abstract PQueue<T> push(T value);

	public abstract PQueue<T> pop();

	/**
	 * Returns an iterator over the elements in this queue from front to back.
	 * 
	 * @return an iterator over the elements in this queue from front to back.
	 */
	@Override
	public Iterator<T> iterator() {
		return new QueueIterator<>(this);
	}

	static class QueueIterator<T> implements Iterator<T> {
		private PQueue<T> current;

		public QueueIterator(PQueue<T> topmost) {
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
			T val = current.front();
			current = current.pop();
			return val;
		}
	}

	/**
	 * For example, <tt>{size=4, [3, 1, 4, 1]}</tt>
	 * 
	 * @return The content representation.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{");
		sb.append("size=" + size() + ", [");
		boolean first = true;
		for (T e : this) {
			if (!first)
				sb.append(", ");
			sb.append(e);
			first = false;
		}
		sb.append("]}");
		return sb.toString();
	}

	public static <T> PQueue<T> of(T value) {
		return RealtimeQueue.<T>create().push(value);
	}
}
