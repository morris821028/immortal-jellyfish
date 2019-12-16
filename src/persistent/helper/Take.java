package persistent.helper;

import persistent.PStack;
import persistent.stack.AppendStack;
import persistent.stack.PersistStack;

public class Take<T> implements PStack<T> {
	private final PStack<T> x;
	private final int n;

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
		return AppendStack.append(value, this);
	}

	@Override
	public PStack<T> pop() {
		return create(n - 1, x.pop());
	}

	public static <T> PStack<T> create(int n, PStack<T> x) {
		if (n == 0)
			return PersistStack.create();
		return new Take<>(n, x);
	}
}
