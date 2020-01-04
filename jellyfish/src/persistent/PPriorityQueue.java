package persistent;

public abstract class PPriorityQueue<T> {
	public abstract boolean isEmpty();

	public abstract int size();

	public abstract T top();

	public abstract PPriorityQueue<T> push(T value);

	public abstract PPriorityQueue<T> pop();
}
