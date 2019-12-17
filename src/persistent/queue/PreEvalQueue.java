package persistent.queue;

import persistent.PQueue;
import persistent.PStack;
import persistent.helper.Append;
import persistent.stack.PersistStack;

/**
 * Paper: "Simple and efficient purely functional queues and deques", Chris Okasaki
 *
 * <p>
 * Invariants: {@literal |R| <= |L| and |^L| = |L| - |R|}
 * </p>
 * 
 * @author morrisy
 *
 * @param <T> The type of element
 */
public class PreEvalQueue<T> implements PQueue<T> {
	@SuppressWarnings("rawtypes")
	private static final PreEvalQueue<?> EMPTY = new PreEvalQueue();

	@SuppressWarnings("unchecked")
	public static <T> PreEvalQueue<T> create() {
		return (PreEvalQueue<T>) EMPTY;
	}

	private final PStack<T> l;
	private final PStack<T> r;
	private final PStack<T> b;

	class Rot implements PStack<T> {
		private final PStack<T> l;
		private final PStack<T> r;
		private final PStack<T> a;
		private final int size;

		/** cache top element */
		private T top;
		/** cache immutable pop result */
		private PStack<T> pop;

		Rot(PStack<T> l, PStack<T> r) {
			this(l, r, PersistStack.create());
		}

		Rot(PStack<T> l, PStack<T> r, PStack<T> a) {
			this.l = l;
			this.r = r;
			this.a = a;
			this.size = l.size() + r.size() + a.size();
			assert this.size != 0;
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		@Override
		public int size() {
			return size;
		}

		@Override
		public T top() {
			if (top != null)
				return top;
			if (!l.isEmpty()) {
				top = l.top();
				return top;
			}
			if (!r.isEmpty()) {
				top = r.top();
				return top;
			}
			top = a.top();
			return top;
		}

		@Override
		public PStack<T> push(T value) {
			PStack<T> t = PersistStack.create();
			t = t.push(value);
			return Append.create(t, this);
		}

		@Override
		public PStack<T> pop() {
			if (pop != null)
				return pop;
			if (r.size() == 1) {
				pop = a;
				return a;
			}
			PStack<T> t = PersistStack.create();
			t = t.push(r.top());
			pop = new Rot(l.pop(), r.pop(), Append.create(t, a));
			return pop;
		}
	}

	private PreEvalQueue() {
		this.l = PersistStack.create();
		this.r = l;
		this.b = l;
	}

	private PreEvalQueue(PStack<T> l, PStack<T> r, PStack<T> b) {
		if (!b.isEmpty()) {
			this.l = l;
			this.r = r;
			this.b = b.pop();
		} else {
			this.r = PersistStack.create();
			this.l = new Rot(l, r);
			this.b = this.l;
		}
	}

	public boolean isEmpty() {
		return l.isEmpty();
	}

	public int size() {
		return l.size() + r.size();
	}

	public PreEvalQueue<T> clear() {
		return create();
	}

	public T front() {
		return l.top();
	}

	public PreEvalQueue<T> push(T value) {
		return new PreEvalQueue<>(l, r.push(value), b);
	}

	public PreEvalQueue<T> pop() {
		if (size() == 1)
			return create();
		return new PreEvalQueue<>(l.pop(), r, b);
	}
}
