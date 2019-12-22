package persistent;

import persistent.stack.PersistStack;

public interface PStack<T> {
	public boolean isEmpty();

	public int size();

	public T top();

	public PStack<T> push(T value);

	public PStack<T> pop();

	public static <T> PStack<T> of(T value) {
		PStack<T> stk = PersistStack.create();
		return stk.push(value);
	}
}