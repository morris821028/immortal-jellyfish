package persistent;

public interface PStack<T> {
	public boolean isEmpty();

	public long size();

	public T top();

	public PStack<T> push(T value);

	public PStack<T> pop();
}