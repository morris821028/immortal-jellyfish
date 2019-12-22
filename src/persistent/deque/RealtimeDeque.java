package persistent.deque;

import persistent.PDeque;
import persistent.PStack;
import persistent.helper.Append;
import persistent.helper.Take;
import persistent.util.PCollections;

public final class RealtimeDeque<T> implements PDeque<T> {
	@SuppressWarnings("rawtypes")
	private static final RealtimeDeque<?> EMPTY = new RealtimeDeque();

	@SuppressWarnings("unchecked")
	public static <T> RealtimeDeque<T> create() {
		return (RealtimeDeque<T>) EMPTY;
	}

	protected final PStack<T> lhs;
	protected final PStack<T> rhs;

	protected final PStack<T> lhsExtra;
	protected final PStack<T> rhsExtra;

	protected final PStack<T> sFrom;
	protected final PStack<T> sAux;
	protected final PStack<T> sNew;

	protected final PStack<T> bFrom;
	protected final PStack<T> bAux;
	protected final PStack<T> bNew;
	protected final int sCopied;

	/** True if right-hand-side is bigger. */
	protected final boolean sb;
	/** Pop and reverse the topmost elements of bigger stack */
	protected final int bMove;

	private RealtimeDeque() {
		this(PCollections.emptyStack(), PCollections.emptyStack(), null, null, null, null, null, null, null, null, 0,
				false, 0);
	}

	private RealtimeDeque(PStack<T> lhs, PStack<T> rhs, PStack<T> lhsExtra, PStack<T> rhsExtra, PStack<T> sFrom,
			PStack<T> sAux, PStack<T> sNew, PStack<T> bFrom, PStack<T> bAux, PStack<T> bNew, int sCopied, boolean sb,
			int bMove) {
		if (lhs.size() + rhs.size() > 4 && sAux == null
				&& (lhs.size() > rhs.size() * 3 || lhs.size() * 3 < rhs.size())) {
			// initiate the transfer process
			this.lhs = lhs;
			this.rhs = rhs;

			this.lhsExtra = PCollections.emptyStack();
			this.rhsExtra = PCollections.emptyStack();

			this.sAux = PCollections.emptyStack();
			this.sNew = PCollections.emptyStack();

			this.bAux = PCollections.emptyStack();
			this.bNew = PCollections.emptyStack();
			this.sCopied = sCopied;
			this.sb = lhs.size() < rhs.size();
			if (this.sb) {
				this.sFrom = lhs;
				this.bFrom = rhs;
			} else {
				this.sFrom = rhs;
				this.bFrom = lhs;
			}
			this.bMove = this.bFrom.size() - this.sFrom.size() - 1;
		} else {
			this.lhs = lhs;
			this.rhs = rhs;
			this.lhsExtra = lhsExtra;
			this.rhsExtra = rhsExtra;

			this.sFrom = sFrom;
			this.sAux = sAux;
			this.sNew = sNew;

			this.bFrom = bFrom;
			this.bAux = bAux;
			this.bNew = bNew;

			this.sCopied = sCopied;
			this.sb = sb;
			this.bMove = bMove;
		}
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public int size() {
		int size = lhs.size() + rhs.size();
		if (lhsExtra != null)
			size += lhsExtra.size();
		if (rhsExtra != null)
			size += rhsExtra.size();
		return size;
	}

	@Override
	public T front() {
		if (lhsExtra != null && !lhsExtra.isEmpty())
			return lhsExtra.top();
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
		if (rhsExtra != null && !rhsExtra.isEmpty())
			return rhsExtra.top();
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
		return sAux != null;
	}

	@Override
	public RealtimeDeque<T> pushFront(T value) {
		int size = size();
		if (size < 4) {
			assert !isTransferring();
			if (size == 3) {
				PDeque<T> tmp = this;
				T w = value;
				T x = tmp.front();
				tmp = tmp.popFront();
				T y = tmp.front();
				tmp = tmp.popFront();
				T z = tmp.front();
				// [w, x, y, z] => [w, x] [y, z]
				PStack<T> s = PCollections.emptyStack();
				s = s.push(x).push(w);
				PStack<T> b = PCollections.emptyStack();
				b = b.push(y).push(z);
				return new RealtimeDeque<>(s, b, lhsExtra, rhsExtra, sFrom, sAux, sNew, bFrom, bAux, bNew, sCopied, sb,
						bMove);
			} else {
				return new RealtimeDeque<>(lhs.push(value), rhs, lhsExtra, rhsExtra, sFrom, sAux, sNew, bFrom, bAux,
						bNew, sCopied, sb, bMove);
			}
		} else if (!isTransferring()) {
			return create(lhs.push(value), rhs, lhsExtra, rhsExtra, sFrom, sAux, sNew, bFrom, bAux, bNew, sCopied, sb,
					bMove);
		} else {
			return create(lhs, rhs, lhsExtra.push(value), rhsExtra, sFrom, sAux, sNew, bFrom, bAux, bNew, sCopied, sb,
					bMove);
		}
	}

	@Override
	public RealtimeDeque<T> pushBack(T value) {
		int size = size();
		if (size < 4) {
			assert !isTransferring();
			if (size == 3) {
				RealtimeDeque<T> tmp = this;
				T w = tmp.front();
				tmp = tmp.popFront();
				T x = tmp.front();
				tmp = tmp.popFront();
				T y = tmp.front();
				T z = value;
				// [w, x, y, z] => [w, x] [y, z]
				PStack<T> s = PCollections.emptyStack();
				s = s.push(x).push(w);
				PStack<T> b = PCollections.emptyStack();
				b = b.push(y).push(z);
				return new RealtimeDeque<>(s, b, lhsExtra, rhsExtra, sFrom, sAux, sNew, bFrom, bAux, bNew, sCopied, sb,
						bMove);
			} else {
				return new RealtimeDeque<>(lhs, rhs.push(value), lhsExtra, rhsExtra, sFrom, sAux, sNew, bFrom, bAux,
						bNew, sCopied, sb, bMove);
			}
		} else if (!isTransferring()) {
			return create(lhs, rhs.push(value), lhsExtra, rhsExtra, sFrom, sAux, sNew, bFrom, bAux, bNew, sCopied, sb,
					bMove);
		} else {
			return create(lhs, rhs, lhsExtra, rhsExtra.push(value), sFrom, sAux, sNew, bFrom, bAux, bNew, sCopied, sb,
					bMove);
		}
	}

	@Override
	public RealtimeDeque<T> popFront() {
		int size = size();
		if (size < 4) {
			assert !isTransferring();
			if (size == 1)
				return create();
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
			assert lhs.size() + rhs.size() == size;
			PStack<T> nl = PCollections.emptyStack();
			PStack<T> nr = PCollections.emptyStack();
			for (int i = size - 1; i >= 1; i--) {
				assert buf[i] != null;
				@SuppressWarnings("unchecked")
				T val = (T) buf[i];
				if (i == 1)
					nl = nl.push(val);
				else
					nr = nr.push(val);
			}
			return create(nl, nr, lhsExtra, rhsExtra, sFrom, sAux, sNew, bFrom, bAux, bNew, sCopied, sb, bMove);
		} else if (!isTransferring()) {
			return create(lhs.pop(), rhs, lhsExtra, rhsExtra, sFrom, sAux, sNew, bFrom, bAux, bNew, sCopied, sb, bMove);
		} else {
			if (!lhsExtra.isEmpty())
				return create(lhs, rhs, lhsExtra.pop(), rhsExtra, sFrom, sAux, sNew, bFrom, bAux, bNew, sCopied, sb,
						bMove);
			else
				return create(lhs.pop(), rhs, lhsExtra, rhsExtra, sFrom, sAux, sNew, bFrom, bAux, bNew, sCopied, sb,
						bMove);
		}
	}

	@Override
	public RealtimeDeque<T> popBack() {
		int size = size();
		if (size < 4) {
			assert !isTransferring();
			if (size == 1)
				return create();
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
			assert lhs.size() + rhs.size() == size;
			PStack<T> nl = PCollections.emptyStack();
			PStack<T> nr = PCollections.emptyStack();
			for (int i = size - 2; i >= 0; i--) {
				assert buf[i] != null;
				@SuppressWarnings("unchecked")
				T val = (T) buf[i];
				if (i == 0)
					nl = nl.push(val);
				else
					nr = nr.push(val);
			}
			return create(nl, nr, lhsExtra, rhsExtra, sFrom, sAux, sNew, bFrom, bAux, bNew, sCopied, sb, bMove);
		} else if (!isTransferring()) {
			return create(lhs, rhs.pop(), lhsExtra, rhsExtra, sFrom, sAux, sNew, bFrom, bAux, bNew, sCopied, sb, bMove);
		} else {
			if (!lhsExtra.isEmpty())
				return create(lhs, rhs, lhsExtra, rhsExtra.pop(), sFrom, sAux, sNew, bFrom, bAux, bNew, sCopied, sb,
						bMove);
			else
				return create(lhs, rhs.pop(), lhsExtra, rhsExtra, sFrom, sAux, sNew, bFrom, bAux, bNew, sCopied, sb,
						bMove);
		}
	}

	/**
	 * Perform two incremental steps
	 */
	private static <T> RealtimeDeque<T> step(final RealtimeDeque<T> q) {
		if (!q.isTransferring())
			return q;

		PStack<T> lhs = q.lhs;
		PStack<T> rhs = q.rhs;

		PStack<T> lhsExtra = q.lhsExtra;
		PStack<T> rhsExtra = q.rhsExtra;

		PStack<T> sFrom = q.sFrom;
		PStack<T> sAux = q.sAux;
		PStack<T> sNew = q.sNew;

		PStack<T> bFrom = q.bFrom;
		PStack<T> bAux = q.bAux;
		PStack<T> bNew = q.bNew;

		int sCopied = q.sCopied;
		boolean sb = q.sb;
		int bMove = q.bMove;

		if (bMove > 0) {
			bAux = bAux.push(bFrom.top());
			bFrom = bFrom.pop();

			bMove--;
			if (!sFrom.isEmpty()) {
				sAux = sAux.push(sFrom.top());
				sFrom = sFrom.pop();
			}
			return new RealtimeDeque<>(lhs, rhs, lhsExtra, rhsExtra, sFrom, sAux, sNew, bFrom, bAux, bNew, sCopied, sb,
					bMove);
		}

		if (!bAux.isEmpty()) {
			bNew = bNew.push(bAux.top());
			bAux = bAux.pop();
		}

		if (!bFrom.isEmpty()) {
			sNew = sNew.push(bFrom.top());
			bFrom = bFrom.pop();
			return new RealtimeDeque<>(lhs, rhs, lhsExtra, rhsExtra, sFrom, sAux, sNew, bFrom, bAux, bNew, sCopied, sb,
					bMove);
		}

		if (sb) {
			if (!sAux.isEmpty() && sCopied < lhs.size()) {
				sCopied++;
				sNew = sNew.push(sAux.top());
				sAux = sAux.pop();
			}

			if (sCopied == lhs.size()) {
				int m = (sNew.size() + sAux.size())/2;
				lhs = Append.create(lhsExtra, sNew);
				rhs = Append.create(rhsExtra, Take.create(rhs.size() - m - 1, rhs));

				lhsExtra = rhsExtra = null;

				sFrom = sAux = sNew = null;

				bFrom = bAux = bNew = null;

				sCopied = 0;
				RealtimeDeque<T> r = new RealtimeDeque<>(lhs, rhs, lhsExtra, rhsExtra, sFrom, sAux, sNew, bFrom, bAux,
						bNew, sCopied, sb, bMove);
				assert !r.isTransferring();
				return r;
			}
		} else {
			if (!sAux.isEmpty() && sCopied < rhs.size()) {
				sCopied++;
				sNew = sNew.push(sAux.top());
				sAux = sAux.pop();
			}

			if (sCopied == rhs.size()) {
				int m = (sNew.size() + sAux.size())/2;
				lhs = Append.create(lhsExtra, Take.create(lhs.size() - m - 1, lhs));
				rhs = Append.create(rhsExtra, sNew);

				lhsExtra = rhsExtra = null;

				sFrom = sAux = sNew = null;

				bFrom = bAux = bNew = null;

				sCopied = 0;
				RealtimeDeque<T> r = new RealtimeDeque<>(lhs, rhs, lhsExtra, rhsExtra, sFrom, sAux, sNew, bFrom, bAux,
						bNew, sCopied, sb, bMove);
				assert !r.isTransferring();
				return r;
			}
		}
		return new RealtimeDeque<>(lhs, rhs, lhsExtra, rhsExtra, sFrom, sAux, sNew, bFrom, bAux, bNew, sCopied, sb,
				bMove);
	}

	private static <T> RealtimeDeque<T> create(PStack<T> lhs, PStack<T> rhs, PStack<T> lhsExtra, PStack<T> rhsExtra,
			PStack<T> sFrom, PStack<T> sAux, PStack<T> sNew, PStack<T> bFrom, PStack<T> bAux, PStack<T> bNew,
			int sCopied, boolean sb, int bMove) {
		boolean init = sAux == null;
		RealtimeDeque<T> ret = new RealtimeDeque<>(lhs, rhs, lhsExtra, rhsExtra, sFrom, sAux, sNew, bFrom, bAux, bNew,
				sCopied, sb, bMove);
		init = init && ret.isTransferring();
		ret = step(ret);
		ret = step(ret);
		ret = step(ret);
		ret = step(ret);
		if (init) {
			ret = step(ret);
			ret = step(ret);
			ret = step(ret);
			ret = step(ret);
			ret = step(ret);
			ret = step(ret);
		}
		return ret;
	}

	protected void dump() {
		System.out.printf("size l %d, r %d, %d, sCopied %d\n", lhs.size(), rhs.size(), this.size(), this.sCopied);
		if (isTransferring()) {
			System.out.printf("\tlExtra %d, rExtra %d\n", lhsExtra.size(), rhsExtra.size());
			System.out.printf("\tlNew %d, rNew %d\n", sNew.size(), bNew.size());
			System.out.printf("\tlAux %d, rAux %d\n", sAux.size(), bAux.size());
		}
	}
}
