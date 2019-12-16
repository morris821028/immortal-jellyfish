package persistent.stack;

import persistent.PStack;

/**
 * @author morrisy
 *
 * @param <T> The type of element
 */
public class PersistStack<T> implements PStack<T> {
	@SuppressWarnings("rawtypes")
	private static final PersistStack<?> EMPTY = new PersistStack();

	@SuppressWarnings("unchecked")
	public static <T> PersistStack<T> create() {
		return (PersistStack<T>) EMPTY;
	}

	private final T value;
	private final PersistStack<T> next;
	private final int size;

	private PersistStack() {
		this(null, null, 0);
	}

	private PersistStack(T value, PersistStack<T> next, int size) {
		this.value = value;
		this.next = next;
		this.size = size;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public int size() {
		return size;
	}

	public PersistStack<T> clear() {
		return create();
	}

	public T top() {
		if (isEmpty())
			return null;
		return value;
	}

	public PersistStack<T> push(T value) {
		return new PersistStack<>(value, this, size + 1);
	}

	public PersistStack<T> pop() {
		return next != null ? next : create();
	}
}