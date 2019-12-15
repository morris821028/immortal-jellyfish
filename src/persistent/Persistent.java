package persistent;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Scanner;

public class Persistent {
	public static interface PStack<T> {
		public boolean isEmpty();

		public long size();

		public T top();

		public PStack<T> push(T value);

		public PStack<T> pop();
	}
	
	public static class AppendStack<T> implements PStack<T> {
		private final PStack<T> l;
		private final PStack<T> r;
		private final long size;
		
		private AppendStack(PStack<T> l, PStack<T> r) {
			assert !l.isEmpty();
			this.l = l;
			this.r = r;
			this.size = l.size() + r.size();
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		@Override
		public long size() {
			return size;
		}

		@Override
		public T top() {
			return l.top();
		}

		@Override
		public PStack<T> push(T value) {
			return new AppendStack<>(l.push(value), r);
		}

		@Override
		public PStack<T> pop() {
			if (l.size() == 1)
				return r;
			return new AppendStack<>(l.pop(), r);
		}

		public static <T> PStack<T> append(PStack<T> l, PStack<T> r) {
			if (l.isEmpty())
				return r;
			if (r.isEmpty())
				return l;
			return new AppendStack<>(l, r);
		}
	}

	/**
	 * Paper: "Real Time Queue Operations in Pure LISP", Hood, Robert T. & Melville,
	 * Robert C.
	 * 
	 * @author morrisy
	 *
	 * @param <T> The type of element
	 */
	public static class PersistStack<T> implements PStack<T> {
		@SuppressWarnings("rawtypes")
		private static final PersistStack<?> EMPTY = new PersistStack();

		@SuppressWarnings("unchecked")
		public static <T> PersistStack<T> create() {
			return (PersistStack<T>) EMPTY;
		}

		private final T value;
		private final PersistStack<T> next;
		private final long size;

		private PersistStack() {
			this(null, null, 0);
		}

		private PersistStack(T value, PersistStack<T> next, long size) {
			this.value = value;
			this.next = next;
			this.size = size;
		}

		public boolean isEmpty() {
			return size == 0;
		}

		public long size() {
			return size;
		}

		public PersistStack<T> clear() {
			return create();
		}

		public T top() {
			if (isEmpty())
				return null;
			return value;
		}

		public PersistStack<T> push(T value) {
			return new PersistStack<>(value, this, size + 1);
		}

		public PersistStack<T> pop() {
			return next != null ? next : create();
		}
	}

	/** 
	 * @author morrisy
	 *
	 * @param <T> The type of element
	 */
	public static class PersistQueue<T> {
		@SuppressWarnings("rawtypes")
		private static final PersistQueue<?> EMPTY = new PersistQueue();

		@SuppressWarnings("unchecked")
		public static <T> PersistQueue<T> create() {
			return (PersistQueue<T>) EMPTY;
		}

		private final PersistStack<T> head;
		private final PersistStack<T> tail;

		private final PersistStack<T> tailReverseFrom;
		private final PersistStack<T> tailReverseTo;
		private final PersistStack<T> headReverseFrom;
		private final PersistStack<T> headReverseTo;
		private final long headCopied;

		private PersistQueue() {
			this(PersistStack.create(), PersistStack.create(), null, null, null, null, 0);
		}

		private PersistQueue(PersistStack<T> head, PersistStack<T> tail, PersistStack<T> tailReverseFrom,
				PersistStack<T> tailReverseTo, PersistStack<T> headReverseFrom, PersistStack<T> headReverseTo,
				long headCopied) {
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

		public long size() {
			long size = head.size() + tail.size() - headCopied;
			if (tailReverseTo != null) {
				size += tailReverseTo.size() + tailReverseFrom.size();
			}
			return size;
		}

		public PersistQueue<T> clear() {
			return create();
		}

		public T front() {
			return head.top();
		}

		public PersistQueue<T> push(T value) {
			return newPersistQueue(head, tail.push(value), tailReverseFrom, tailReverseTo, headReverseFrom,
					headReverseTo, headCopied);
		}

		public PersistQueue<T> pop() {
			return newPersistQueue(head.pop(), tail, tailReverseFrom, tailReverseTo, headReverseFrom, headReverseTo,
					headCopied);
		}

		private boolean needsStep() {
			return tailReverseFrom != null && tailReverseTo != null && headReverseFrom != null && headReverseTo != null;
		}

		private static <T> PersistQueue<T> step(PersistStack<T> head, PersistStack<T> tail,
				PersistStack<T> tailReverseFrom, PersistStack<T> tailReverseTo, PersistStack<T> headReverseFrom,
				PersistStack<T> headReverseTo, long headCopied) {
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
			return new PersistQueue<>(head, tail, tailReverseFrom, tailReverseTo, headReverseFrom, headReverseTo,
					headCopied);
		}

		private static <T> PersistQueue<T> newPersistQueue(PersistStack<T> head, PersistStack<T> tail,
				PersistStack<T> tailReverseFrom, PersistStack<T> tailReverseTo, PersistStack<T> headReverseFrom,
				PersistStack<T> headReverseTo, long headCopied) {
			PersistQueue<T> ret = new PersistQueue<>(head, tail, tailReverseFrom, tailReverseTo, headReverseFrom,
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

	private static void testGoldenStack() {
		Deque<Integer> stk = new ArrayDeque<>();
		for (int i = 0; i < 1000000; i++) {
			stk.push(i);
		}

		for (int i = 1000000 - 1; i >= 0; i--) {
			Integer v = stk.peekFirst();
			assert v == i;
			stk.pop();
		}
		System.out.println("testStack() pass");
	}
	
	private static void testStack() {
		PersistStack<Integer> stk = PersistStack.create();
		PersistStack<Integer> stk1 = stk.push(1);
		PersistStack<Integer> stk2 = stk1.push(2);
		PersistStack<Integer> stk3 = stk2.pop();
		PersistStack<Integer> stk4 = stk3.push(3);

		assert stk1.top() == 1;
		assert stk2.top() == 2;
		assert stk3.top() == 1;
		assert stk4.top() == 3;

		stk = PersistStack.create();
		for (int i = 0; i < 1000000; i++) {
			stk = stk.push(i);
		}
		for (int i = 0; i < 1000000; i++) {
			PersistStack<Integer> t = stk.push(1);
			Integer v = t.top();
			assert v == 1;
		}
		for (int i = 1000000 - 1; i >= 0; i--) {
			Integer v = stk.top();
			assert v == i;
			stk = stk.pop();
		}
		System.out.println("testStack() pass");
	}

	private static void testQueue() {
		PersistQueue<Integer> que = PersistQueue.create();
		PersistQueue<Integer> que1 = que.push(1);
		PersistQueue<Integer> que2 = que1.push(2);
		PersistQueue<Integer> que3 = que2.pop();
		PersistQueue<Integer> que4 = que3.push(3);

		assert que1.front() == 1;
		assert que2.front() == 1;
		assert que3.front() == 2;
		assert que4.front() == 2;

		que = PersistQueue.create();
		for (int i = 0; i < 1000000; i++) {
			que = que.push(i);
			assert que.size() == i + 1;
		}
		for (int i = 0; i < 1000000; i++) {
			PersistQueue<Integer> t = que.push(1);
			Integer v = t.front();
			assert v == 0;
		}
		for (int i = 0; i < 1000000; i++) {
			Integer v = que.front();
			assert v == i;
			que = que.pop();
			assert que.size() == 1000000 - i - 1;
		}
		System.out.println("testQueue() pass");
	}

	private static void testQueue2() {
		PreEvalQueue<Integer> que = PreEvalQueue.create();
		PreEvalQueue<Integer> que1 = que.push(1);
		PreEvalQueue<Integer> que2 = que1.push(2);
		PreEvalQueue<Integer> que3 = que2.pop();
		PreEvalQueue<Integer> que4 = que3.push(3);

		assert que1 != null;
		assert que1.front() == 1;
		assert que2.front() == 1;
		assert que3.front() == 2;
		assert que4.front() == 2;

		que = PreEvalQueue.create();
		for (int i = 0; i < 1000000; i++) {
			que = que.push(i);
			assert que.size() == i + 1;
		}
		for (int i = 0; i < 1000000; i++) {
			PreEvalQueue<Integer> t = que.push(1);
			Integer v = t.front();
			assert v == 0;
		}
		for (int i = 0; i < 1000000; i++) {
			Integer v = que.front();
			assert v != null && v == i : String.format("%s %d", v, i);
			que = que.pop();
			assert que.size() == 1000000 - i - 1;
		}
		System.out.println("testQueue2() pass");
	}

	public static void main(String[] args) {
//		Scanner cin = new Scanner(System.in);
//		cin.next();
		test(() -> testGoldenStack());
		test(() -> testStack());
		test(() -> testQueue());
		test(() -> testQueue2());
//		test(() -> testDqueue());
//		try {
//			assert false;
//			System.out.println("test fail, please set up -ea");
//		} catch (Exception e) {
//			System.out.println("test successful");
//		}
	}

	private static void test(Runnable r) {
		System.gc();
		long start = System.nanoTime();
		r.run();
		long time = System.nanoTime() - start;
		System.out.printf("Time: %f\n\n", time / 10e+9);
	}
}
