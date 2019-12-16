package persistent.queue;

import persistent.PQueue;
import persistent.stack.PersistStack;

/**
 * Paper: "Real Time Queue Operations in Pure LISP", Hood, Robert T. & Melville, Robert C.
 * 
 * @author morrisy
 *
 * @param <T> The type of element
 */
public class RealtimeQueue<T> implements PQueue<T> {
	@SuppressWarnings("rawtypes")
	private static final RealtimeQueue<?> EMPTY = new RealtimeQueue();

	@SuppressWarnings("unchecked")
	public static <T> RealtimeQueue<T> create() {
		return (RealtimeQueue<T>) EMPTY;
	}

	private final PersistStack<T> head;
	private final PersistStack<T> tail;

	private final PersistStack<T> tailReverseFrom;
	private final PersistStack<T> tailReverseTo;
	private final PersistStack<T> headReverseFrom;
	private final PersistStack<T> headReverseTo;
	private final int headCopied;

	private RealtimeQueue() {
		this(PersistStack.create(), PersistStack.create(), null, null, null, null, 0);
	}

	private RealtimeQueue(PersistStack<T> head, PersistStack<T> tail, PersistStack<T> tailReverseFrom,
			PersistStack<T> tailReverseTo, PersistStack<T> headReverseFrom, PersistStack<T> headReverseTo,
			int headCopied) {
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
				this.tail = PersistStack.create();
				this.tailReverseFrom = null;
				this.tailReverseTo = null;
				this.headReverseFrom = null;
				this.headReverseTo = null;
			} else {
				this.head = head;
				this.tail = PersistStack.create();

				this.tailReverseFrom = tail;
				this.tailReverseTo = PersistStack.create();
				this.headReverseFrom = head;
				this.headReverseTo = PersistStack.create();
			}
		}
	}

	public boolean isEmpty() {
		return head.isEmpty() && tail.isEmpty();
	}

	public int size() {
		int size = head.size() + tail.size() - headCopied;
		if (tailReverseTo != null) {
			size += tailReverseTo.size() + tailReverseFrom.size();
		}
		return size;
	}

	public RealtimeQueue<T> clear() {
		return create();
	}

	public T front() {
		return head.top();
	}

	public RealtimeQueue<T> push(T value) {
		return newPersistQueue(head, tail.push(value), tailReverseFrom, tailReverseTo, headReverseFrom,
				headReverseTo, headCopied);
	}

	public RealtimeQueue<T> pop() {
		return newPersistQueue(head.pop(), tail, tailReverseFrom, tailReverseTo, headReverseFrom, headReverseTo,
				headCopied);
	}

	private boolean needsStep() {
		return tailReverseFrom != null && tailReverseTo != null && headReverseFrom != null && headReverseTo != null;
	}

	private static <T> RealtimeQueue<T> step(PersistStack<T> head, PersistStack<T> tail,
			PersistStack<T> tailReverseFrom, PersistStack<T> tailReverseTo, PersistStack<T> headReverseFrom,
			PersistStack<T> headReverseTo, int headCopied) {
		assert (tailReverseFrom != null && tailReverseTo != null && headReverseFrom != null
				&& headReverseTo != null) : "Internal error: invariant failure.";

		if (!tailReverseFrom.isEmpty()) {
			tailReverseTo = tailReverseTo.push(tailReverseFrom.top());
			tailReverseFrom = tailReverseFrom.pop();
		}
		if (!tailReverseFrom.isEmpty()) {
			tailReverseTo = tailReverseTo.push(tailReverseFrom.top());
			tailReverseFrom = tailReverseFrom.pop();
		}

		if (!headReverseFrom.isEmpty()) {
			headReverseTo = headReverseTo.push(headReverseFrom.top());
			headReverseFrom = headReverseFrom.pop();
		}
		if (!headReverseFrom.isEmpty()) {
			headReverseTo = headReverseTo.push(headReverseFrom.top());
			headReverseFrom = headReverseFrom.pop();
		}

		if (tailReverseFrom.isEmpty()) {
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
			}
		}
		return new RealtimeQueue<>(head, tail, tailReverseFrom, tailReverseTo, headReverseFrom, headReverseTo,
				headCopied);
	}

	private static <T> RealtimeQueue<T> newPersistQueue(PersistStack<T> head, PersistStack<T> tail,
			PersistStack<T> tailReverseFrom, PersistStack<T> tailReverseTo, PersistStack<T> headReverseFrom,
			PersistStack<T> headReverseTo, int headCopied) {
		RealtimeQueue<T> ret = new RealtimeQueue<>(head, tail, tailReverseFrom, tailReverseTo, headReverseFrom,
				headReverseTo, headCopied);
		if (ret.needsStep())
			ret = step(ret.head, ret.tail, ret.tailReverseFrom, ret.tailReverseTo, ret.headReverseFrom,
					ret.headReverseTo, ret.headCopied);
		if (ret.needsStep())
			ret = step(ret.head, ret.tail, ret.tailReverseFrom, ret.tailReverseTo, ret.headReverseFrom,
					ret.headReverseTo, ret.headCopied);
		return ret;
	}
}