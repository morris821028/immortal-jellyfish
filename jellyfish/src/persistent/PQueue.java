package persistent;

import java.util.Iterator;
import java.util.NoSuchElementException;

import persistent.PStack.StackIterator;
import persistent.queue.RealtimeQueue;

public abstract class PQueue<T> implements Iterable<T> {
	public abstract boolean isEmpty();

	public abstract int size();

	public abstract T front();

	public abstract PQueue<T> push(T value);

	public abstract PQueue<T> pop();

	/**
	 * Returns an iterator over the elements in this stack from top to bottom.
	 * 
	 * @return an iterator over the elements in this stack from top to bottom.
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

	public static <T> PQueue<T> of(T value) {
		PQueue<T> que = RealtimeQueue.create();
		return que.push(value);
	}
}
