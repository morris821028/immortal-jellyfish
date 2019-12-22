package persistent.queue;

import org.junit.jupiter.api.Test;

import persistent.PQueueTestUtil;

public class Realtime4QueueTest {
	@Test
	public void testQueue() {
		Realtime4Queue<Integer> que = Realtime4Queue.create();
		System.out.println("Realtime4Queue feat.");
		PQueueTestUtil.testQueue(que);
	}
}
