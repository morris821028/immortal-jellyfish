package test.queue;

import org.junit.jupiter.api.Test;

import persistent.queue.PreEvalQueue;
import test.PQueueTestUtil;

public class PreEvalQueueTest {
	@Test
	public void testQueue() {
		PreEvalQueue<Integer> que = PreEvalQueue.create();
		System.out.println("PreEvalQueue feat.");
		PQueueTestUtil.testQueue(que);
	}
}
