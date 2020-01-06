package persistent;

import java.util.NoSuchElementException;

/**
 * Fully flexible array, different from {@link PList} which is one-sided
 * flexible arrays. A collection supports
 * 
 * @author morrisy
 *
 * @param <T> the type of elements held in this collection
 */
public abstract class PFullyList<T> extends PList<T> {
	/**
	 * Concatenated the specified element to the begin of this list.
	 * 
	 * @param value element to be concatenated to this list
	 * @return the new array, which concatenate the specified element.
	 */
	public abstract PFullyList<T> pushFront(T value);

	/**
	 * Removes the element at the first position in this list.
	 * 
	 * @return the new array, which removes first element.
	 * @throws NoSuchElementException if the size if list is empty.
	 */
	public abstract PFullyList<T> popFront();

	/**
	 * Appends the specified element to the end of this list.
	 * 
	 * @param value element to be append to this list
	 * @return the new array, which appends the specified element.
	 */
	public abstract PFullyList<T> pushBack(T value);

	/**
	 * Removes the element at the last position in this list.
	 * 
	 * @return the new array, which removes last element.
	 * @throws NoSuchElementException if the size if list is empty.
	 */
	public abstract PFullyList<T> popBack();

	public abstract T front();

	public abstract T back();
}
