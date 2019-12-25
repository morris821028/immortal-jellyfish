package persistent.queue;

import persistent.PQueue;
import persistent.PStack;
import persistent.helper.Append;
import persistent.util.PCollections;

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
public final class PreEvalQueue<T> implements PQueue<T> {
	@SuppressWarnings("rawtypes")
	private static final PreEvalQueue<?> EMPTY = new PreEvalQueue();

	@SuppressWarnings("unchecked")
	public static <T> PreEvalQueue<T> create() {
		return (PreEvalQueue<T>) EMPTY;
	}

	private final PStack<T> l;
	private final PStack<T> r;
	private final int hsize;

	class Rot extends PStack<T> {
		private final PStack<T> l;
		private final PStack<T> r;
		private final PStack<T> a;
		private final int size;

		Rot(PStack<T> l, PStack<T> r) {
			this(l, r, PCollections.emptyStack());
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
			if (!l.isEmpty()) {
				return l.top();
			}
			if (!r.isEmpty()) {
				return r.top();
			}
			return a.top();
		}

		@Override
		public PStack<T> push(T value) {
			PStack<T> t = PStack.of(value);
			return Append.create(t, this);
		}

		@Override
		public PStack<T> pop() {
			if (r.size() == 1) {
				return a;
			}
			PStack<T> t = PStack.of(r.top());
			return new Rot(l.pop(), r.pop(), Append.create(t, a));
		}
	}

	private PreEvalQueue() {
		this.l = PCollections.emptyStack();
		this.r = l;
		this.hsize = 0;
	}

	private PreEvalQueue(PStack<T> l, PStack<T> r, int hsize) {
		if (hsize > 0) {
			this.l = l;
			this.r = r;
			this.hsize = hsize - 1;
		} else {
			this.r = PCollections.emptyStack();
			this.l = new Rot(l, r);
			this.hsize = this.l.size();
		}
	}

	@Override
	public boolean isEmpty() {
		return l.isEmpty();
	}

	@Override
	public int size() {
		return l.size() + r.size();
	}

	public PreEvalQueue<T> clear() {
		return create();
	}

	@Override
	public T front() {
		return l.top();
	}

	@Override
	public PreEvalQueue<T> push(T value) {
		return new PreEvalQueue<>(l, r.push(value), hsize);
	}

	@Override
	public PreEvalQueue<T> pop() {
		if (size() == 1)
			return create();
		return new PreEvalQueue<>(l.pop(), r, hsize);
	}
}
