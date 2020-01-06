package persistent.array.fully;

import java.util.NoSuchElementException;

import persistent.PDeque;
import persistent.PFullyList;
import persistent.PList;
import persistent.util.PCollections;

/**
 * Paper: "Leaf Trees", Kaldewaij, A., & Dielissen, V. J., 1996
 * 
 * <p>
 * The balanced-tree based array list. The time complexity of each operation is
 * O(log n). After m operations, the memory consumption is at most O(m log m).
 * </p>
 * 
 * @author morrisy
 *
 * @param <T> The type of elements held in this collection
 */
public class DeTreeList<T> extends PFullyList<T> {
	@SuppressWarnings("rawtypes")
	/** The empty list. */
	private static final DeTreeList<?> EMPTY = new DeTreeList();

	/**
	 * Returns an empty list.
	 * 
	 * @param <T> The type of elements held in this collection
	 * @return An empty list.
	 */
	@SuppressWarnings("unchecked")
	public static <T> DeTreeList<T> create() {
		return (DeTreeList<T>) EMPTY;
	}

	private static class ListNode<T> {
		private final int size;
		private final Node<T> root;

		ListNode(Node<T> root, int size) {
			this.root = root;
			this.size = size;
		}

		public String toString() {
			return "" + size;
		}
	}

	private static interface Node<T> { // NOSONAR
	}

	private static final class TreeNode<T> implements Node<T> {
		private final Node<T> lson;
		private final Node<T> rson;

		TreeNode(Node<T> l, Node<T> r) {
			this.lson = l;
			this.rson = r;
		}
	}

	private static final class DataNode<T> implements Node<T> {
		private final T val;

		DataNode(T val) {
			this.val = val;
		}

		@Override
		public String toString() {
			return val == null ? "null" : val.toString();
		}
	}

	private final int size;
	private final PDeque<ListNode<T>> roots;

	private DeTreeList() {
		this(PCollections.emptyDeque(), 0);
	}

	private DeTreeList(PDeque<ListNode<T>> roots, int size) {
		this.roots = roots;
		this.size = size;
	}

	@Override
	public DeTreeList<T> pushFront(T value) {
		PDeque<ListNode<T>> u = roots;
		Node<T> data = new DataNode<>(value);

		int s = 1;
		while (!u.isEmpty()) {
			ListNode<T> v = u.front();
			if (v.size == s) {
				data = new TreeNode<>(data, v.root);
				s <<= 1;
				u = u.popFront();
			} else {
				break;
			}
		}
		u = u.pushFront(new ListNode<>(data, s));
		return new DeTreeList<>(u, size + 1);
	}

	@Override
	public DeTreeList<T> popFront() {
		if (isEmpty())
			throw new NoSuchElementException();
		if (size == 1)
			return create();
		PDeque<ListNode<T>> u = roots;
		ListNode<T> v = u.front();
		u = u.popFront();
		while (v.size > 1) {
			u = u.pushFront(new ListNode<>(((TreeNode<T>) v.root).rson, v.size >> 1));
			v = new ListNode<>(((TreeNode<T>) v.root).lson, v.size >> 1);
		}
		return new DeTreeList<>(u, size - 1);
	}

	@Override
	public DeTreeList<T> pushBack(T value) {
		PDeque<ListNode<T>> u = roots;
		Node<T> data = new DataNode<>(value);

		int s = 1;
		while (!u.isEmpty()) {
			ListNode<T> v = u.back();
			if (v.size == s) {
				data = new TreeNode<>(v.root, data);
				s <<= 1;
				u = u.popBack();
			} else {
				break;
			}
		}
		u = u.pushBack(new ListNode<>(data, s));
		return new DeTreeList<>(u, size + 1);
	}

	@Override
	public DeTreeList<T> popBack() {
		if (isEmpty())
			throw new NoSuchElementException();
		if (size == 1)
			return create();
		PDeque<ListNode<T>> u = roots;
		ListNode<T> v = u.back();
		u = u.popBack();
		while (v.size > 1) {
			u = u.pushBack(new ListNode<>(((TreeNode<T>) v.root).lson, v.size >> 1));
			v = new ListNode<>(((TreeNode<T>) v.root).rson, v.size >> 1);
		}
		return new DeTreeList<>(u, size - 1);
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
		PDeque<ListNode<T>> u = roots;
		while (!u.isEmpty()) {
			ListNode<T> v = u.front();
			if (index < v.size)
				return get(v.root, v.size >> 1, index);
			index -= v.size;
			u = u.popFront();
		}
		assert false;
		return null;
	}

	private static <T> T get(Node<T> u, int size, int index) {
		while (size > 0) {
			TreeNode<T> tn = (TreeNode<T>) u;
			if (index < size) {
				u = tn.lson;
			} else {
				u = tn.rson;
				index -= size;
			}
			size >>= 1;
		}
		assert index == 0;
		return ((DataNode<T>) u).val;
	}

	@Override
	public PList<T> set(int index, T value) {
		if (index < 0 || index >= size)
			throw new IndexOutOfBoundsException();

		return new DeTreeList<>(set(roots, index, value), size);
	}

	private static <T> PDeque<ListNode<T>> set(PDeque<ListNode<T>> u, int index, T value) {
		ListNode<T> v = u.front();
		if (index < v.size)
			return u.popFront().pushFront(new ListNode<>(setValue(v.root, v.size >> 1, index, value), v.size));
		return set(u.popFront(), index - v.size, value).pushFront(v);
	}

	private static <T> Node<T> setValue(Node<T> u, int size, int index, T value) {
		if (u instanceof DataNode) {
			assert index == 0;
			return new DataNode<>(value);
		}

		TreeNode<T> tn = (TreeNode<T>) u;
		if (index < size) {
			return new TreeNode<>(setValue(tn.lson, size >> 1, index, value), tn.rson);
		} else {
			return new TreeNode<>(tn.lson, setValue(tn.rson, size >> 1, index - size, value));
		}
	}

	@Override
	public T front() {
		if (isEmpty())
			throw new NoSuchElementException();
		return get(0);
	}

	@Override
	public T back() {
		if (isEmpty())
			throw new NoSuchElementException();
		return get(size - 1);
	}
}
