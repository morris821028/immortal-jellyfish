package persistent.queue;

import persistent.PQueue;
import persistent.PStack;
import persistent.util.PCollections;

/**
 * Paper: "Real-Time Deques, Multihead Turing Machines, and Purely Functional
 * Programming", Tyng-Ruey Chuang and Benjamin Goldberg
 * 
 * @author morrisy
 *
 * @param <T> The type of element
 */
public final class RealtimeExtraQueue<T> implements PQueue<T> {
	@SuppressWarnings("rawtypes")
	private static final RealtimeExtraQueue<?> EMPTY = new RealtimeExtraQueue();

	@SuppressWarnings("unchecked")
	public static <T> RealtimeExtraQueue<T> create() {
		return (RealtimeExtraQueue<T>) EMPTY;
	}

	private final PStack<T> head;
	private final PStack<T> tail;

	private final PStack<T> iExtra;
	private final PStack<T> iFrom;
	private final PStack<T> oNew;
	private final PStack<T> oFrom;
	private final PStack<T> oAux;
	private final int headCopied;

	private RealtimeExtraQueue() {
		this(PCollections.emptyStack(), PCollections.emptyStack(), null, null, null, null, null, 0);
	}

	private RealtimeExtraQueue(PStack<T> head, PStack<T> tail, PStack<T> iExtra, PStack<T> iFrom, PStack<T> oNew,
			PStack<T> oFrom, PStack<T> oAux, int headCopied) {
		this.headCopied = headCopied;
		if (head.size() < tail.size() && iFrom == null) {
			// violated invariant
			assert iFrom == null && oNew == null && oFrom == null
					&& oAux == null : "Internal error: invariant failure.";
			if (tail.size() == 1) {
				this.head = tail;
				this.tail = PCollections.emptyStack();
				this.iExtra = null;
				this.iFrom = null;
				this.oNew = null;
				this.oFrom = null;
				this.oAux = null;
			} else {
				// initiate the transfer process
				this.head = head;
				this.tail = tail;

				this.iExtra = PCollections.emptyStack();
				this.iFrom = tail;
				this.oNew = PCollections.emptyStack();
				this.oFrom = head;
				this.oAux = PCollections.emptyStack();
			} 
		} else {
			this.head = head;
			this.tail = tail;

			this.iExtra = iExtra;
			this.iFrom = iFrom;
			this.oNew = oNew;
			this.oFrom = oFrom;
			this.oAux = oAux;
		}
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public int size() {
		int size = head.size() + tail.size();
		if (iExtra != null)
			size += iExtra.size();
		return size;
	}

	public PQueue<T> clear() {
		return create();
	}

	@Override
	public T front() {
		return head.top();
	}

	@Override
	public PQueue<T> push(T value) {
		if (iExtra != null)
			return create(head, tail, iExtra.push(value), iFrom, oNew, oFrom, oAux, headCopied);
		return create(head, tail.push(value), iExtra, iFrom, oNew, oFrom, oAux, headCopied);
	}

	@Override
	public PQueue<T> pop() {
		return create(head.pop(), tail, iExtra, iFrom, oNew, oFrom, oAux, headCopied);
	}

	/**
	 * Test whether is transferring elements
	 */
	protected boolean isTransferring() {
		return iFrom != null;
	}

	/**
	 * Perform two incremental steps
	 */
	private static <T> RealtimeExtraQueue<T> step(final RealtimeExtraQueue<T> q) {
		if (!q.isTransferring())
			return q;

		PStack<T> head = q.head;
		PStack<T> tail = q.tail;
		PStack<T> iExtra = q.iExtra;
		PStack<T> iFrom = q.iFrom;
		PStack<T> oNew = q.oNew;
		PStack<T> oFrom = q.oFrom;
		PStack<T> oAux = q.oAux;
		int headCopied = q.headCopied;

		if (!iFrom.isEmpty()) {
			oNew = oNew.push(iFrom.top());
			iFrom = iFrom.pop();
			return new RealtimeExtraQueue<>(head, tail, iExtra, iFrom, oNew, oFrom, oAux, headCopied);
		}

		if (!oFrom.isEmpty()) {
			oAux = oAux.push(oFrom.top());
			oFrom = oFrom.pop();
			return new RealtimeExtraQueue<>(head, tail, iExtra, iFrom, oNew, oFrom, oAux, headCopied);
		}

		if (!oAux.isEmpty() && headCopied < head.size()) {
			headCopied++;
			oNew = oNew.push(oAux.top());
			oAux = oAux.pop();
		}

		if (headCopied == head.size()) {
			head = oNew;
			tail = iExtra;
			iExtra = null;
			iFrom = null;
			oNew = null;
			oFrom = null;
			oAux = null;
			headCopied = 0;
		}

		return new RealtimeExtraQueue<>(head, tail, iExtra, iFrom, oNew, oFrom, oAux, headCopied);
	}

	private static <T> PQueue<T> create(PStack<T> head, PStack<T> tail, PStack<T> iExtra, PStack<T> iFrom, PStack<T> oNew,
			PStack<T> oFrom, PStack<T> oAux, int headCopied) {
		boolean init = iFrom == null;
		RealtimeExtraQueue<T> ret = new RealtimeExtraQueue<>(head, tail, iExtra, iFrom, oNew, oFrom, oAux, headCopied);
		init = init && ret.isTransferring();
		if (init) {
			ret = step(ret);
			ret = step(ret);
			ret = step(ret);
			ret = step(ret);
		}
		ret = step(ret);
		ret = step(ret);
		ret = step(ret);
		return ret;
	}
}