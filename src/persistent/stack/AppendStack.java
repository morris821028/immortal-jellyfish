package persistent.stack;

import persistent.PStack;

public class AppendStack<T> implements PStack<T> {
	private final PStack<T> l;
	private final PStack<T> r;
	private final int size;
	
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
	public int size() {
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

	public static <T> PStack<T> append(T l, PStack<T> r) {
		if (l == null)
			return r;

		PStack<T> t = PersistStack.create();
		t.push(l);
		if (r.isEmpty()) {

			return t;
		}
		return new AppendStack<>(t, r);
	}
}