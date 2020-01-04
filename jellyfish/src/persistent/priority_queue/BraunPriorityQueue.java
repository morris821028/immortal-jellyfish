package persistent.priority_queue;

import java.util.Comparator;

import persistent.PPriorityQueue;

public class BraunPriorityQueue<T> extends PPriorityQueue<T> {
	/**
	 * Returns an empty priority queue.
	 * 
	 * @param <T> The type of elements held in this collection
	 * @return An empty priority queue.
	 */
	public static <T> BraunPriorityQueue<T> create(Comparator<? super T> comparator) {
		return new BraunPriorityQueue<>(comparator);
	}

	private final int size;
	private final Node<T> root;
	private final Comparator<? super T> comparator;

	private BraunPriorityQueue(Comparator<? super T> comparator) {
		this(null, 0, comparator);
	}

	private BraunPriorityQueue(Node<T> root, int size, Comparator<? super T> comparator) {
		this.root = root;
		this.size = size;
		this.comparator = comparator;
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
	public T top() {
		return root == null ? null : root.value;
	}

	@Override
	public PPriorityQueue<T> push(T value) {
		if (size == 0)
			return new BraunPriorityQueue<>(new Node<>(null, null, value), 1, comparator);
		return new BraunPriorityQueue<>(push(value, root), size + 1, comparator);
	}

	private Node<T> push(T value, Node<T> u) {
		if (u == null)
			return new Node<>(null, null, value);
		int c = comparator.compare(value, u.value);
		return new Node<>(push(c > 0 ? value : u.value, u.rson), u.lson, c > 0 ? u.value : value);
	}

	@Override
	public PPriorityQueue<T> pop() {
		if (size <= 1)
			return create(comparator);
		Pair<T> p = pop(root);
		assert p != null;
		return new BraunPriorityQueue<>(p.u, size - 1, comparator);
	}

	private static class Pair<T> {
		T value;
		Node<T> u;

		public Pair(T value, Node<T> u) {
			this.value = value;
			this.u = u;
		}
	}

	private Pair<T> pop(Node<T> u) {
		if (u == null)
			return null;
		Pair<T> p = pop(u.lson);
		if (p != null) {
			int c = u.rson == null ? -1 : comparator.compare(p.value, u.rson.value);
			if (c <= 0)
				return new Pair<>(u.value, new Node<>(u.rson, p.u, p.value));
			Node<T> ls = new Node<>(u.rson.lson, u.rson.rson, p.value);
			return new Pair<>(u.value, new Node<>(ls, p.u, u.rson.value));
		}
		return new Pair<>(u.value, null);
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
