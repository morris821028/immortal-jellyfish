package persistent.deque;

import java.util.Arrays;
import java.util.Iterator;

import persistent.PDeque;
import persistent.PStack;
import persistent.helper.Take;
import persistent.util.ConcatenatedIterator;
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

	abstract static class TransDeque<T> extends RealtimeDeque<T> {
		public TransDeque(PStack<T> lhs, PStack<T> rhs) {
			super(lhs, rhs);
		}

		@Override
		public RealtimeDeque<T> pushFront(T value) {
			return createTrans(lhs.push(value), rhs);
		}

		@Override
		public RealtimeDeque<T> pushBack(T value) {
			return createTrans(lhs, rhs.push(value));
		}

		@Override
		public PDeque<T> popFront() {
			int size = size();
			if (size < 4)
				return super.popFront();
			return createTrans(lhs.pop(), rhs);
		}

		@Override
		public PDeque<T> popBack() {
			int size = size();
			if (size < 4)
				return super.popBack();
			return createTrans(lhs, rhs.pop());
		}

		abstract RealtimeDeque<T> createTrans(PStack<T> lhs, PStack<T> rhs);
	}

	static class TransPrevDeque<T> extends TransDeque<T> {
		/**
		 * The small stack, is the smaller stack of two parts in begin. Then, present
		 * the state of copy transferring.
		 */
		protected final PStack<T> sFrom;
		/** The small auxiliary-stack, store reversed version of {@link #sFrom}. */
		protected final PStack<T> sAux;

		/**
		 * The big stack, is the bigger stack of two parts in begin. Then, present the
		 * state of copy transferring.
		 */
		protected final PStack<T> bFrom;
		/**
		 * The big auxiliary-stack, store partial reversed version of {@link #bFrom}.
		 */
		protected final PStack<T> bAux;

		private TransPrevDeque(PStack<T> lhs, PStack<T> rhs, PStack<T> sFrom, PStack<T> sAux, PStack<T> bFrom,
				PStack<T> bAux) {
			super(lhs, rhs);

			this.sFrom = sFrom;
			this.sAux = sAux;

			this.bFrom = bFrom;
			this.bAux = bAux;
		}

		RealtimeDeque<T> createTrans(PStack<T> lhs, PStack<T> rhs) {
			final int lsz = lhs.size();
			final int rsz = rhs.size();
			if (lsz + rsz <= 4 || (Math.min(lsz, rsz) * 3 >= Math.max(lsz, rsz)))
				return new RealtimeDeque<>(lhs, rhs);

			return stepPrev(lhs, rhs, sFrom, sAux, bFrom, bAux, 4);
		}
	}

	static class TransPostDeque<T> extends TransDeque<T> {
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

		private TransPostDeque(PStack<T> lhs, PStack<T> rhs, PStack<T> sAux, PStack<T> sNew, PStack<T> bFrom,
				PStack<T> bAux, PStack<T> bNew, int sCopied) {
			super(lhs, rhs);

			this.sAux = sAux;
			this.sNew = sNew;

			this.bFrom = bFrom;
			this.bAux = bAux;
			this.bNew = bNew;

			this.sCopied = sCopied;
		}

		RealtimeDeque<T> createTrans(PStack<T> lhs, PStack<T> rhs) {
			final int lsz = lhs.size();
			final int rsz = rhs.size();
			if (lsz + rsz <= 4 || (Math.min(lsz, rsz) * 3 >= Math.max(lsz, rsz)))
				return new RealtimeDeque<>(lhs, rhs);

			return stepPost(lhs, rhs, sAux, sNew, bFrom, bAux, bNew, sCopied, 4);
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

	private T[] flattenDeque() {
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
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<T> iterator() {
		return ConcatenatedIterator.create(lhs.iterator(), rhs.descendingIterator());
	}

	private static <T> RealtimeDeque<T> stepPrev(PStack<T> lhs, PStack<T> rhs, PStack<T> sFrom, PStack<T> sAux,
			PStack<T> bFrom, PStack<T> bAux, int cost) {
		int tMove = bAux.size() + bFrom.size() - (sAux.size() + sFrom.size()) - 1;
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
			return new TransPrevDeque<>(lhs, rhs, sFrom, sAux, bFrom, bAux);
		PStack<T> empty = PCollections.emptyStack();
		return stepPost(lhs, rhs, sAux, empty, bFrom, bAux, empty, 0, cost);
	}

	private static <T> RealtimeDeque<T> stepPost(PStack<T> lhs, PStack<T> rhs, PStack<T> sAux, PStack<T> sNew,
			PStack<T> bFrom, PStack<T> bAux, PStack<T> bNew, int sCopied, int cost) {
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
		return new TransPostDeque<>(lhs, rhs, sAux, sNew, bFrom, bAux, bNew, sCopied);
	}

	private static <T> RealtimeDeque<T> create(PStack<T> lhs, PStack<T> rhs) {
		final int lsz = lhs.size();
		final int rsz = rhs.size();
		if (lsz + rsz > 4 && (Math.min(lsz, rsz) * 3 < Math.max(lsz, rsz))) {
			// initiate the transfer process

			PStack<T> empty = PCollections.emptyStack();
			if (lhs.size() < rhs.size()) {
				return stepPrev(lhs, rhs, lhs, empty, rhs, empty, 8);
			} else {
				return stepPrev(lhs, rhs, rhs, empty, lhs, empty, 8);
			}
		}
		return new RealtimeDeque<>(lhs, rhs);
	}
}
