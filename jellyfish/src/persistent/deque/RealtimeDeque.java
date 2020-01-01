package persistent.deque;

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
public final class RealtimeDeque<T> extends PDeque<T> {
	@SuppressWarnings("rawtypes")
	private static final RealtimeDeque<?> EMPTY = new RealtimeDeque();

	@SuppressWarnings("unchecked")
	public static <T> RealtimeDeque<T> create() {
		return (RealtimeDeque<T>) EMPTY;
	}

	/** The head/front part of deque */
	protected final PStack<T> lhs;
	/** The tail/back part of deque */
	protected final PStack<T> rhs;

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

	private RealtimeDeque() {
		this(PCollections.emptyStack(), PCollections.emptyStack(), null, null, null, null, null, null, 0);
	}

	private RealtimeDeque(PStack<T> lhs, PStack<T> rhs, PStack<T> sFrom, PStack<T> sAux, PStack<T> sNew,
			PStack<T> bFrom, PStack<T> bAux, PStack<T> bNew, int sCopied) {
		if (lhs.size() + rhs.size() <= 4 || (lhs.size() <= rhs.size() * 3 && lhs.size() * 3 >= rhs.size())) {
			this.lhs = lhs;
			this.rhs = rhs;

			this.sFrom = null;
			this.sAux = null;
			this.sNew = null;

			this.bFrom = null;
			this.bAux = null;
			this.bNew = null;

			this.sCopied = 0;
		} else if (sNew == null) {
			// initiate the transfer process
			this.lhs = lhs;
			this.rhs = rhs;

			this.sAux = PCollections.emptyStack();
			this.sNew = PCollections.emptyStack();

			this.bAux = PCollections.emptyStack();
			this.bNew = PCollections.emptyStack();
			this.sCopied = sCopied;
			if (lhs.size() < rhs.size()) {
				this.sFrom = lhs;
				this.bFrom = rhs;
			} else {
				this.sFrom = rhs;
				this.bFrom = lhs;
			}
		} else {
			this.lhs = lhs;
			this.rhs = rhs;

			this.sFrom = sFrom;
			this.sAux = sAux;
			this.sNew = sNew;

			this.bFrom = bFrom;
			this.bAux = bAux;
			this.bNew = bNew;

			this.sCopied = sCopied;
		}
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public int size() {
		return lhs.size() + rhs.size();
	}

	@Override
	public T front() {
		if (!lhs.isEmpty())
			return lhs.top();
		if (rhs.size() == 1)
			return rhs.top();
		else if (rhs.size() == 2)
			return rhs.pop().top();
		else if (rhs.size() == 3)
			return rhs.pop().pop().top();
		assert false : rhs.size();
		return null;
	}

	@Override
	public T back() {
		if (!rhs.isEmpty())
			return rhs.top();
		if (lhs.size() == 1)
			return lhs.top();
		else if (lhs.size() == 2)
			return lhs.pop().top();
		else if (lhs.size() == 3)
			return lhs.pop().pop().top();
		assert false;
		return null;
	}

	public boolean isTransferring() {
		return sNew != null;
	}

	@Override
	public RealtimeDeque<T> pushFront(T value) {
		int size = size();
		if (size < 4) {
			assert !isTransferring();
			if (size == 3) {
				T[] buf = flattenDeque();
				T w = value;
				T x = buf[0];
				T y = buf[1];
				T z = buf[2];
				// [w, x, y, z] => [w, x] [y, z]
				PStack<T> s = PStack.of(x, w);
				PStack<T> b = PStack.of(y, z);
				return new RealtimeDeque<>(s, b, sFrom, sAux, sNew, bFrom, bAux, bNew, sCopied);
			} else {
				return new RealtimeDeque<>(lhs.push(value), rhs, sFrom, sAux, sNew, bFrom, bAux, bNew, sCopied);
			}
		} else {
			return create(lhs.push(value), rhs, sFrom, sAux, sNew, bFrom, bAux, bNew, sCopied);
		}
	}

	@Override
	public RealtimeDeque<T> pushBack(T value) {
		int size = size();
		if (size < 4) {
			assert !isTransferring();
			if (size == 3) {
				T[] buf = flattenDeque();
				T w = buf[0];
				T x = buf[1];
				T y = buf[2];
				T z = value;
				// [w, x, y, z] => [w, x] [y, z]
				PStack<T> s = PStack.of(x, w);
				PStack<T> b = PStack.of(y, z);
				return new RealtimeDeque<>(s, b, sFrom, sAux, sNew, bFrom, bAux, bNew, sCopied);
			} else {
				return new RealtimeDeque<>(lhs, rhs.push(value), sFrom, sAux, sNew, bFrom, bAux, bNew, sCopied);
			}
		} else {
			return create(lhs, rhs.push(value), sFrom, sAux, sNew, bFrom, bAux, bNew, sCopied);
		}
	}

	@Override
	public RealtimeDeque<T> popFront() {
		int size = size();
		if (size < 4) {
			assert !isTransferring();
			if (size == 1)
				return create();
			T[] buf = flattenDeque();
			PStack<T> nl = PCollections.emptyStack();
			PStack<T> nr = PCollections.emptyStack();
			for (int i = size - 1; i >= 1; i--) {
				assert buf[i] != null;
				T val = buf[i];
				if (i == 1)
					nl = nl.push(val);
				else
					nr = nr.push(val);
			}
			return create(nl, nr, sFrom, sAux, sNew, bFrom, bAux, bNew, sCopied);
		} else {
			return create(lhs.pop(), rhs, sFrom, sAux, sNew, bFrom, bAux, bNew, sCopied);
		}
	}

	@Override
	public RealtimeDeque<T> popBack() {
		int size = size();
		if (size < 4) {
			assert !isTransferring();
			if (size == 1)
				return create();
			T[] buf = flattenDeque();
			PStack<T> nl = PCollections.emptyStack();
			PStack<T> nr = PCollections.emptyStack();
			for (int i = size - 2; i >= 0; i--) {
				assert buf[i] != null;
				T val = buf[i];
				if (i == 0)
					nl = nl.push(val);
				else
					nr = nr.push(val);
			}
			return create(nl, nr, sFrom, sAux, sNew, bFrom, bAux, bNew, sCopied);
		} else {
			return create(lhs, rhs.pop(), sFrom, sAux, sNew, bFrom, bAux, bNew, sCopied);
		}
	}

	@SuppressWarnings("unchecked")
	private T[] flattenDeque() {
		int size = size();
		assert size <= 4;
		PStack<T> tmp = lhs;
		Object[] buf = new Object[size];
		for (int i = 0; !tmp.isEmpty(); i++) {
			buf[i] = tmp.top();
			tmp = tmp.pop();
		}
		tmp = rhs;
		for (int i = size - 1; !tmp.isEmpty(); i--) {
			buf[i] = tmp.top();
			tmp = tmp.pop();
		}
		return (T[]) buf;
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
	private static <T> RealtimeDeque<T> step(final RealtimeDeque<T> q, int cost) {
		if (!q.isTransferring())
			return q;

		PStack<T> lhs = q.lhs;
		PStack<T> rhs = q.rhs;

		PStack<T> sFrom = q.sFrom;
		PStack<T> sAux = q.sAux;
		PStack<T> sNew = q.sNew;

		PStack<T> bFrom = q.bFrom;
		PStack<T> bAux = q.bAux;
		PStack<T> bNew = q.bNew;

		int sCopied = q.sCopied;
		int tMove = sFrom != null ? bAux.size() + bFrom.size() - sAux.size() - sFrom.size() - 1 : 0;

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
			return new RealtimeDeque<>(lhs, rhs, sFrom, sAux, sNew, bFrom, bAux, bNew, sCopied);

		sFrom = null;

		boolean sb = lhs.size() < rhs.size();
		while (cost > 0) {
			cost--;
			if (!bAux.isEmpty()) {
				bNew = bNew.push(bAux.top());
				bAux = bAux.pop();
			}

			if (!bFrom.isEmpty()) {
				sNew = sNew.push(bFrom.top());
				bFrom = bFrom.pop();
				continue;
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
					lhs = sNew;
					rhs = Take.create(rhs.size() - m - 1, rhs);
				} else {
					lhs = Take.create(lhs.size() - m - 1, lhs);
					rhs = sNew;
				}

				sFrom = sAux = sNew = null;

				bFrom = bAux = bNew = null;

				sCopied = 0;
				break;
			}
		}
		return new RealtimeDeque<>(lhs, rhs, sFrom, sAux, sNew, bFrom, bAux, bNew, sCopied);
	}

	private static <T> RealtimeDeque<T> create(PStack<T> lhs, PStack<T> rhs, PStack<T> sFrom, PStack<T> sAux,
			PStack<T> sNew, PStack<T> bFrom, PStack<T> bAux, PStack<T> bNew, int sCopied) {
		boolean init = sAux == null;
		RealtimeDeque<T> ret = new RealtimeDeque<>(lhs, rhs, sFrom, sAux, sNew, bFrom, bAux, bNew, sCopied);
		if (init && ret.isTransferring()) {
			// allocate 6 steps, which violates the invariant
			// 4 steps to each operations
			ret = step(ret, 10);
		} else {
			ret = step(ret, 4);
		}
		return ret;
	}
}
