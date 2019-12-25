package persistent.array;

import persistent.PList;
import persistent.PStack;
import persistent.util.PCollections;

public class TreeList<T> extends PList<T> {
	@SuppressWarnings("rawtypes")
	private static final TreeList<?> EMPTY = new TreeList();

	@SuppressWarnings("unchecked")
	public static <T> TreeList<T> create() {
		return (TreeList<T>) EMPTY;
	}

	private final PStack<Node<T>> rNodes;
	private final int size;

	private TreeList() {
		this(PCollections.emptyStack(), 0);
	}

	private TreeList(PStack<Node<T>> rNodes, int size) {
		this.rNodes = rNodes;
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
			if (index < u.size() / 2) {
				u = tn.lson;
			} else {
				u = tn.rson;
				index -= tn.size() / 2;
			}
		}
		assert index == 0;
		return ((DataNode<T>) u).val;
	}

	@Override
	public PList<T> set(int index, T value) {
		// TODO Auto-generated method stub
		return null;
	}

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
				u = u.pop().push(new CountNode<>(d.size - 1));
			}
		}

		u = u.push(data);
		if (dep > 0)
			u = u.push(new CountNode<>(dep));
		return new TreeList<>(u, size + 1);
	}

	@Override
	public PList<T> popBack() {
		// TODO Auto-generated method stub
		return null;
	}

	private abstract static class Node<T> {
		protected final int size;

		Node(int size) {
			this.size = size;
		}

		final int size() {
			return size;
		}
	}

	private static class TreeNode<T> extends Node<T> {
		private final Node<T> lson;
		private final Node<T> rson;

		TreeNode(Node<T> l, Node<T> r) {
			super(l.size() + r.size());
			lson = l;
			rson = r;
			assert (size&(-size)) == size;
		}
	}

	private static class DataNode<T> extends Node<T> {
		private final T val;

		DataNode(T val) {
			super(1);
			this.val = val;
		}
	}

	private class CountNode<T> extends Node<T> {
		CountNode(int size) {
			super(size);
		}
	}
}