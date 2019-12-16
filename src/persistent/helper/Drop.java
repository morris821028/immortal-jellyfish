package persistent.helper;

import persistent.PStack;
import persistent.stack.AppendStack;
import persistent.stack.PersistStack;

public class Drop<T> implements PStack<T> {
	private final PStack<T> x;
	private final int n;
	
	private PStack<T> rx;
	private PStack<T> pop;
	private T top;
	private final int size;

	private Drop(int n, PStack<T> x) {
		this.n = n;
		this.x = x;
		this.size = x.size() - n;
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
		if (top != null)
			return top;
		T v = getReal().top();
		top = v;
		assert v != null : String.format("%d %s %b", getReal().size(), getReal().getClass(), isEmpty());
		return v;
	}

	@Override
	public PStack<T> push(T value) {
		return AppendStack.create(value, this);
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
		if (x instanceof Drop) {
			Drop<T> t = ((Drop<T>) x);
			return new Drop<>(n + t.n, t.x);
		}
		rx = x;
		for (int i = 1; i <= n; i++) {
			PStack<T> res = rx.pop();
			assert res.size() == x.size() - i : String.format("%s %s", rx.getClass(), res.getClass());
			rx = res;
		}
		return rx;
	}

	public static <T> PStack<T> create(int n, PStack<T> x) {
		if (n == 0)
			return x;
		if (n >= x.size())
			return PersistStack.create();
		if (x instanceof Drop<?>) {
			Drop<T> t = ((Drop<T>) x);
			return new Drop<>(n + t.n, t.x);
		}
		return new Drop<>(n, x);
	}
}
