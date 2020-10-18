package persistent.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Support two iterator concatenation.
 */
public class ConcatenatedIterator<T> implements Iterator<T> {
	public static <T> Iterator<T> create(Iterator<T> a, Iterator<T> b) {
		if (!a.hasNext())
			return b;
		else if (!b.hasNext())
			return a;
		else
			return new ConcatenatedIterator<>(a, b);
	}

	private Iterator<T> a;
	private Iterator<T> b;

	private ConcatenatedIterator(Iterator<T> a, Iterator<T> b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public boolean hasNext() {
		return a.hasNext() || b.hasNext();
	}

	@Override
	public T next() {
		if (!hasNext())
			throw new NoSuchElementException();
		T val = a.hasNext() ? a.next() : b.next();
		return val;
	}
}
