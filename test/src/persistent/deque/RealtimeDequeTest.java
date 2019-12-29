package persistent.deque;

import org.junit.jupiter.api.Test;

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
}
