package persistent.queue;

import org.junit.jupiter.api.Test;

import persistent.PQueueTestUtil;
import persistent.queue.RealtimeQueue;

public class RealtimeQueueTest {
	@Test
	public void testQueue() {
		RealtimeQueue<Integer> que = RealtimeQueue.create();
		System.out.println("RealtimeQueue feat.");
		PQueueTestUtil.testQueue(que);
	}
}
