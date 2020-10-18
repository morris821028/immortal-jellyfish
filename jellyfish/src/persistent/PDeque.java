package persistent;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A linear collection that supports element insertion and removal at both ends.
 * The name <i>deque</i> is short for "double ended queue" and is usually
 * pronounced "deck". Most <tt>Deque</tt> implementations is immutable by
 * insertion and removal, but this interface supports immutable deque.
 * 
 * @author morrisy
 *
 * @param <T> the type of elements held in this collection
 */
public abstract class PDeque<T> implements Iterable<T> {
	public abstract boolean isEmpty();

	public abstract int size();

	public abstract T front();

	public abstract T back();

	public abstract PDeque<T> pushFront(T value);

	public abstract PDeque<T> pushBack(T value);

	public abstract PDeque<T> popFront();

	public abstract PDeque<T> popBack();

	/**
	 * Returns an iterator over the elements in this deque from front to back.
	 * 
	 * @return an iterator over the elements in this deque from front to back.
	 */
	@Override
	public Iterator<T> iterator() {
		return new QueueIterator<>(this);
	}

	/**
	 * Returns an iterator over the elements in this deque from front to back.
	 * 
	 * @return an iterator over the elements in this deque from front to back.
	 */
	public Iterator<T> descendingIterator() {
		return new DescendQueueIterator<>(this);
	}

	static class QueueIterator<T> implements Iterator<T> {
		private PDeque<T> current;

		public QueueIterator(PDeque<T> topmost) {
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
			current = current.popFront();
			return val;
		}
	}

	static class DescendQueueIterator<T> implements Iterator<T> {
		private PDeque<T> current;

		public DescendQueueIterator(PDeque<T> topmost) {
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
			T val = current.back();
			current = current.popBack();
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
}
