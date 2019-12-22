package persistent.deque;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import persistent.PDeque;
import persistent.deque.RealtimeDeque;

public class RealtimeDequeTest {
	private RealtimeDequeTest() {}

	@Disabled("temp")
	@Test
	public void testPushFront() {
		PDeque<Integer> q = RealtimeDeque.create();
		for (int i = 1; i <= 1000000; i++) {
			q = q.pushFront(i);
			Assertions.assertEquals(i, q.front());
		}
	}
	
	@Test
	public void testPopFront() {
		RealtimeDeque<Integer> q = RealtimeDeque.create();
		int n = 1000000;
		for (int i = 1; i <= n; i++) {
			q = q.pushFront(i);
			Assertions.assertEquals(i, q.front());
			Assertions.assertEquals(i, q.size());
		}

		System.out.println("start");

		for (int i = n; i >= 1; i--) {
			Assertions.assertEquals(i, q.front());
			q = q.popFront();
			Assertions.assertEquals(i-1, q.size());
		}
	}
}
