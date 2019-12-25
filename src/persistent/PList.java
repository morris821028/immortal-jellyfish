package persistent;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Work like {@literal C++ std::vector}
 * 
 * @author morrisy
 *
 * @param <T> The type of element
 */
public abstract class PList<T> implements Iterable<T> {
	public abstract boolean isEmpty();

	public abstract int size();

	/**
	 * Returns the element at the specified position in this list.
	 * 
	 * @param index index of the element to return
	 * @return Returns the element at the specified position in this list.
	 */
	public abstract T get(int index);

	public abstract PList<T> set(int index, T value);

	/**
	 * Appends the specified element to the end of this list.
	 * 
	 * @param value element to be append to this list
	 * @return The changed array list
	 */
	public abstract PList<T> pushBack(T value);

	public abstract PList<T> popBack();

	/**
	 * Returns an iterator over the elements in this array from index 0 to size.
	 * 
	 * @return an iterator over the elements in this array from index 0 to size.
	 */
	@Override
	public Iterator<T> iterator() {
		return new ListIterator(this);
	}

	class ListIterator implements Iterator<T> {
		private PList<T> current;
		private int index;

		public ListIterator(PList<T> array) {
			current = array;
		}

		@Override
		public boolean hasNext() {
			return index < current.size();
		}

		@Override
		public T next() {
			if (!hasNext())
				throw new NoSuchElementException();
			T val = current.get(index);
			index++;
			return val;
		}
	}
}
