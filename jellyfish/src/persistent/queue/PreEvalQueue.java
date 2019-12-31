package persistent.queue;

import persistent.PQueue;
import persistent.PStack;
import persistent.helper.Append;
import persistent.util.PCollections;

/**
 * Paper: "Simple and efficient purely functional queues and deques", Chris
 * Okasaki
 *
 * <p>
 * Invariants: {@literal |R| <= |L| and |^L| = |L| - |R|}
 * </p>
 * 
 * @author morrisy
 *
 * @param <T> The type of element
 */
public final class PreEvalQueue<T> extends PQueue<T> {
	@SuppressWarnings("rawtypes")
	private static final PreEvalQueue<?> EMPTY = new PreEvalQueue();

	@SuppressWarnings("unchecked")
	public static <T> PreEvalQueue<T> create() {
		return (PreEvalQueue<T>) EMPTY;
	}

	private final PStack<T> l;
	private final PStack<T> r;
	private final int hsize;

	static class Rot<T> extends PStack<T> {
		private final PStack<T> l;
		private PStack<T> r;
		private PStack<T> a;
		private final int size;
		private final T top;

		Rot(PStack<T> l, PStack<T> r) {
			this(l, r, PCollections.emptyStack());
		}

		Rot(PStack<T> l, PStack<T> r, PStack<T> a) {
			if (l instanceof Rot) {
				Rot<T> rot = (Rot<T>) l;
				assert rot.r.isEmpty();
				this.l = Append.create(rot.l, rot.a);
			} else {
				this.l = l;
			}
			this.r = r;
			this.a = a;
			this.size = l.size() + r.size() + a.size();
			if (!l.isEmpty())
				this.top = l.top();
			else if (!r.isEmpty())
				this.top = r.top();
			else
				this.top = a.top();
			assert this.top != null : String.format("%d %d %d", l.size(), r.size(), a.size());
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
			return top;
		}

		@Override
		public PStack<T> push(T value) {
			step();
			if (r.isEmpty())
				return Append.create(l.push(value), a);
			return Append.create(PStack.<T>of(value), this);
		}

		@Override
		public PStack<T> pop() {
			step();
			if (l.isEmpty()) {
				if (r.isEmpty())
					return a.pop();
				if (r.size() == 1)
					return a;
			}
			if (r.isEmpty())
				return Append.create(l.pop(), a);
			return new Rot<>(l.pop(), r.pop(), a.push(r.top()));
		}

		private Rot<T> step() {
			if (!r.isEmpty()) {
				a = a.push(r.top());
				r = r.pop();
			}
			return this;
		}

		public static <T> PStack<T> create(PStack<T> l, PStack<T> r) {
			if (r.size() <= 2) {
				PStack<T> t = PCollections.emptyStack();
				while (!r.isEmpty()) {
					t = t.push(r.top());
					r = r.pop();
				}
				return Append.create(l, t);
			}

			return new Rot<>(l, r).step();
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
			this.l = Rot.<T>create(l, r);

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
		if (l instanceof Rot) {
			Rot<T> rot = (Rot<T>) l;
			rot.step();
		}
		return new PreEvalQueue<>(l, r.push(value), hsize);
	}

	@Override
	public PreEvalQueue<T> pop() {
		if (size() == 1)
			return create();
		return new PreEvalQueue<>(l.pop(), r, hsize);
	}
}
