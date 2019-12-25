package persistent;

import org.junit.jupiter.api.Assertions;

import persistent.PQueue;

public class PQueueTestUtil {
	private PQueueTestUtil() {}

	public static void testQueueAsSliding(final PQueue<Integer> empty) {
		PQueue<Integer> que = empty;

		int m = 100000;
		int n = 1000000;
		for (int i = 0; i < m; i++) {
			que = que.push(i);
			Assertions.assertEquals(i + 1L, que.size());
		}

		for (int i = m; i < n; i++) {
			Integer v = que.front();
			Assertions.assertEquals(i - m, v);
			que = que.pop();
			que = que.push(i);
			Assertions.assertEquals(m, que.size());
		}
		System.out.println("testQueueAsSliding() pass");
	}

	public static void testQueue(PQueue<Integer> empty) {
		PQueue<Integer> que = empty;
		PQueue<Integer> que1 = que.push(1);
		PQueue<Integer> que2 = que1.push(2);
		PQueue<Integer> que3 = que2.pop();
		PQueue<Integer> que4 = que3.push(3);

		Assertions.assertNotNull(que1);
		Assertions.assertEquals(1, que1.front());
		Assertions.assertEquals(1, que2.front());
		Assertions.assertEquals(2, que3.front());
		Assertions.assertEquals(2, que4.front());

		final int n = 1000000;
		for (int i = 0; i < n; i++) {
			que = que.push(i);
			Assertions.assertEquals(i + 1L, que.size());
		}
		for (int i = 0; i < n; i++) {
			PQueue<Integer> t = que.push(1);
			Integer v = t.front();
			Assertions.assertEquals(0, v);
		}
		for (int i = 0; i < n; i++) {
			PQueue<Integer> t = que.pop();
			Integer v = t.front();
			Assertions.assertEquals(1, v);
		}
		for (int i = 0; i < n; i++) {
			Integer v = que.front();
			Assertions.assertEquals(i, v);
			que = que.pop();
			Assertions.assertEquals(n - i - 1, que.size());
		}
		System.out.println("testQueue() pass");
		testQueueAsSliding(empty);
	}
}
