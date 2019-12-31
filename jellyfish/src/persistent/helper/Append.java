package persistent.helper;

import persistent.PStack;
import persistent.stack.PersistStack;

public class Append<T> extends PStack<T> {
	private final PStack<T> l;
	private final PStack<T> r;
	private final T top;
	private final int size;
	
	private Append(PStack<T> l, PStack<T> r) {
		assert !l.isEmpty();
		this.l = l;
		this.r = r;
		this.size = l.size() + r.size();
		this.top = l.top();
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
		if (l instanceof Append) {
			Append<T> a = (Append<T>) l;
			return new Append<>(a.l, new Append<>(a.r, r));
		}
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
