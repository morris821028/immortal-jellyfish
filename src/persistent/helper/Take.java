package persistent.helper;

import persistent.PStack;
import persistent.stack.AppendStack;
import persistent.stack.PersistStack;

public class Take<T> implements PStack<T> {
	private final PStack<T> x;
	private final int n;

	private PStack<T> pop;

	private Take(int n, PStack<T> x) {
		this.n = n;
		this.x = x;
//		assert n < 1024;
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
		return AppendStack.create(value, this);
	}

	@Override
	public PStack<T> pop() {
		if (n == 1)
			return PersistStack.create();
		pop = create(n - 1, x.pop());
		return pop;
	}

	public static <T> PStack<T> create(int n, PStack<T> x) {
		if (n == 0)
			return PersistStack.create();
		return new Take<>(n, x);
	}
}
