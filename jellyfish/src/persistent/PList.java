package persistent;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

import persistent.array.TreeList;

/**
 * Work like {@literal C++ std::vector}
 * 
 * @author morrisy
 *
 * @param <T> The type of element
 */
public abstract class PList<T> implements Iterable<T> {
	/**
	 * Returns <tt>true</tt> if this list contains no elements.
	 *
	 * @return <tt>true</tt> if this list contains no elements
	 */
	public abstract boolean isEmpty();

	/**
	 * Returns the number of elements in this list.
	 *
	 * @return the number of elements in this list
	 */
	public abstract int size();

	/**
	 * Returns the element at the specified position in this list.
	 * 
	 * @param index index of the element to return
	 * @return the element at the specified position in this list.
	 * 
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *                                   (<tt>index &lt; 0 || index &gt;= size()</tt>)
	 */
	public abstract T get(int index);

	/**
	 * Replaces the element at the specified position in this list with the
	 * specified element.
	 * 
	 * @param index index of the element to replace
	 * @param value element to be stored at the specified position
	 * @return the new array contains current specified element and remaining
	 *         elements.
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *                                   (<tt>index &lt; 0 || index &gt;= size()</tt>)
	 */
	public abstract PList<T> set(int index, T value);

	/**
	 * Appends the specified element to the end of this list.
	 * 
	 * @param value element to be append to this list
	 * @return the new array, which appends the specified element.
	 */
	public abstract PList<T> pushBack(T value);

	/**
	 * Removes the element at the last position in this list.
	 * 
	 * @return the new array, which removes last element.
	 * @throws NoSuchElementException if the size if list is empty.
	 */
	public abstract PList<T> popBack();

	/**
	 * Returns an iterator over the elements in this array from index 0 to size.
	 * 
	 * @return an iterator over the elements in this array from index 0 to size.
	 */
	@Override
	public Iterator<T> iterator() {
		return new ListIterator<>(this);
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

	/**
	 * Compares the specified object with this list for equality. 
	 */
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof PList))
			return false;
		PList<?> otherList = (PList<?>) other;
		if (otherList.size() != this.size())
			return false;
		Iterator<?> itr = this.iterator();
		Iterator<?> jtr = otherList.iterator();
		while (itr.hasNext()) {
			if (!Objects.equals(itr.next(), jtr.next()))
				return false;
		}
		return !itr.hasNext() && !jtr.hasNext();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		int hash = 0;
		for (T e : this)
			hash = Objects.hash(hash, e);
		return hash;
	}

	static class ListIterator<T> implements Iterator<T> {
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

	public static <T> PList<T> of(T value) {
		return TreeList.<T>create().pushBack(value);
	}
}
