package persistent.array;

import persistent.PList;

/**
 * Braun tree
 * 
 * @author morrisy
 *
 * @param <T> The type of elements held in this collection
 */
public class BraunList<T> extends PList<T> {
	@SuppressWarnings("rawtypes")
	/** The empty list. */
	private static final BraunList<?> EMPTY = new BraunList();

	/**
	 * Returns an empty list.
	 * 
	 * @param <T> The type of elements held in this collection
	 * @return An empty list.
	 */
	@SuppressWarnings("unchecked")
	public static <T> BraunList<T> create() {
		return (BraunList<T>) EMPTY;
	}

	private final int size;
	private final Node<T> root;

	private BraunList() {
		this(null, 0);
	}

	private BraunList(Node<T> root, int size) {
		this.root = root;
		this.size = size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public T get(int index) {
		if (index < 0 || index >= size)
			throw new IndexOutOfBoundsException();
		Node<T> u = root;
		index++;
		while (index > 0) {
			if (index == 1)
				return u.value;
			if ((index & 1) == 0) {
				u = u.lson;
			} else {
				u = u.rson;
			}
			index >>= 1;
		}
		assert false;
		return null;
	}

	@Override
	public PList<T> set(int index, T value) {
		if (index < 0 || index >= size)
			throw new IndexOutOfBoundsException();
		return new BraunList<>(set(index + 1, value, root), size);
	}

	private Node<T> set(int index, T value, Node<T> u) {
		if (index == 1) {
			if (u != null)
				return new Node<>(u.lson, u.rson, value);
			else
				return new Node<>(null, null, value);
		}

		if ((index & 1) == 0) {
			return new Node<>(set(index >> 1, value, u.lson), u.rson, u.value);
		} else {
			return new Node<>(u.lson, set(index >> 1, value, u.rson), u.value);
		}
	}

	@Override
	public PList<T> pushBack(T value) {
		if (size == 0)
			return new BraunList<>(new Node<>(null, null, value), 1);
		return new BraunList<>(insert(size + 1, value, root), size + 1);
	}

	private Node<T> insert(int index, T value, Node<T> u) {
		if (index == 1)
			return new Node<>(null, null, value);
		if ((index & 1) == 0) {
			return new Node<>(insert(index >> 1, value, u.lson), u.rson, u.value);
		} else {
			return new Node<>(u.lson, insert(index >> 1, value, u.rson), u.value);
		}
	}

	@Override
	public PList<T> popBack() {
		if (size == 1)
			return create();
		return new BraunList<>(remove(size + 1, root), size - 1);
	}

	private Node<T> remove(int index, Node<T> u) {
		if (index == 1)
			return null;
		if ((index & 1) == 0) {
			return new Node<>(remove(index >> 1, u.lson), u.rson, u.value);
		} else {
			return new Node<>(u.lson, remove(index >> 1, u.rson), u.value);
		}
	}

	private static class Node<T> {
		private final Node<T> lson;
		private final Node<T> rson;
		private final T value;

		Node(Node<T> lson, Node<T> rson, T value) {
			this.lson = lson;
			this.rson = rson;
			this.value = value;
		}
	}
}
