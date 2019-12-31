package persistent.helper;

import persistent.PStack;
import persistent.util.PCollections;

public class Take<T> extends PStack<T> {
	private final PStack<T> x;
	private final int n;
	private final T top;

	private Take(int n, PStack<T> x) {
		this.n = n;
		this.x = x;
		this.top = x.top();
		assert !(x instanceof Take);
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public int size() {
		return n;
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
		return create(n - 1, x.pop());
	}

	public static <T> PStack<T> create(int n, PStack<T> x) {
		if (n == 0)
			return PCollections.emptyStack();
		if (x instanceof Drop<?>) {
			Drop<T> t = (Drop<T>) x;
			x = t.getReal();
		}
		if (x instanceof Take<?>) {
			Take<T> t = (Take<T>) x;
			if (n == t.n)
				return x;
			assert n < t.n;
			return new Take<>(n, t.x);
		}
		return new Take<>(n, x);
	}
}
