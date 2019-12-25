package persistent.array;

import java.util.NoSuchElementException;

import persistent.PList;
import persistent.PStack;
import persistent.helper.Append;
import persistent.util.PCollections;

/**
 * <p>
 * The balanced-tree based array list. The time complexity of each operation is
 * O(log n). After m operations, the memory consumption is at most O(m log m).
 * </p>
 * 
 * @author morrisy
 *
 * @param <T> The type of elements held in this collection
 */
public class TreeList<T> extends PList<T> {
	@SuppressWarnings("rawtypes")
	/** The empty list. */
	private static final TreeList<?> EMPTY = new TreeList();

	/**
	 * Returns an empty list.
	 * 
	 * @param <T> The type of elements held in this collection
	 * @return An empty list.
	 */
	@SuppressWarnings("unchecked")
	public static <T> TreeList<T> create() {
		return (TreeList<T>) EMPTY;
	}

	/**
	 * The helper counter to record tree height. Support size from 0 to 32.
	 */
	private static final CountNode<?>[] COUNTS = CountNode.build();

	/** Pointer to tree root node. */
	private final PStack<Node<T>> rNodes;
	/** The number of elements in this list. */
	private final int size;

	private TreeList() {
		this(PCollections.emptyStack(), 0);
	}

	private TreeList(PStack<Node<T>> rNodes, int size) {
		this.rNodes = rNodes;
		this.size = size;
		assert rNodes.size() < 32;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public int size() {
		return size;
	}

	/**
	 * Returns the element at the specified position in this list.
	 * 
	 * @param index index of the element to return
	 *
	 * @return the element at the specified position in this list
	 * @throws IndexOutOfBoundsException {@inheritDoc}
	 */
	@Override
	public T get(int index) {
		if (index < 0 || index >= size)
			throw new IndexOutOfBoundsException();

		PStack<Node<T>> u = rNodes;
		int tail = size;
		while (!u.isEmpty()) {
			Node<T> v = u.top();
			if (v instanceof CountNode) {
				u = u.pop();
			} else {
				int b = v.size();
				if (index >= tail - b)
					return get(v, b - (tail - index));
				u = u.pop();
				tail -= b;
			}
		}
		assert false;
		return null;
	}

	private static <T> T get(Node<T> u, int index) {
		assert u.size() > index;
		while (u instanceof TreeNode) {
			TreeNode<T> tn = (TreeNode<T>) u;
			if (index < tn.lson.size()) {
				u = tn.lson;
			} else {
				u = tn.rson;
				index -= tn.lson.size();
			}
		}
		assert index == 0;
		return ((DataNode<T>) u).val;
	}

	@Override
	public PList<T> set(int index, T value) {
		if (index < 0 || index >= size())
			throw new IndexOutOfBoundsException();

		return new TreeList<>(setValue(rNodes, size(), index, value), size);
	}

	private static <T> PStack<Node<T>> setValue(PStack<Node<T>> u, int size, int index, T value) {
		Node<T> v = u.top();
		if (v instanceof CountNode)
			return setValue(u.pop(), size, index, value).push(v);

		int b = v.size();
		if (index >= size - b)
			return u.pop().push(setValue(v, b - (size - index), value));
		return setValue(u.pop(), size - b, index, value).push(v);
	}

	private static <T> Node<T> setValue(Node<T> u, int index, T value) {
		assert u.size() > index;
		if (u instanceof DataNode) {
			assert index == 0;
			return new DataNode<>(value);
		}

		TreeNode<T> tn = (TreeNode<T>) u;
		if (index < tn.lson.size()) {
			return new TreeNode<>(setValue(tn.lson, index, value), tn.rson);
		} else {
			return new TreeNode<>(tn.lson, setValue(tn.rson, index - tn.lson.size, value));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public PList<T> pushBack(T value) {
		Node<T> data = new DataNode<>(value);
		if (isEmpty())
			return new TreeList<>(PStack.of(data), 1);

		PStack<Node<T>> u = rNodes;
		int dep = 0;
		while (!u.isEmpty() && !(u.top() instanceof CountNode)) {
			Node<T> v = u.top();
			data = new TreeNode<>(v, data);
			u = u.pop();
			dep++;
		}

		if (u.top() instanceof CountNode) {
			CountNode<T> d = (CountNode<T>) u.top();
			if (d.size == 1) {
				u = u.pop();
			} else {
				u = u.pop().push((CountNode<T>) COUNTS[d.size - 1]);
			}
		}

		u = u.push(data);
		if (dep > 0)
			u = u.push((CountNode<T>) COUNTS[dep]);
		return new TreeList<>(u, size + 1);
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public PList<T> popBack() {
		if (isEmpty())
			throw new NoSuchElementException();
		if (size == 1)
			return create();

		PStack<Node<T>> u = rNodes;
		int dep = 0;
		if (u.top() instanceof CountNode) {
			dep = ((CountNode<T>) u.top()).size;
			u = u.pop();
		}

		Node<T> r = u.top();
		PStack<Node<T>> f = PCollections.emptyStack();
		for (int i = 0; i < dep; i++) {
			TreeNode<T> tn = (TreeNode<T>) r;
			f = f.push(tn.lson);
			r = tn.rson;
		}

		PStack<Node<T>> v = u.pop();
		if (v.isEmpty())
			return new TreeList<>(f, size - 1);

		if (v.top() instanceof CountNode) {
			int c = ((CountNode<T>) v.top()).size + 1;
			v = v.pop().push((CountNode<T>) COUNTS[c]);
		} else {
			v = v.push((CountNode<T>) COUNTS[1]);
		}
		return new TreeList<>(Append.create(f, v), size - 1);
	}

	private abstract static class Node<T> { // NOSONAR
		protected final int size;

		Node(int size) {
			this.size = size;
		}

		final int size() {
			return size;
		}
	}

	private static final class TreeNode<T> extends Node<T> {
		private final Node<T> lson;
		private final Node<T> rson;

		TreeNode(Node<T> l, Node<T> r) {
			super(l.size() + r.size());
			lson = l;
			rson = r;
			assert (size & (-size)) == size;
		}
	}

	private static final class DataNode<T> extends Node<T> {
		private final T val;

		DataNode(T val) {
			super(1);
			this.val = val;
		}
	}

	private static final class CountNode<T> extends Node<T> {
		private CountNode(int size) {
			super(size);
		}

		@SuppressWarnings("rawtypes")
		public static CountNode[] build() {
			CountNode[] ret = new CountNode[32];
			for (int i = 0; i < 32; i++)
				ret[i] = new CountNode(i);
			return ret;
		}
	}
}