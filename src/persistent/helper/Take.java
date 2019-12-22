package persistent.helper;

import persistent.PStack;
import persistent.stack.PersistStack;

public class Take<T> extends PStack<T> {
	private final PStack<T> x;
	private final int n;

	private PStack<T> pop;

	private Take(int n, PStack<T> x) {
		this.n = n;
		this.x = x;
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
		return x.top();
	}

	@Override
	public PStack<T> push(T value) {
		return Append.create(value, this);
	}

	@Override
	public PStack<T> pop() {
		if (pop != null)
			return pop;
		if (n == 1)
			return pop = PersistStack.create();
		pop = create(n - 1, x.pop());
		return pop;
	}

	public static <T> PStack<T> create(int n, PStack<T> x) {
		if (n == 0)
			return PersistStack.create();
		if (x instanceof Drop<?>) {
			Drop<T> t = (Drop<T>) x;
			x = t.getReal();
		}
		return new Take<>(n, x);
	}
}
