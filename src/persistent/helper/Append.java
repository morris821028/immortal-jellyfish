package persistent.helper;

import persistent.PStack;
import persistent.stack.PersistStack;

public class Append<T> extends PStack<T> {
	private final PStack<T> l;
	private final PStack<T> r;
	private final int size;
	
	private Append(PStack<T> l, PStack<T> r) {
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
	public int size() {
		return size;
	}

	@Override
	public T top() {
		T v = l.top();
		assert v != null : String.format("%d %s %b", l.size(), l.getClass(), isEmpty());
		return v;
	}

	@Override
	public PStack<T> push(T value) {
		return new Append<>(l.push(value), r);
	}

	@Override
	public PStack<T> pop() {
		if (l.size() == 1)
			return r;
		return create(l.pop(), r);
	}

	public static <T> PStack<T> create(PStack<T> l, PStack<T> r) {
		if (l.isEmpty())
			return r;
		if (r.isEmpty())
			return l;
		return new Append<>(l, r);
	}

	public static <T> PStack<T> create(T l, PStack<T> r) {
		if (l == null)
			return r;

		PStack<T> t = PersistStack.create();
		t = t.push(l);
		if (r.isEmpty()) {
			return t;
		}
		return new Append<>(t, r);
	}
}
