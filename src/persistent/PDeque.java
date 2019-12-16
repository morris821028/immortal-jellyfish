package persistent;

public interface PDeque<T> {
	public boolean isEmpty();

	public int size();

	public T front();

	public T back();

	public PDeque<T> pushFront(T value);

	public PDeque<T> pushBack(T value);

	public PDeque<T> popFront();

	public PDeque<T> popBack();
}
