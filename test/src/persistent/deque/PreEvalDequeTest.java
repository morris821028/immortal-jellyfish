package persistent.deque;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import persistent.PDequeTestUtil;
import persistent.deque.PreEvalDeque;

public class PreEvalDequeTest {
	@Test
	public void testDequeAsStackBack() {
		PDequeTestUtil.testDequeAsStackBack(PreEvalDeque.create());
	}

	@Test
	public void testDequeAsStackFront() {
		PDequeTestUtil.testDequeAsStackFront(PreEvalDeque.create());
	}

	@Test
	public void testDequeAsQueue() {
		PDequeTestUtil.testDequeAsQueue(PreEvalDeque.create());
	} 

	@Test
	public void testDequeAsRevQueue() {
		PDequeTestUtil.testDequeAsRevQueue(PreEvalDeque.create());
	}

	@Test
	public void testDequeAsSliding() {
		PDequeTestUtil.testDequeAsSliding(PreEvalDeque.create());
	}
}
