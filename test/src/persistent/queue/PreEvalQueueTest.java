package persistent.queue;

import org.junit.jupiter.api.Test;

import persistent.PQueueTestUtil;
import persistent.queue.PreEvalQueue;

public class PreEvalQueueTest {
	@Test
	public void testQueue() {
		PreEvalQueue<Integer> que = PreEvalQueue.create();
		System.out.println("PreEvalQueue feat.");
		PQueueTestUtil.testQueue(que);
	}
}
