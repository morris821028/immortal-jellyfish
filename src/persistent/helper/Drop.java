package persistent.helper;

import persistent.PStack;
import persistent.stack.AppendStack;
import persistent.stack.PersistStack;

public class Drop<T> implements PStack<T> {
	private final PStack<T> x;
	private final int n;
	private PStack<T> rx;
	private PStack<T> pop;

	private Drop(int n, PStack<T> x) {
		this.n = n;
		this.x = x;
		assert n != 0;
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
		return getReal().top();
	}

	@Override
	public PStack<T> push(T value) {
		return AppendStack.append(value, this);
	}

	@Override
	public PStack<T> pop() {
		if (pop == null)
			pop = getReal().pop();
		return pop;
	}

	private PStack<T> getReal() {
		if (rx != null)
			return rx;
		rx = x;
		for (int i = n; i > 0; i--)
			rx = rx.pop();
		return rx;
	}

	public static <T> PStack<T> create(int n, PStack<T> x) {
		if (n == x.size())
			return PersistStack.create();
		return new Drop<>(n, x);
	}
}
