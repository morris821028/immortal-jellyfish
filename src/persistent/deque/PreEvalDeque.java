package persistent.deque;

import persistent.PDeque;
import persistent.PStack;
import persistent.helper.Append;
import persistent.helper.Drop;
import persistent.helper.Rev;
import persistent.helper.Take;
import persistent.stack.PersistStack;

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
public class PreEvalDeque<T> implements PDeque<T> {
	private static final int C = 3;
	@SuppressWarnings("rawtypes")
	private static final PreEvalDeque<?> EMPTY = new PreEvalDeque();

	@SuppressWarnings("unchecked")
	public static <T> PreEvalDeque<T> create() {
		return (PreEvalDeque<T>) EMPTY;
	}

	private final PStack<T> l;
	private final PStack<T> r;
	private final PStack<T> lHat;
	private final PStack<T> rHat;

	private class Rot1 implements PStack<T> {
		private final int n;
		private final PStack<T> l;
		private final PStack<T> r;
		private final int size;

		private PStack<T> rx;
		private PStack<T> pop;

		public Rot1(int n, PStack<T> l, PStack<T> r) {
			this.n = n;
			this.l = l;
			this.r = r;
			this.size = l.size() + r.size() - n;
			assert this.n >= 0 && this.n < r.size();
		}

		@Override
		public boolean isEmpty() {
			return size() == 0;
		}

		@Override
		public int size() {
			return size;
		}

		@Override
		public T top() {
			if (!l.isEmpty())
				return l.top();
			return getReal().top();
		}

		@Override
		public PStack<T> push(T value) {
			return Append.create(value, this);
		}

		@Override
		public PStack<T> pop() {
			if (pop != null)
				return pop;
			if (n >= C) {
					assert !l.isEmpty();
					pop = new Rot1(n - C, l.pop(), Drop.create(C, r));
				assert pop.size() == this.size() - 1 : String.format("%d %b %s", l.size(), l.isEmpty(), pop.getClass());
				return pop;
			}
			pop = getReal().pop();
			assert pop.size() == this.size() - 1;
			return pop;
		}

		private PStack<T> getReal() {
			if (rx != null)
				return rx;
			assert n < C;
			rx = new Rot2(l, Drop.create(n, r), PersistStack.create());
			return rx;
		}
	}

	private class Rot2 implements PStack<T> {
		private final PStack<T> l;
		private final PStack<T> r;
		private final PStack<T> a;

		private PStack<T> rx;
		private PStack<T> pop;

		public Rot2(PStack<T> l, PStack<T> r, PStack<T> a) {
			this.l = l;
			this.r = r;
			this.a = a;
		}

		@Override
		public boolean isEmpty() {
			return size() == 0;
		}

		@Override
		public int size() {
			return l.size() + r.size() + a.size();
		}

		@Override
		public T top() {
			if (!l.isEmpty())
				return l.top();
			T v = getReal().top();
			assert v != null : String.format("%d %s %b", getReal().size(), getReal().getClass(), isEmpty());
			return v;
		}

		@Override
		public PStack<T> push(T value) {
			return Append.create(value, this);
		}

		@Override
		public PStack<T> pop() {
			if (pop != null)
				return pop;
			if (!l.isEmpty() && r.size() >= C) {
				pop = new Rot2(l.pop(), Drop.create(C, r), Append.create(Rev.create(Take.create(C, r)), a));
				return pop;
			}
			pop = getReal().pop();
			assert pop.size() == this.size() - 1;
			return pop;
		}

		private PStack<T> getReal() {
			if (rx != null)
				return rx;
			assert l.size() == 0 || r.size() < C;
			rx = Append.create(Append.create(l, Rev.create(r)), a);
			assert rx.size() == this.size();
			return rx;
		}
	}

	private PreEvalDeque() {
		l = PersistStack.create();
		r = l;
		lHat = l;
		rHat = l;
	}

	private PreEvalDeque(PStack<T> l, PStack<T> r, PStack<T> lHat, PStack<T> rHat) {
		if (l.size() > C * r.size() + 1) {
			int n = (l.size() + r.size()) / 2;
			PStack<T> ll = Take.create(n, l);
			PStack<T> rr = n == 0 ? Append.create(r, l) : new Rot1(n, r, l);
			this.l = ll;
			this.r = rr;
			this.lHat = ll;
			this.rHat = rr;
		} else if (r.size() > C * l.size() + 1) {
			int n = (l.size() + r.size()) / 2;
			PStack<T> ll = n == 0 ? Append.create(l, r) : new Rot1(n, l, r);
			PStack<T> rr = Take.create(n, r);
			this.l = ll;
			this.r = rr;
			this.lHat = ll;
			this.rHat = rr;
		} else {
			this.l = l;
			this.r = r;
			this.lHat = lHat;
			this.rHat = rHat;
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
		return new PreEvalDeque<>(l.push(value), r, lHat.pop(), rHat.pop());
	}

	@Override
	public PDeque<T> pushBack(T value) {
		return new PreEvalDeque<>(l, r.push(value), lHat.pop(), rHat.pop());
	}

	@Override
	public PDeque<T> popFront() {
		if (!l.isEmpty())
			return new PreEvalDeque<>(l.pop(), r, lHat.pop().pop(), rHat.pop().pop());
		assert r.size() == 1 : r.size();
		return create();
	}

	@Override
	public PDeque<T> popBack() {
		if (!r.isEmpty())
			return new PreEvalDeque<>(l, r.pop(), lHat.pop().pop(), rHat.pop().pop());
		assert l.size() == 1;
		return create();
	}
}
