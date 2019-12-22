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

	public static <T> PStack<T> emptyStack() {
		return PersistStack.create();
	}

	public static <T> PQueue<T> emptyQueue() {
		return RealtimeQueue.create();
	}

	public static <T> PDeque<T> emptyDeque() {
		return RealtimeDeque.create();
	}
}
