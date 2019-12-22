package persistent.deque;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import junit.framework.Assert;
import persistent.PDeque;
import persistent.PDequeTestUtil;
import persistent.deque.RealtimeDeque;

public class RealtimeDequeTest {
	private RealtimeDequeTest() {
	}

//	@Disabled("temp")
	@Test
	public void testPushFront() {
		PDeque<Integer> q = RealtimeDeque.create();
		for (int i = 1; i <= 1000000; i++) {
			q = q.pushFront(i);
			Assertions.assertEquals(i, q.front());
		}
	}

//	@Disabled("temp")
	@Test
	public void testPopFront() {
		RealtimeDeque<Integer> q = RealtimeDeque.create();
		int n = 50;
		for (int i = 1; i <= n; i++) {
			q = q.pushFront(i);
			Assertions.assertEquals(i, q.front());
			Assertions.assertEquals(i, q.size());
		}

//		System.out.println("start");

		for (int i = n; i >= 1; i--) {
//			System.out.printf("pop %d\n", i);
			Assertions.assertEquals(i, q.front());
			q = q.popFront();
//			q.dump();
			Assertions.assertEquals(i - 1, q.size());
		}
	}

//	@Disabled("temp")
	@Test
	public void testPushBack() {
		RealtimeDeque<Integer> q = RealtimeDeque.create();
		for (int i = 1; i <= 1000000; i++) {
			q = q.pushBack(i);
			Assertions.assertEquals(i, q.back());
		}
	}

//	@Disabled("temp")
	@Test
	public void testPopBack() {
		RealtimeDeque<Integer> q = RealtimeDeque.create();
		int n = 20;
		for (int i = 1; i <= n; i++) {
//			System.out.printf("\npush %d\n", i);
			q = q.pushBack(i);
//			q.dump();
			Assertions.assertEquals(i, q.back());
			Assertions.assertEquals(i, q.size());
		}

//		System.out.println("start");

		for (int i = n; i >= 1; i--) {
//			System.out.printf("\npop %d\n", i);
			Assertions.assertEquals(i, q.back());
			Assertions.assertEquals(1, q.front());
			q = q.popBack();
//			q.dump();
			Assertions.assertEquals(i - 1, q.size());
		}
		Assertions.assertTrue(q.isEmpty());
	}
	
	@Test
	public void testDequeAsStackBack() {
		PDequeTestUtil.testDequeAsStackBack(RealtimeDeque.create());
	}

	@Test
	public void testDequeAsStackFront() {
		PDequeTestUtil.testDequeAsStackFront(RealtimeDeque.create());
	}

	@Test
	public void testDequeAsQueue() {
		PDequeTestUtil.testDequeAsQueue(RealtimeDeque.create());
	} 

	@Test
	public void testDequeAsRevQueue() {
		PDequeTestUtil.testDequeAsRevQueue(RealtimeDeque.create());
	}

	@Test
	public void testDequeAsSliding() {
		PDequeTestUtil.testDequeAsSliding(RealtimeDeque.create());
	}
}
