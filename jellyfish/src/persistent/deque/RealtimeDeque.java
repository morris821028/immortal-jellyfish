package persistent.deque;

import java.util.Arrays;

import persistent.PDeque;
import persistent.PStack;
import persistent.helper.Take;
import persistent.util.PCollections;

/**
 * <p>
 * Paper: "Real-Time Deques, Multihead Turing Machines, and Purelay Functional
 * Programming", Tyng-Ruey Chang and Benjamin Goldberg.
 * </p>
 * 
 * @author morrisy
 * 
 * @param <T> The type of element
 */
public class RealtimeDeque<T> extends PDeque<T> {
	private static final PDeque<?> EMPTY = new TinyDeque<>(new Object[0]);

	@SuppressWarnings("unchecked")
	public static <T> PDeque<T> create() {
		return (PDeque<T>) EMPTY;
	}

	static class TinyDeque<T> extends PDeque<T> {
		final T[] elems;

		private TinyDeque(T[] e) {
			elems = e;
		}

		@Override
		public boolean isEmpty() {
			return size() == 0;
		}

		@Override
		public int size() {
			return elems.length;
		}

		@Override
		public T front() {
			return elems[0];
		}

		@Override
		public T back() {
			return elems[elems.length - 1];
		}

		@Override
		public PDeque<T> pushFront(T value) {
			int n = size();
			if (n < 3) {
				@SuppressWarnings("unchecked")
				T[] objs = (T[]) new Object[n + 1];
				objs[0] = value;
				System.arraycopy(elems, 0, objs, 1, n);
				return new TinyDeque<>(objs);
			} else {
				PStack<T> s = PStack.of(elems[0], value);
				PStack<T> b = PStack.of(elems[1], elems[2]);
				return new RealtimeDeque<>(s, b);
			}
		}

		@Override
		public PDeque<T> pushBack(T value) {
			int n = size();
			if (n < 3) {
				@SuppressWarnings("unchecked")
				T[] objs = (T[]) new Object[n + 1];
				objs[n] = value;
				System.arraycopy(elems, 0, objs, 0, n);
				return new TinyDeque<>(objs);
			} else {
				PStack<T> s = PStack.of(elems[1], elems[0]);
				PStack<T> b = PStack.of(elems[2], value);
				return new RealtimeDeque<>(s, b);
			}
		}

		@Override
		public PDeque<T> popFront() {
			int n = size();
			if (n == 1)
				return create();
			@SuppressWarnings("unchecked")
			T[] objs = (T[]) new Object[n - 1];
			System.arraycopy(elems, 1, objs, 0, n - 1);
			return new TinyDeque<>(objs);
		}

		@Override
		public PDeque<T> popBack() {
			int n = size();
			if (n == 1)
				return create();
			@SuppressWarnings("unchecked")
			T[] objs = (T[]) new Object[n - 1];
			System.arraycopy(elems, 0, objs, 0, n - 1);
			return new TinyDeque<>(objs);
		}
	}

	static class TransferDeque<T> extends RealtimeDeque<T> {
		/**
		 * The small stack, is the smaller stack of two parts in begin. Then, present
		 * the state of copy transferring.
		 */
		protected final PStack<T> sFrom;
		/** The small auxiliary-stack, store reversed version of {@link #sFrom}. */
		protected final PStack<T> sAux;
		/** The final result of smaller stack, replace the smaller one with this. */
		protected final PStack<T> sNew;

		/**
		 * The big stack, is the bigger stack of two parts in begin. Then, present the
		 * state of copy transferring.
		 */
		protected final PStack<T> bFrom;
		/**
		 * The big auxiliary-stack, store partial reversed version of {@link #bFrom}.
		 */
		protected final PStack<T> bAux;
		/** The final result of bigger stack, replace the bigger one with this. */
		protected final PStack<T> bNew;
		/** The counter of copied smaller stack elements. */
		protected final int sCopied;

		private TransferDeque(PStack<T> lhs, PStack<T> rhs, PStack<T> sFrom, PStack<T> sAux, PStack<T> sNew,
				PStack<T> bFrom, PStack<T> bAux, PStack<T> bNew, int sCopied) {
			super(lhs, rhs);

			this.sFrom = sFrom;
			this.sAux = sAux;
			this.sNew = sNew;

			this.bFrom = bFrom;
			this.bAux = bAux;
			this.bNew = bNew;

			this.sCopied = sCopied;
		}

		@Override
		public RealtimeDeque<T> pushFront(T value) {
			return create(lhs.push(value), rhs, sFrom, sAux, sNew, bFrom, bAux, bNew, sCopied);
		}

		@Override
		public RealtimeDeque<T> pushBack(T value) {
			return create(lhs, rhs.push(value), sFrom, sAux, sNew, bFrom, bAux, bNew, sCopied);
		}

		@Override
		public PDeque<T> popFront() {
			int size = size();
			if (size < 4)
				return super.popFront();
			return create(lhs.pop(), rhs, sFrom, sAux, sNew, bFrom, bAux, bNew, sCopied);
		}

		@Override
		public PDeque<T> popBack() {
			int size = size();
			if (size < 4)
				return super.popBack();
			return create(lhs, rhs.pop(), sFrom, sAux, sNew, bFrom, bAux, bNew, sCopied);
		}

		private static <T> RealtimeDeque<T> create(PStack<T> lhs, PStack<T> rhs, PStack<T> sFrom, PStack<T> sAux,
				PStack<T> sNew, PStack<T> bFrom, PStack<T> bAux, PStack<T> bNew, int sCopied) {
			if (lhs.size() + rhs.size() <= 4
					|| (Math.min(lhs.size(), rhs.size()) * 3 >= Math.max(lhs.size(), rhs.size())))
				return new RealtimeDeque<>(lhs, rhs);

			return step(lhs, rhs, sFrom, sAux, sNew, bFrom, bAux, bNew, sCopied, 4);
		}
	}

	/** The head/front part of deque */
	protected final PStack<T> lhs;
	/** The tail/back part of deque */
	protected final PStack<T> rhs;

	private RealtimeDeque() {
		this(PCollections.emptyStack(), PCollections.emptyStack());
	}

	private RealtimeDeque(PStack<T> lhs, PStack<T> rhs) {
		this.lhs = lhs;
		this.rhs = rhs;
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public int size() {
		return lhs.size() + rhs.size();
	}

	private static <T> T bottom(PStack<T> stk) {
		for (int i = stk.size() - 1; i >= 1; i--)
			stk = stk.pop();
		return stk.top();
	}

	@Override
	public T front() {
		if (!lhs.isEmpty())
			return lhs.top();
		assert rhs.size() <= 3;
		return bottom(rhs);
	}

	@Override
	public T back() {
		if (!rhs.isEmpty())
			return rhs.top();
		assert lhs.size() <= 3;
		return bottom(lhs);
	}

	@Override
	public PDeque<T> pushFront(T value) {
		return create(lhs.push(value), rhs);
	}

	@Override
	public PDeque<T> pushBack(T value) {
		return create(lhs, rhs.push(value));
	}

	@Override
	public PDeque<T> popFront() {
		int size = size();
		if (size < 4) {
			if (size == 1)
				return create();
			T[] buf = flattenDeque();
			return new TinyDeque<>(Arrays.copyOfRange(buf, 1, size));
		}
		return create(lhs.pop(), rhs);
	}

	@Override
	public PDeque<T> popBack() {
		int size = size();
		if (size < 4) {
			if (size == 1)
				return create();
			T[] buf = flattenDeque();
			return new TinyDeque<>(Arrays.copyOfRange(buf, 0, size - 1));
		}
		return create(lhs, rhs.pop());
	}

	T[] flattenDeque() {
		int size = size();
		assert size <= 4;
		PStack<T> tmp = lhs;
		@SuppressWarnings("unchecked")
		T[] buf = (T[]) new Object[size];
		for (int i = 0; !tmp.isEmpty(); i++) {
			buf[i] = tmp.top();
			tmp = tmp.pop();
		}
		tmp = rhs;
		for (int i = size - 1; !tmp.isEmpty(); i--) {
			buf[i] = tmp.top();
			tmp = tmp.pop();
		}
		return buf;
	}

	/**
	 * Perform 2 incremental steps. Totally, we will perform {@literal 4n + 6} for
	 * the transform process.
	 * 
	 * @param <T> The type of element
	 * @param q   The adjusting deque
	 * @return An adjusted deque by exact 2 steps. If remaining steps is not enough,
	 *         return the last state.
	 */
	private static <T> RealtimeDeque<T> step(PStack<T> lhs, PStack<T> rhs, PStack<T> sFrom, PStack<T> sAux,
			PStack<T> sNew, PStack<T> bFrom, PStack<T> bAux, PStack<T> bNew, int sCopied, int cost) {
		if (sNew != null) {
			int tMove = sFrom != null ? bAux.size() + bFrom.size() - (sAux.size() + sFrom.size()) - 1 : 0;

			while (bAux.size() < tMove && cost > 0) {
				bAux = bAux.push(bFrom.top());
				bFrom = bFrom.pop();

				cost--;

				if (!sFrom.isEmpty()) {
					sAux = sAux.push(sFrom.top());
					sFrom = sFrom.pop();
				}
			}

			if (cost == 0)
				return new TransferDeque<>(lhs, rhs, sFrom, sAux, sNew, bFrom, bAux, bNew, sCopied);

			sFrom = null;

			boolean sb = lhs.size() < rhs.size();
			while (cost > 0) {
				cost--;
				if (!bAux.isEmpty()) {
					bNew = bNew.push(bAux.top());
					bAux = bAux.pop();
					if (!bFrom.isEmpty()) {
						sNew = sNew.push(bFrom.top());
						bFrom = bFrom.pop();
						continue;
					}
				}

				int targetSize = sb ? lhs.size() : rhs.size();
				if (!sAux.isEmpty() && sCopied < targetSize) {
					sCopied++;
					sNew = sNew.push(sAux.top());
					sAux = sAux.pop();
				}

				if (sCopied == targetSize) {
					int m = (sNew.size() + sAux.size()) / 2;
					if (sb) {
						lhs = sNew.top() == lhs.top() ? sNew : sNew.pop().push(lhs.top());
						rhs = Take.create(rhs.size() - m - 1, rhs);
					} else {
						lhs = Take.create(lhs.size() - m - 1, lhs);
						rhs = sNew.top() == rhs.top() ? sNew : sNew.pop().push(rhs.top());
					}

					return new RealtimeDeque<>(lhs, rhs);
				}
			}
		}
		if (sNew != null)
			return new TransferDeque<>(lhs, rhs, sFrom, sAux, sNew, bFrom, bAux, bNew, sCopied);
		return new RealtimeDeque<>(lhs, rhs);
	}

	private static <T> RealtimeDeque<T> create(PStack<T> lhs, PStack<T> rhs) {
		if (lhs.size() + rhs.size() > 4
				&& (Math.min(lhs.size(), rhs.size()) * 3 < Math.max(lhs.size(), rhs.size()))) {
			// initiate the transfer process

			PStack<T> sAux = PCollections.emptyStack();
			PStack<T> sNew = PCollections.emptyStack();

			PStack<T> bAux = PCollections.emptyStack();
			PStack<T> bNew = PCollections.emptyStack();
			PStack<T> sFrom;
			PStack<T> bFrom;
			if (lhs.size() < rhs.size()) {
				sFrom = lhs;
				bFrom = rhs;
			} else {
				sFrom = rhs;
				bFrom = lhs;
			}
			return step(lhs, rhs, sFrom, sAux, sNew, bFrom, bAux, bNew, 0, 8);
		}
		return new RealtimeDeque<>(lhs, rhs);
	}
}
