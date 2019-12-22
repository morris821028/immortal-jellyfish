package persistent.util;

import persistent.PDeque;
import persistent.PQueue;
import persistent.PStack;
import persistent.deque.RealtimeDeque;
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
}
