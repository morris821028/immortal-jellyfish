package persistent.helper;

import persistent.PStack;
import persistent.stack.PersistStack;

public class Rev<T> extends PStack<T> {
	private final PStack<T> x;
	private PStack<T> y;

	private PStack<T> pop;

	private Rev(PStack<T> x) {
		this.x = x;
	}

	@Override
	public boolean isEmpty() {
		return x.isEmpty();
	}

	@Override
	public int size() {
		return x.size();
	}

	@Override
	public T top() {
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
		pop = getReal().pop();
		return pop;
	}
	
	private PStack<T> getReal() {
		if (y != null)
			return y;
		y = PersistStack.create();
		PStack<T> tmp = x;
		while (!tmp.isEmpty()) {
			y = y.push(tmp.top());
			tmp = tmp.pop();
		}
		assert y.size() == x.size() : String.format("%d %d %s", y.size(), x.size(), x.getClass());
		return y;
	}

	public static <T> PStack<T> create(PStack<T> r) {
		if (r.isEmpty())
			return PersistStack.create();
		if (r.size() == 1)
			return r;
		if (r instanceof Rev<?>) {
			Rev<T> t = (Rev<T>) r;
			return t.x;
		}
		return new Rev<>(r);
	}
}
