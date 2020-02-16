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

	private static class TransPrevQueue<T> extends RealtimeQueue<T> {
		private final PStack<T> headRevFrom;
		private final PStack<T> headRevTo;
		private final PStack<T> tailRevFrom;

		private TransPrevQueue(PStack<T> head, PStack<T> tail, PStack<T> tailRevFrom, PStack<T> headRevFrom,
				PStack<T> headRevTo) {
			super(head, tail);

			this.headRevTo = headRevTo;
			this.headRevFrom = headRevFrom;
			this.tailRevFrom = tailRevFrom;
		}

		@Override
		public int size() {
			return super.size() + tailRevFrom.size();
		}

		@Override
		public PQueue<T> push(T value) {
			return createTrans(head, tail.push(value));
		}

		@Override
		public PQueue<T> pop() {
			return createTrans(head.pop(), tail);
		}

		private PQueue<T> createTrans(PStack<T> head, PStack<T> tail) {
			return stepPrev(head, tail, tailRevFrom, PCollections.emptyStack(), headRevFrom, headRevTo, 3);
		}
	}

	private static class TransMidQueue<T> extends RealtimeQueue<T> {
		private final PStack<T> tailRevFrom;
		private final PStack<T> tailRevTo;
		private final PStack<T> headRevTo;

		private TransMidQueue(PStack<T> head, PStack<T> tail, PStack<T> tailRevFrom, PStack<T> tailRevTo,
				PStack<T> headRevTo) {
			super(head, tail);

			this.tailRevFrom = tailRevFrom;
			this.tailRevTo = tailRevTo;
			this.headRevTo = headRevTo;
		}

		@Override
		public int size() {
			return super.size() + tailRevTo.size() + tailRevFrom.size();
		}

		@Override
		public PQueue<T> push(T value) {
			return createTrans(head, tail.push(value));
		}

		@Override
		public PQueue<T> pop() {
			return createTrans(head.pop(), tail);
		}

		private PQueue<T> createTrans(PStack<T> head, PStack<T> tail) {
			return stepPrev(head, tail, tailRevFrom, tailRevTo, PCollections.emptyStack(), headRevTo, 3);
		}
	}

	private static class TransPostQueue<T> extends RealtimeQueue<T> {
		private final PStack<T> tailRevTo;
		private final PStack<T> headRevTo;
		private final int headCopied;

		private TransPostQueue(PStack<T> head, PStack<T> tail, PStack<T> tailRevTo, PStack<T> headRevTo,
				int headCopied) {
			super(head, tail);
			this.headCopied = headCopied;
			this.tailRevTo = tailRevTo;
			this.headRevTo = headRevTo;
		}

		@Override
		public int size() {
			return super.size() + tailRevTo.size() - headCopied;
		}

		@Override
		public PQueue<T> push(T value) {
			return createTrans(head, tail.push(value));
		}

		@Override
		public PQueue<T> pop() {
			return createTrans(head.pop(), tail);
		}

		private PQueue<T> createTrans(PStack<T> head, PStack<T> tail) {
			return stepPost(head, tail, tailRevTo, headRevTo, headCopied, 3);
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

	private static <T> RealtimeQueue<T> stepPost(PStack<T> head, PStack<T> tail, PStack<T> tailRevTo,
			PStack<T> headRevTo, int headCopied, int cost) {
		int target = head.size();
		while (cost > 0 && headCopied < target) {
			cost--;

			headCopied++;
			tailRevTo = tailRevTo.push(headRevTo.top());
			headRevTo = headRevTo.pop();
		}

		if (headCopied == target) {
			head = tailRevTo;
			return new RealtimeQueue<>(head, tail);
		}
		return new TransPostQueue<>(head, tail, tailRevTo, headRevTo, headCopied);
	}

	/**
	 * Perform incremental steps
	 */
	private static <T> RealtimeQueue<T> stepPrev(PStack<T> head, PStack<T> tail, PStack<T> tailRevFrom,
			PStack<T> tailRevTo, PStack<T> headRevFrom, PStack<T> headRevTo, int cost) {
		while (cost > 0) {
			cost--;
			if (!headRevFrom.isEmpty()) {
				headRevTo = headRevTo.push(headRevFrom.top());
				headRevFrom = headRevFrom.pop();
			} else if (!tailRevFrom.isEmpty()) {
				tailRevTo = tailRevTo.push(tailRevFrom.top());
				tailRevFrom = tailRevFrom.pop();
			} else if (tailRevFrom.isEmpty()) {
				return stepPost(head, tail, tailRevTo, headRevTo, 0, cost + 1);
			}
		}

		if (tailRevTo.isEmpty())
			return new TransPrevQueue<>(head, tail, tailRevFrom, headRevFrom, headRevTo);
		return new TransMidQueue<>(head, tail, tailRevFrom, tailRevTo, headRevTo);
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
				return stepPrev(head, tail, tailReverseFrom, tailReverseTo, headReverseFrom, headReverseTo, 4);
			}
		}
		return new RealtimeQueue<>(head, tail);
	}
}
