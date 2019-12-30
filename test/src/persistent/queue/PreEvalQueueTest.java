package persistent.queue;

import org.junit.jupiter.api.Test;

import persistent.PQueueTestUtil;

public class PreEvalQueueTest {
	@Test
	public void testQueueAsSliding() {
		PreEvalQueue<Integer> que = PreEvalQueue.create();
		System.out.println("PreEvalQueue feat.");
		PQueueTestUtil.testQueueAsSliding(que);
	}

	@Test
	public void testQueue() {
		PreEvalQueue<Integer> que = PreEvalQueue.create();
		System.out.println("PreEvalQueue feat.");
		PQueueTestUtil.testQueue(que);
	}
}
