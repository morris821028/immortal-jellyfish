package persistent.queue;

import persistent.PQueue;
import persistent.PStack;
import persistent.util.PCollections;

/**
 * Paper: "Real Time Queue Operations in Pure LISP", Hood, Robert T. & Melville,
 * Robert C.
 * 
 * @author morrisy
 *
 * @param <T> The type of element
 */
public final class RealtimeQueue<T> extends PQueue<T> {
	@SuppressWarnings("rawtypes")
	private static final RealtimeQueue<?> EMPTY = new RealtimeQueue();

	@SuppressWarnings("unchecked")
	public static <T> RealtimeQueue<T> create() {
		return (RealtimeQueue<T>) EMPTY;
	}

	private final PStack<T> head;
	private final PStack<T> tail;

	private final PStack<T> tailReverseFrom;
	private final PStack<T> tailReverseTo;
	private final PStack<T> headReverseFrom;
	private final PStack<T> headReverseTo;
	private final int headCopied;

	private RealtimeQueue() {
		this(PCollections.emptyStack(), PCollections.emptyStack(), null, null, null, null, 0);
	}

	private RealtimeQueue(PStack<T> head, PStack<T> tail, PStack<T> tailReverseFrom, PStack<T> tailReverseTo,
			PStack<T> headReverseFrom, PStack<T> headReverseTo, int headCopied) {
		this.headCopied = headCopied;
		if (tail.size() <= head.size()) {
			this.head = head;
			this.tail = tail;

			this.tailReverseFrom = tailReverseFrom;
			this.tailReverseTo = tailReverseTo;
			this.headReverseFrom = headReverseFrom;
			this.headReverseTo = headReverseTo;
		} else {
			assert tailReverseFrom == null && tailReverseTo == null && headReverseFrom == null
					&& headReverseTo == null : "Internal error: invariant failure.";
			if (tail.size() == 1) {
				this.head = tail;
				this.tail = PCollections.emptyStack();
				this.tailReverseFrom = null;
				this.tailReverseTo = null;
				this.headReverseFrom = null;
				this.headReverseTo = null;
			} else {
				this.head = head;
				this.tail = PCollections.emptyStack();

				// initiate the transfer process
				this.tailReverseFrom = tail;
				this.tailReverseTo = PCollections.emptyStack();
				this.headReverseFrom = head;
				this.headReverseTo = PCollections.emptyStack();
			}
		}
	}

	@Override
	public boolean isEmpty() {
		return head.isEmpty() && tail.isEmpty();
	}

	@Override
	public int size() {
		int size = head.size() + tail.size();
		if (tailReverseTo != null) {
			size += tailReverseTo.size() + tailReverseFrom.size() - headCopied;
		}
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
		return create(head, tail.push(value), tailReverseFrom, tailReverseTo, headReverseFrom, headReverseTo,
				headCopied);
	}

	@Override
	public PQueue<T> pop() {
		return create(head.pop(), tail, tailReverseFrom, tailReverseTo, headReverseFrom, headReverseTo, headCopied);
	}

	/**
	 * Test whether is transferring elements
	 */
	private static boolean needsStep(final RealtimeQueue<?> q) {
		return q.tailReverseFrom != null;
	}

	/**
	 * Perform incremental steps
	 */
	private static <T> RealtimeQueue<T> step(final RealtimeQueue<T> q, int cost) {
		if (!needsStep(q))
			return q;

		PStack<T> head = q.head;
		PStack<T> tail = q.tail;
		PStack<T> tailReverseFrom = q.tailReverseFrom;
		PStack<T> tailReverseTo = q.tailReverseTo;
		PStack<T> headReverseFrom = q.headReverseFrom;
		PStack<T> headReverseTo = q.headReverseTo;
		int headCopied = q.headCopied;

		while (cost > 0) {
			cost--;
			if (!headReverseFrom.isEmpty()) {
				headReverseTo = headReverseTo.push(headReverseFrom.top());
				headReverseFrom = headReverseFrom.pop();
			} else if (!tailReverseFrom.isEmpty()) {
				tailReverseTo = tailReverseTo.push(tailReverseFrom.top());
				tailReverseFrom = tailReverseFrom.pop();
			} else if (tailReverseFrom.isEmpty()) {
				if (!headReverseTo.isEmpty() && headCopied < head.size()) {
					headCopied++;
					tailReverseTo = tailReverseTo.push(headReverseTo.top());
					headReverseTo = headReverseTo.pop();
				}

				if (headCopied == head.size()) {
					head = tailReverseTo;
					tailReverseFrom = null;
					tailReverseTo = null;
					headReverseFrom = null;
					headReverseTo = null;
					headCopied = 0;
					break;
				}
			}
		}
		return new RealtimeQueue<>(head, tail, tailReverseFrom, tailReverseTo, headReverseFrom, headReverseTo,
				headCopied);
	}

	private static <T> PQueue<T> create(PStack<T> head, PStack<T> tail, PStack<T> tailReverseFrom,
			PStack<T> tailReverseTo, PStack<T> headReverseFrom, PStack<T> headReverseTo, int headCopied) {
		boolean init = tailReverseFrom == null;
		RealtimeQueue<T> ret = new RealtimeQueue<>(head, tail, tailReverseFrom, tailReverseTo, headReverseFrom,
				headReverseTo, headCopied);
		if (init && needsStep(ret)) {
			ret = step(ret, 4);
		} else {
			ret = step(ret, 3);
		}
		return ret;
	}
}
