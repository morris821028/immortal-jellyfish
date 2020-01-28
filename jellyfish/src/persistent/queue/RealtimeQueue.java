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
		private final PStack<T> tailRevFrom;
		private final PStack<T> tailRevTo;
		private final PStack<T> headRevFrom;
		private final PStack<T> headRevTo;
		private final int headCopied;

		private TransferQueue(PStack<T> head, PStack<T> tail, PStack<T> tailRevFrom, PStack<T> tailRevTo,
				PStack<T> headRevFrom, PStack<T> headRevTo, int headCopied) {
			super(head, tail);
			this.headCopied = headCopied;

			this.tailRevFrom = tailRevFrom;
			this.tailRevTo = tailRevTo;
			this.headRevFrom = headRevFrom;
			this.headRevTo = headRevTo;
		}

		@Override
		public int size() {
			return super.size() + tailRevTo.size() + tailRevFrom.size() - headCopied;
		}

		@Override
		public PQueue<T> push(T value) {
			return create(head, tail.push(value), tailRevFrom, tailRevTo, headRevFrom, headRevTo, headCopied);
		}

		@Override
		public PQueue<T> pop() {
			return create(head.pop(), tail, tailRevFrom, tailRevTo, headRevFrom, headRevTo, headCopied);
		}

		private static <T> PQueue<T> create(PStack<T> head, PStack<T> tail, PStack<T> tailRevFrom, PStack<T> tailRevTo,
				PStack<T> headRevFrom, PStack<T> headRevTo, int headCopied) {
			return step(head, tail, tailRevFrom, tailRevTo, headRevFrom, headRevTo, headCopied, 3);
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
	private static <T> RealtimeQueue<T> step(PStack<T> head, PStack<T> tail, PStack<T> tailRevFrom, PStack<T> tailRevTo,
			PStack<T> headRevFrom, PStack<T> headRevTo, int headCopied, int cost) {
		while (cost > 0) {
			cost--;
			if (!headRevFrom.isEmpty()) {
				headRevTo = headRevTo.push(headRevFrom.top());
				headRevFrom = headRevFrom.pop();
			} else if (!tailRevFrom.isEmpty()) {
				tailRevTo = tailRevTo.push(tailRevFrom.top());
				tailRevFrom = tailRevFrom.pop();
			} else if (tailRevFrom.isEmpty()) {
				if (!headRevTo.isEmpty() && headCopied < head.size()) {
					headCopied++;
					tailRevTo = tailRevTo.push(headRevTo.top());
					headRevTo = headRevTo.pop();
				}

				if (headCopied == head.size()) {
					head = tailRevTo;
					return new RealtimeQueue<>(head, tail);
				}
			}
		}

		return new TransferQueue<>(head, tail, tailRevFrom, tailRevTo, headRevFrom, headRevTo, headCopied);
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
