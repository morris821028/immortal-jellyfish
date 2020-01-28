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
public class RealtimeQueue<T> extends PQueue<T> {
	@SuppressWarnings("rawtypes")
	private static final RealtimeQueue<?> EMPTY = new RealtimeQueue();

	@SuppressWarnings("unchecked")
	public static <T> RealtimeQueue<T> create() {
		return (RealtimeQueue<T>) EMPTY;
	}

	private static class TransferQueue<T> extends RealtimeQueue<T> {
		private final PStack<T> tailReverseFrom;
		private final PStack<T> tailReverseTo;
		private final PStack<T> headReverseFrom;
		private final PStack<T> headReverseTo;
		private final int headCopied;

		private TransferQueue(PStack<T> head, PStack<T> tail, PStack<T> tailReverseFrom, PStack<T> tailReverseTo,
				PStack<T> headReverseFrom, PStack<T> headReverseTo, int headCopied) {
			super(head, tail);
			this.headCopied = headCopied;

			this.tailReverseFrom = tailReverseFrom;
			this.tailReverseTo = tailReverseTo;
			this.headReverseFrom = headReverseFrom;
			this.headReverseTo = headReverseTo;
		}

		@Override
		public int size() {
			return super.size() + tailReverseTo.size() + tailReverseFrom.size() - headCopied;
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

		private static <T> PQueue<T> create(PStack<T> head, PStack<T> tail, PStack<T> tailReverseFrom,
				PStack<T> tailReverseTo, PStack<T> headReverseFrom, PStack<T> headReverseTo, int headCopied) {
			if (tail.size() > head.size()) {
				assert tailReverseFrom == null && tailReverseTo == null && headReverseFrom == null
						&& headReverseTo == null : "Internal error: invariant failure.";
				if (tail.size() == 1) {
					head = tail;
					tail = PCollections.emptyStack();
					return new RealtimeQueue<>(head, tail);
				} else {
					// initiate the transfer process
					tailReverseFrom = tail;
					tailReverseTo = PCollections.emptyStack();
					headReverseFrom = head;
					headReverseTo = PCollections.emptyStack();

					tail = PCollections.emptyStack();
				}
			}

			return step(head, tail, tailReverseFrom, tailReverseTo, headReverseFrom, headReverseTo, headCopied, 3);
		}
	}

	final PStack<T> head;
	final PStack<T> tail;

	private RealtimeQueue() {
		this(PCollections.emptyStack(), PCollections.emptyStack());
	}

	private RealtimeQueue(PStack<T> head, PStack<T> tail) {
		this.head = head;
		this.tail = tail;
	}

	@Override
	public boolean isEmpty() {
		return head.isEmpty() && tail.isEmpty();
	}

	@Override
	public int size() {
		return head.size() + tail.size();
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
		return create(head, tail.push(value));
	}

	@Override
	public PQueue<T> pop() {
		return create(head.pop(), tail);
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
						return new RealtimeQueue<>(head, tail);
					}
				}
			}
		}

		if (tailReverseFrom == null)
			return new RealtimeQueue<>(head, tail);
		return new TransferQueue<>(head, tail, tailReverseFrom, tailReverseTo, headReverseFrom, headReverseTo,
				headCopied);
	}

	private static <T> PQueue<T> create(PStack<T> head, PStack<T> tail) {
		if (tail.size() > head.size()) {
			if (tail.size() == 1) {
				head = tail;
				tail = PCollections.emptyStack();
			} else {
				// initiate the transfer process
				PStack<T> tailReverseFrom = tail;
				PStack<T> tailReverseTo = PCollections.emptyStack();
				PStack<T> headReverseFrom = head;
				PStack<T> headReverseTo = PCollections.emptyStack();

				tail = PCollections.emptyStack();
				return step(head, tail, tailReverseFrom, tailReverseTo, headReverseFrom, headReverseTo, 0, 4);
			}
		}
		return new RealtimeQueue<>(head, tail);
	}
}
