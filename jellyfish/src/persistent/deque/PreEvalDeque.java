package persistent.deque;

import persistent.PDeque;
import persistent.PStack;
import persistent.helper.Append;
import persistent.helper.Drop;
import persistent.helper.Rev;
import persistent.helper.Take;
import persistent.util.PCollections;

/**
 * Paper: "Simple and efficient purely functional queues and deques", Chris
 * Okasaki
 *
 * <p>
 * Invariants: {@literal |L| <= c|R|+1 and |R| <= c|L|+1}
 * </p>
 * 
 * <p>
 * Invariants: {@literal |^L| <= max(2j+2-k, 0) and |^R| <= max(2j+2-k, 0)},
 * where {@literal j = min(|L|, |R|) and k = max(|L|, |R|)}
 * </p>
 * 
 * @author morrisy
 *
 * @param <T> The type of element
 */
public class PreEvalDeque<T> extends PDeque<T> {
	private static final int C = 3;
	@SuppressWarnings("rawtypes")
	private static final PreEvalDeque<?> EMPTY = new PreEvalDeque();

	@SuppressWarnings("unchecked")
	public static <T> PreEvalDeque<T> create() {
		return (PreEvalDeque<T>) EMPTY;
	}

	private final PStack<T> l;
	private final PStack<T> r;

	private static class Rot1<T> extends PStack<T> {
		private int n;
		private final PStack<T> l;
		private PStack<T> r;
		private final int size;
		private final T top;

		public Rot1(int n, PStack<T> l, PStack<T> r) {
			this.n = n;
			this.l = l;
			this.r = r;
			this.size = l.size() + r.size() - n;
			this.top = l.top();
			assert this.n >= 0 && this.n < r.size();
			assert !l.isEmpty();
			assert n >= C;
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
			return Append.create(value, this);
		}

		@Override
		public PStack<T> pop() {
			if (size == 1)
				return PCollections.emptyStack();
			if (n >= C)
				return Rot1.create(n - C, l.pop(), Drop.create(C, r));
			return Rot2.create(l.pop(), Drop.create(n, r), PCollections.emptyStack());
		}

		private PStack<T> step() {
			if (n >= C) {
				n -= C;
				r = Drop.drop(C, r);
				return this;
			}
			return Rot2.create(l, Drop.create(n, r), PCollections.emptyStack());
		}

		private static <T> PStack<T> create(int n, PStack<T> l, PStack<T> r) {
			if (n >= C)
				return new Rot1<>(n, l, r);
			return Rot2.create(l, Drop.create(n, r), PCollections.emptyStack());
		}
	}

	private static class Rot2<T> extends PStack<T> {
		private final PStack<T> l;
		private PStack<T> r;
		private PStack<T> a;
		private final int size;
		private final T top;

		public Rot2(PStack<T> l, PStack<T> r, PStack<T> a) {
			this.l = l;
			this.r = r;
			this.a = a;
			this.size = l.size() + r.size() + a.size();
			this.top = l.top();
			assert this.size > 0;
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
			return Append.create(value, this);
		}

		@Override
		public PStack<T> pop() {
			if (size == 1)
				return PCollections.emptyStack();
			if (r.size() >= C)
				return create(l.pop(), Drop.create(C, r), Append.create(Rev.create(Take.take(C, r)), a));
			return create(l.pop(), r, a);
		}

		private PStack<T> step() {
			if (r.size() >= C) {
				a = Append.create(Rev.create(Take.take(C, r)), a);
				r = Drop.drop(C, r);
				return this;
			}
			return Append.create(l, Append.create(Rev.create(r), a));
		}

		private static <T> PStack<T> create(PStack<T> l, PStack<T> r, PStack<T> a) {
			if (!l.isEmpty() && r.size() >= C)
				return new Rot2<>(l, r, a);
			return Append.create(l, Append.create(Rev.create(r), a));
		}
	}

	private PreEvalDeque() {
		this.l = PCollections.emptyStack();
		this.r = l;
	}

	private PreEvalDeque(PStack<T> l, PStack<T> r) {
		if (l instanceof Rot1) {
			Rot1<T> rot = (Rot1<T>) l;
			l = rot.step();
		}
		if (r instanceof Rot1) {
			Rot1<T> rot = (Rot1<T>) r;
			r = rot.step();
		}
		if (l instanceof Rot2) {
			Rot2<T> rot = (Rot2<T>) l;
			l = rot.step();
		}
		if (r instanceof Rot2) {
			Rot2<T> rot = (Rot2<T>) r;
			r = rot.step();
		}
		if (l.size() > C * r.size() + 1) {
			int n = (l.size() + r.size()) / 2;
			PStack<T> ll = Take.create(n, l);
			PStack<T> rr = n == 0 ? Append.create(r, l) : Rot1.create(n, r, l);
			this.l = ll;
			this.r = rr;
		} else if (r.size() > C * l.size() + 1) {
			int n = (l.size() + r.size()) / 2;
			PStack<T> ll = n == 0 ? Append.create(l, r) : Rot1.create(n, l, r);
			PStack<T> rr = Take.create(n, r);
			this.l = ll;
			this.r = rr;
		} else {
			this.l = l;
			this.r = r;
		}
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public int size() {
		return l.size() + r.size();
	}

	@Override
	public T front() {
		if (!l.isEmpty())
			return l.top();
		return r.top();
	}

	@Override
	public T back() {
		if (!r.isEmpty())
			return r.top();
		return l.top();
	}

	@Override
	public PDeque<T> pushFront(T value) {
		return new PreEvalDeque<>(l.push(value), r);
	}

	@Override
	public PDeque<T> pushBack(T value) {
		return new PreEvalDeque<>(l, r.push(value));
	}

	@Override
	public PDeque<T> popFront() {
		if (!l.isEmpty())
			return new PreEvalDeque<>(l.pop(), r);
		assert r.size() == 1 : r.size();
		return create();
	}

	@Override
	public PDeque<T> popBack() {
		if (!r.isEmpty())
			return new PreEvalDeque<>(l, r.pop());
		assert l.size() == 1;
		return create();
	}
}
