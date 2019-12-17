package test.deque;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import persistent.deque.PreEvalDeque;
import test.PDequeTestUtil;

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
}
