package persistent.util;

import java.util.Comparator;

import persistent.PDeque;
import persistent.PList;
import persistent.PPriorityQueue;
import persistent.PQueue;
import persistent.PStack;
import persistent.array.TreeList;
import persistent.deque.RealtimeDeque;
import persistent.priority_queue.BraunPriorityQueue;
import persistent.queue.RealtimeQueue;
import persistent.stack.PersistStack;

public class PCollections {
	private PCollections() {
	}

	/**
	 * Default {@link PersistStack}
	 * 
	 * @param <T> The type of element
	 * @return The empty base of stack
	 */
	public static <T> PStack<T> emptyStack() {
		return PersistStack.create();
	}

	/**
	 * Default {@link RealtimeQueue}
	 * 
	 * @param <T> The type of element
	 * @return The empty base of queue
	 */
	public static <T> PQueue<T> emptyQueue() {
		return RealtimeQueue.create();
	}

	/**
	 * Default {@link RealtimeDeque}
	 * 
	 * @param <T> The type of element
	 * @return The empty base of deque
	 */
	public static <T> PDeque<T> emptyDeque() {
		return RealtimeDeque.create();
	}

	/**
	 * Default {@link TreeList}
	 * 
	 * @param <T> The type of element
	 * @return The empty base of list
	 */
	public static <T> PList<T> emptyList() {
		return TreeList.create();
	}

	/**
	 * Default {@link BraunPriorityQueue}
	 * 
	 * @param <T>        The type of element
	 * @param comparator the comparator that will be used to order this priority
	 *                   queue
	 * @return The empty base of priority queue
	 */
	public static <T> PPriorityQueue<T> emptyPriorityQueue(Comparator<T> comparator) {
		return BraunPriorityQueue.create(comparator);
	}
}
