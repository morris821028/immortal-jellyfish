package persistent;

public interface PQueue<T> {
	public boolean isEmpty();

	public int size();

	public T front();

	public PQueue<T> push(T value);

	public PQueue<T> pop();
}
