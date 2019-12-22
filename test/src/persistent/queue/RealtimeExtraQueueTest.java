package persistent.queue;

import org.junit.jupiter.api.Test;

import persistent.PQueueTestUtil;

public class RealtimeExtraQueueTest {
	@Test
	public void testQueue() {
		RealtimeExtraQueue<Integer> que = RealtimeExtraQueue.create();
		System.out.println("RealtimeEXtraQueue feat.");
		PQueueTestUtil.testQueue(que);
	}
}
