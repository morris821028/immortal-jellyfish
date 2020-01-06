package persistent.array;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

import persistent.PList;
import persistent.PStack;
import persistent.helper.Rev;
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

	/** Pointer to tree root node. */
	private final PStack<Node<T>> rNodes;
	/** The number of elements in this list, leftist leaf tree */
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
	 * {@inheritDoc}
	 * 
	 * <p>
	 * O(log n) time in tree-style List, and cost O(log n) space.
	 * </p>
	 * 
	 * @param index {@inheritDoc}}
	 *
	 * @return {@inheritDoc}}
	 * @throws IndexOutOfBoundsException {@inheritDoc}
	 */
	@Override
	public T get(int index) {
		if (index < 0 || index >= size)
			throw new IndexOutOfBoundsException();

		PStack<Node<T>> u = rNodes;
		int tail = size;
		while (tail > 0) {
			Node<T> v = u.top();
			int b = tail & (-tail);
			if (index >= tail - b)
				return get(v, b >> 1, b - (tail - index));
			tail = tail - b;
			u = u.pop();
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

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * O(log n) time in tree-style List, and cost O(log n) space.
	 * </p>
	 * 
	 * @param index {@inheritDoc}
	 * @param value {@inheritDoc}
	 * @throws IndexOutOfBoundsException {@inheritDoc}
	 */
	@Override
	public TreeList<T> set(int index, T value) {
		if (index < 0 || index >= size())
			throw new IndexOutOfBoundsException();

		return new TreeList<>(setValue(rNodes, size(), index, value), size);
	}

	/**
	 * Helper function: replaces the element at the tree stack.
	 * 
	 * @param <T>   the type of basic elements in list
	 * @param u     tree node stack
	 * @param size  tree size
	 * @param index index of the element to replace
	 * @param value element to be stored at the specified position
	 * @return modified tree node stack
	 */
	private static <T> PStack<Node<T>> setValue(PStack<Node<T>> u, int size, int index, T value) {
		Node<T> v = u.top();
		int b = size & (-size);
		if (index >= size - b)
			return u.pop().push(setValue(v, b >> 1, b - (size - index), value));
		return setValue(u.pop(), size - b, index, value).push(v);
	}

	/**
	 * Helper function: replaces the element in the tree.
	 * 
	 * @param <T>   the type of basic elements in tree
	 * @param u     tree root
	 * @param size  tree size
	 * @param index index of the element to replace
	 * @param value element to be stored at the specified position
	 * @return modified tree root node
	 */
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
	public TreeList<T> pushBack(T value) {
		Node<T> data = new DataNode<>(value);
		if (isEmpty())
			return new TreeList<>(PStack.of(data), 1);

		PStack<Node<T>> u = rNodes;
		for (int i = 0; ((size >> i) & 1) != 0; i++) {
			Node<T> v = u.top();
			data = new TreeNode<>(v, data);
			u = u.pop();
		}

		u = u.push(data);
		return new TreeList<>(u, size + 1);
	}

	@Override
	public TreeList<T> popBack() {
		if (isEmpty())
			throw new NoSuchElementException();
		if (size == 1)
			return create();

		PStack<Node<T>> v = rNodes.pop();
		Node<T> r = rNodes.top();
		for (int i = 0; ((size >> i) & 1) == 0; i++) {
			TreeNode<T> tn = (TreeNode<T>) r;
			v = v.push(tn.lson);
			r = tn.rson;
		}

		return new TreeList<>(v, size - 1);
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

	@Override
	public Iterator<T> iterator() {
		return new TreeListIterator<>(this);
	}

	static class NodeIterator<T> implements Iterator<T> {
		private Deque<StackNode<T>> stk = new ArrayDeque<>();

		static class StackNode<T> {
			Node<T> node;
			boolean left;

			StackNode(Node<T> node, boolean left) {
				this.node = node;
				this.left = left;
			}
		}

		NodeIterator(Node<T> root) {
			stk.addLast(new StackNode<T>(root, true));
			while (root instanceof TreeNode) {
				Node<T> u = ((TreeNode<T>) root).lson;
				stk.addLast(new StackNode<T>(u, true));
				root = u;
			}
		}

		@Override
		public boolean hasNext() {
			return !stk.isEmpty();
		}

		@Override
		public T next() {
			if (!hasNext())
				throw new NoSuchElementException();
			T val = ((DataNode<T>) stk.getLast().node).val;
			findNext();
			return val;
		}

		private void findNext() {
			stk.removeLast();
			while (!stk.isEmpty() && !stk.peekLast().left) {
				stk.removeLast();
			}
			if (!stk.isEmpty()) {
				stk.peekLast().left = false;
				Node<T> root = ((TreeNode<T>) stk.peekLast().node).rson;
				stk.addLast(new StackNode<T>(root, true));
				while (root instanceof TreeNode) {
					Node<T> u = ((TreeNode<T>) root).lson;
					stk.addLast(new StackNode<T>(u, true));
					root = u;
				}
			}
		}
	}

	static class TreeListIterator<T> implements Iterator<T> {
		private PStack<Node<T>> rNodes;
		private NodeIterator<T> currentItr;
		private T next;

		public TreeListIterator(TreeList<T> array) {
			rNodes = Rev.reverse(array.rNodes);
			findNext();
		}

		@Override
		public boolean hasNext() {
			return currentItr != null;
		}

		@Override
		public T next() {
			if (!hasNext())
				throw new NoSuchElementException();
			T val = next;
			findNext();
			return val;
		}

		private void findNext() {
			if (currentItr != null && currentItr.hasNext()) {
				next = currentItr.next();
				return;
			}
			currentItr = null;
			if (!rNodes.isEmpty()) {
				currentItr = new NodeIterator<>(rNodes.top());
				rNodes = rNodes.pop();
				next = currentItr.next();
			}
		}
	}
}