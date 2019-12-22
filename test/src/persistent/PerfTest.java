package persistent;

import java.util.ArrayDeque;
import java.util.Deque;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import persistent.PDeque;
import persistent.deque.PreEvalDeque;
import persistent.queue.PreEvalQueue;
import persistent.queue.RealtimeQueue;
import persistent.stack.PersistStack;

class PerfTest {
	private static void testGoldenStack() {
		Deque<Integer> stk = new ArrayDeque<>();
		for (int i = 0; i < 1000000; i++) {
			stk.push(i);
		}

		for (int i = 1000000 - 1; i >= 0; i--) {
			Integer v = stk.peekFirst();
			Assertions.assertEquals(v, i);
			stk.pop();
		}
		System.out.println("testGoldenStack() pass");
	}

	private static void testStack() {
		PersistStack<Integer> stk = PersistStack.create();
		PStackTestUtil.testStack(stk);
	}

	private static void testQueue() {
		RealtimeQueue<Integer> que = RealtimeQueue.create();
		System.out.println("RealtimeQueue feat.");
		PQueueTestUtil.testQueue(que);
	}

	private static void testQueue2() {
		PreEvalQueue<Integer> que = PreEvalQueue.create();
		System.out.println("PreEvalQueue feat.");
		PQueueTestUtil.testQueue(que);
	}

	private static void testDequeAsStackBack() {
		PDeque<Integer> stk = PreEvalDeque.create();
		PDequeTestUtil.testDequeAsStackBack(stk);
	}

	private static void testDequeAsStackFront() {
		PDeque<Integer> stk = PreEvalDeque.create();
		PDequeTestUtil.testDequeAsStackFront(stk);
	}

	private static void test(Runnable r) {
		System.gc();
		long start = System.nanoTime();
		r.run();
		long time = System.nanoTime() - start;
		System.out.printf("Time: %f\n\n", time / 10e+9);
	}

	@Disabled
	@Test
	void test() {
		test(() -> testGoldenStack());
		test(() -> testStack());
		test(() -> testQueue());
		test(() -> testQueue2());
		test(() -> testDequeAsStackBack());
		test(() -> testDequeAsStackFront());
	}
}
