package test;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayDeque;
import java.util.Deque;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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
		System.out.println("testStack() pass");
	}
	
	private static void testStack() {
		PersistStack<Integer> stk = PersistStack.create();
		PersistStack<Integer> stk1 = stk.push(1);
		PersistStack<Integer> stk2 = stk1.push(2);
		PersistStack<Integer> stk3 = stk2.pop();
		PersistStack<Integer> stk4 = stk3.push(3);

		assert stk1.top() == 1;
		assert stk2.top() == 2;
		assert stk3.top() == 1;
		assert stk4.top() == 3;

		stk = PersistStack.create();
		for (int i = 0; i < 1000000; i++) {
			stk = stk.push(i);
		}
		for (int i = 0; i < 1000000; i++) {
			PersistStack<Integer> t = stk.push(1);
			Integer v = t.top();
			Assertions.assertEquals(v, 1);
		}
		for (int i = 1000000 - 1; i >= 0; i--) {
			Integer v = stk.top();
			Assertions.assertEquals(v, i);
			stk = stk.pop();
		}
		System.out.println("testStack() pass");
	}

	private static void testQueue() {
		RealtimeQueue<Integer> que = RealtimeQueue.create();
		RealtimeQueue<Integer> que1 = que.push(1);
		RealtimeQueue<Integer> que2 = que1.push(2);
		RealtimeQueue<Integer> que3 = que2.pop();
		RealtimeQueue<Integer> que4 = que3.push(3);

		assert que1.front() == 1;
		assert que2.front() == 1;
		assert que3.front() == 2;
		assert que4.front() == 2;

		que = RealtimeQueue.create();
		for (int i = 0; i < 1000000; i++) {
			que = que.push(i);
			Assertions.assertEquals(que.size(), i+1L);
		}
		for (int i = 0; i < 1000000; i++) {
			RealtimeQueue<Integer> t = que.push(1);
			Integer v = t.front();
			Assertions.assertEquals(v, 0);
		}
		for (int i = 0; i < 1000000; i++) {
			Integer v = que.front();
			Assertions.assertEquals(v, i);
			assert v == i;
			que = que.pop();
			Assertions.assertEquals(que.size(), 1000000 - i - 1);
		}
		System.out.println("testQueue() pass");
	}

	private static void testQueue2() {
		PreEvalQueue<Integer> que = PreEvalQueue.create();
		PreEvalQueue<Integer> que1 = que.push(1);
		PreEvalQueue<Integer> que2 = que1.push(2);
		PreEvalQueue<Integer> que3 = que2.pop();
		PreEvalQueue<Integer> que4 = que3.push(3);

		assert que1 != null;
		assert que1.front() == 1;
		assert que2.front() == 1;
		assert que3.front() == 2;
		assert que4.front() == 2;

		for (int i = 0; i < 1000000; i++) {
			que = que.push(i);
			Assertions.assertEquals(que.size(), i+1L);
		}
		for (int i = 0; i < 1000000; i++) {
			PreEvalQueue<Integer> t = que.push(1);
			Integer v = t.front();
			Assertions.assertEquals(v, 0);
		}
		for (int i = 0; i < 1000000; i++) {
			Integer v = que.front();
			Assertions.assertEquals(v, i);
			assert v == i;
			que = que.pop();
			Assertions.assertEquals(que.size(), 1000000 - i - 1);
		}
		System.out.println("testQueue2() pass");
	}

	private static void test(Runnable r) {
		System.gc();
		long start = System.nanoTime();
		r.run();
		long time = System.nanoTime() - start;
		System.out.printf("Time: %f\n\n", time / 10e+9);
	}

	@Test
	void test() {
		// Scanner cin = new Scanner(System.in);
		// cin.next();
		test(() -> testGoldenStack());
		test(() -> testStack());
		test(() -> testQueue());
		test(() -> testQueue2());
		// test(() -> testDqueue());
		// try {
		// assert false;
		// System.out.println("test fail, please set up -ea");
//		} catch (Exception e) {
//			System.out.println("test successful");
//		}
	}
}
