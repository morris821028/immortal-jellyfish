package persistent.queue;

import org.junit.jupiter.api.Test;

import persistent.PQueueTestUtil;

public class RealtimeQueueTest {
	@Test
	public void testQueueAsSliding() {
		RealtimeQueue<Integer> que = RealtimeQueue.create();
		System.out.println("RealtimeQueue feat.");
		PQueueTestUtil.testQueueAsSliding(que);
	}

	@Test
	public void testQueue() {
		RealtimeQueue<Integer> que = RealtimeQueue.create();
		System.out.println("RealtimeQueue feat.");
		PQueueTestUtil.testQueue(que);
	}
}
