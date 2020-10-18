package persistent.deque;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import persistent.PDeque;
import persistent.PDequeTestUtil;

public class RealtimeDequeTest {
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

	@Test
	public void testZigZag() {
		PDequeTestUtil.testZigZag(RealtimeDeque.create());
	}

	@Test
	public void testIterator() {
		for (int n = 0; n < 32; n++) {
			PDeque<Integer> a = RealtimeDeque.create();
			for (int i = 0; i < n; i++)
				a = a.pushBack(i);

			int index = 0;
			for (Integer i : a) {
//			System.out.printf("[%d] %d\n", index, i);
				Assertions.assertEquals(i, index);
				index++;
			}
			Assertions.assertEquals(index, n);
		}
	}
}
