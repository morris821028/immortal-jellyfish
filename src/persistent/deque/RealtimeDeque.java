package persistent.deque;

import persistent.PDeque;
import persistent.PStack;
import persistent.helper.Append;
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

	protected final PStack<T> lhsFrom;
	protected final PStack<T> lhsAux;
	protected final PStack<T> lhsNew;

	protected final PStack<T> rhsFrom;
	protected final PStack<T> rhsAux;
	protected final PStack<T> rhsNew;
	protected final int headCopied;

	private RealtimeDeque() {
		this(PCollections.emptyStack(), PCollections.emptyStack(), null, null, null, null, null, null, null, null, 0);
	}

	private RealtimeDeque(PStack<T> lhs, PStack<T> rhs, PStack<T> lhsExtra, PStack<T> rhsExtra, PStack<T> lhsFrom,
			PStack<T> lhsAux, PStack<T> lhsNew, PStack<T> rhsFrom, PStack<T> rhsAux, PStack<T> rhsNew, int headCopied) {
		if (lhs.size() + rhs.size() > 4 && lhsAux == null
				&& (lhs.size() > rhs.size() * 3 || lhs.size() * 3 < rhs.size())) {
			// initiate the transfer process
			this.lhs = lhs;
			this.rhs = rhs;
			assert lhs != null && rhs != null;
			assert lhsExtra == null;
			this.lhsExtra = PCollections.emptyStack();
			this.rhsExtra = PCollections.emptyStack();

			this.lhsFrom = lhs;
			this.lhsAux = PCollections.emptyStack();
			this.lhsNew = PCollections.emptyStack();

			this.rhsFrom = rhs;
			this.rhsAux = PCollections.emptyStack();
			this.rhsNew = PCollections.emptyStack();
			this.headCopied = headCopied;
		} else {
			this.lhs = lhs;
			this.rhs = rhs;
			assert lhs != null && rhs != null;
			this.lhsExtra = lhsExtra;
			this.rhsExtra = rhsExtra;

			this.lhsFrom = lhsFrom;
			this.lhsAux = lhsAux;
			this.lhsNew = lhsNew;

			this.rhsFrom = rhsFrom;
			this.rhsAux = rhsAux;
			this.rhsNew = rhsNew;

			this.headCopied = headCopied;
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
		assert false;
		return null;
	}

	@Override
	public T back() {
		if (rhsExtra != null && !rhsExtra.isEmpty())
			return rhsExtra.top();
		assert !rhs.isEmpty();
		return rhs.top();
	}

	public boolean isTransferring() {
		return lhsAux != null;
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
				return new RealtimeDeque<>(s, b, lhsExtra, rhsExtra, lhsFrom, lhsAux, lhsNew, rhsFrom, rhsAux, rhsNew,
						headCopied);
			} else {
				return new RealtimeDeque<>(lhs.push(value), rhs, lhsExtra, rhsExtra, lhsFrom, lhsAux, lhsNew, rhsFrom, rhsAux, rhsNew,
						headCopied);
			}
		} else if (!isTransferring()) {
			return create(lhs.push(value), rhs, lhsExtra, rhsExtra, lhsFrom, lhsAux, lhsNew, rhsFrom, rhsAux, rhsNew,
					headCopied);
		} else {
			return create(lhs, rhs, lhsExtra.push(value), rhsExtra, lhsFrom, lhsAux, lhsNew, rhsFrom, rhsAux, rhsNew,
					headCopied);
		}
	}

	@Override
	public RealtimeDeque<T> pushBack(T value) {
		// TODO Auto-generated method stub
		return null;
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
			for (int i = size-1; !tmp.isEmpty(); i--) {
				buf[i] = tmp.top();
				tmp = tmp.pop();
			}
			assert lhs.size() + rhs.size() == size;
			PStack<T> nl = PCollections.emptyStack();
			PStack<T> nr = PCollections.emptyStack();
			for (int i = size-1; i >= 1; i--) {
				assert buf[i] != null;
				@SuppressWarnings("unchecked")
				T val = (T) buf[i];
				if (i == 1)
					nl = nl.push(val);
				else
					nr = nr.push(val);
			}
			return create(nl, nr, lhsExtra, rhsExtra, lhsFrom, lhsAux, lhsNew, rhsFrom, rhsAux, rhsNew,
					headCopied);
		} else if (!isTransferring()) {
			return create(lhs.pop(), rhs, lhsExtra, rhsExtra, lhsFrom, lhsAux, lhsNew, rhsFrom, rhsAux, rhsNew,
					headCopied);
		} else {
			if (!lhsExtra.isEmpty())
				return create(lhs, rhs, lhsExtra.pop(), rhsExtra, lhsFrom, lhsAux, lhsNew, rhsFrom, rhsAux, rhsNew,
						headCopied);
			else
				return create(lhs.pop(), rhs, lhsExtra, rhsExtra, lhsFrom, lhsAux, lhsNew, rhsFrom, rhsAux, rhsNew,
						headCopied);
		}
	}

	@Override
	public PDeque<T> popBack() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Perform two incremental steps
	 */
	private static <T> RealtimeDeque<T> step(final RealtimeDeque<T> q) {
		assert q.isTransferring() : "Internal error: invariant failure.";

		PStack<T> lhs = q.lhs;
		PStack<T> rhs = q.rhs;

		PStack<T> lhsExtra = q.lhsExtra;
		PStack<T> rhsExtra = q.rhsExtra;

		PStack<T> lhsFrom = q.lhsFrom;
		PStack<T> lhsAux = q.lhsAux;
		PStack<T> lhsNew = q.lhsNew;

		PStack<T> rhsFrom = q.rhsFrom;
		PStack<T> rhsAux = q.rhsAux;
		PStack<T> rhsNew = q.rhsNew;

		int headCopied = q.headCopied;

		if (!lhsFrom.isEmpty()) { // S
			lhsAux = lhsAux.push(lhsFrom.top());
			lhsFrom = lhsFrom.pop();

			if (!rhsFrom.isEmpty()) { // B
				rhsAux = rhsAux.push(rhsFrom.top());
				rhsFrom = rhsFrom.pop();
				if (!rhsFrom.isEmpty()) { // B
					rhsAux = rhsAux.push(rhsFrom.top());
					rhsFrom = rhsFrom.pop();
				}
			}
		}

		if (lhsFrom.isEmpty()) {
			while (rhsFrom.size() > lhsFrom.size() + 1) {
				rhsAux = rhsAux.push(rhsFrom.top());
				rhsFrom = rhsFrom.pop();
			}

			if (!rhsAux.isEmpty()) {
				rhsNew = rhsNew.push(rhsAux.top());
				rhsAux = rhsAux.pop();
				if (!rhsAux.isEmpty()) {
					rhsNew = rhsNew.push(rhsAux.top());
					rhsAux = rhsAux.pop();
				}
			}

			if (!rhsFrom.isEmpty()) {
				lhsNew = lhsNew.push(rhsFrom.top());
				rhsFrom = rhsFrom.pop();
			} else {
				if (!lhsAux.isEmpty() && headCopied < lhs.size()) {
					headCopied++;
					lhsNew = lhsNew.push(lhsAux.top());
					lhsAux = lhsAux.pop();
				}
				
				if (headCopied == lhs.size()) {
					lhs = lhsNew;
					rhs = rhsNew;

					if (lhsExtra != null)
						lhs = Append.create(lhsExtra, lhsNew);
					if (rhsExtra != null)
						rhs = Append.create(rhsExtra, rhsNew);

					lhsExtra = null;
					rhsExtra = null;

					lhsFrom = null;
					lhsAux = null;
					lhsNew = null;

					rhsFrom = null;
					rhsAux = null;
					rhsNew = null;
					
					headCopied = 0;
				}
			}
		}

		return new RealtimeDeque<>(lhs, rhs, lhsExtra, rhsExtra, lhsFrom, lhsAux, lhsNew, rhsFrom, rhsAux, rhsNew,
				headCopied);
	}

	private static <T> RealtimeDeque<T> create(PStack<T> lhs, PStack<T> rhs, PStack<T> lhsExtra, PStack<T> rhsExtra, PStack<T> lhsFrom, PStack<T> lhsAux,
			PStack<T> lhsNew, PStack<T> rhsFrom, PStack<T> rhsAux, PStack<T> rhsNew, int headCopied) {
		RealtimeDeque<T> ret = new RealtimeDeque<>(lhs, rhs, lhsExtra, rhsExtra, lhsFrom, lhsAux, lhsNew, rhsFrom,
				rhsAux, rhsNew, headCopied);
		if (ret.isTransferring())
			ret = step(ret);
		if (ret.isTransferring())
			ret = step(ret);
		if (ret.isTransferring())
			ret = step(ret);
		if (ret.isTransferring())
			ret = step(ret);
		if (ret.isTransferring())
			ret = step(ret);
		return ret;
	}

}
