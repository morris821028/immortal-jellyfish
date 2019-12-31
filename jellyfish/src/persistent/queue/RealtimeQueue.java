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
		this.head = head;
		this.tail = tail;

		this.tailReverseFrom = tailReverseFrom;
		this.tailReverseTo = tailReverseTo;
		this.headReverseFrom = headReverseFrom;
		this.headReverseTo = headReverseTo;
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
	 * Perform incremental steps
	 */
	private static <T> RealtimeQueue<T> step(PStack<T> head, PStack<T> tail, PStack<T> tailReverseFrom,
			PStack<T> tailReverseTo, PStack<T> headReverseFrom, PStack<T> headReverseTo, int headCopied, int cost) {
		if (tailReverseFrom != null) {
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
		}
		return new RealtimeQueue<>(head, tail, tailReverseFrom, tailReverseTo, headReverseFrom, headReverseTo,
				headCopied);
	}

	private static <T> PQueue<T> create(PStack<T> head, PStack<T> tail, PStack<T> tailReverseFrom,
			PStack<T> tailReverseTo, PStack<T> headReverseFrom, PStack<T> headReverseTo, int headCopied) {
		boolean init = tailReverseFrom == null;

		if (tail.size() > head.size()) {
			assert tailReverseFrom == null && tailReverseTo == null && headReverseFrom == null
					&& headReverseTo == null : "Internal error: invariant failure.";
			if (tail.size() == 1) {
				head = tail;
				tail = PCollections.emptyStack();
			} else {
				// initiate the transfer process
				tailReverseFrom = tail;
				tailReverseTo = PCollections.emptyStack();
				headReverseFrom = head;
				headReverseTo = PCollections.emptyStack();

				tail = PCollections.emptyStack();
			}
		}

		if (init && tailReverseFrom != null) {
			return step(head, tail, tailReverseFrom, tailReverseTo, headReverseFrom, headReverseTo, headCopied, 4);
		} else {
			return step(head, tail, tailReverseFrom, tailReverseTo, headReverseFrom, headReverseTo, headCopied, 3);
		}
	}
}
