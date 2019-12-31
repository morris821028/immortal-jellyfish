package persistent.helper;

import persistent.PStack;
import persistent.util.PCollections;

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
		y = reverse(x);
		assert y.size() == x.size() : String.format("%d %d %s", y.size(), x.size(), x.getClass());
		return y;
	}

	/**
	 * <p>
	 * Flatten, and then reverse stack.
	 * </p>
	 * <p>
	 * Time complexity O(|x|)
	 * </p>
	 * 
	 * @param <T> the type of elements
	 * @param x   stack
	 * @return A reversed stack
	 * @see #create(PStack)
	 */
	public static <T> PStack<T> reverse(PStack<T> x) {
		PStack<T> y = PCollections.emptyStack();
		while (!x.isEmpty()) {
			y = y.push(x.top());
			x = x.pop();
		}
		return y;
	}

	/**
	 * <p>
	 * Create a lazy evaluation reverse stack
	 * </p>
	 * <p>
	 * Invariant {@literal |x| <= 6} in internal use.
	 * </p>
	 * 
	 * @param <T> the type of elements
	 * @param r   stack
	 * @return A lazy revered stack
	 */
	public static <T> PStack<T> create(PStack<T> r) {
		if (r.isEmpty())
			return PCollections.emptyStack();
		if (r.size() == 1)
			return r;
		if (r instanceof Rev<?>) {
			Rev<T> t = (Rev<T>) r;
			return t.x;
		}
		assert r.size() <= 6 : String.format("r size = %d", r.size());
		return new Rev<>(r);
	}
}
