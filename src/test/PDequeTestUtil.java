package test;

import org.junit.jupiter.api.Assertions;

import persistent.PDeque;

public class PDequeTestUtil {
	private PDequeTestUtil() {
	}

	public static void testDequeAsStackBack(final PDeque<Integer> empty) {
		PDeque<Integer> stk = empty;
		PDeque<Integer> stk1 = stk.pushBack(1);
		PDeque<Integer> stk2 = stk1.pushBack(2);
		PDeque<Integer> stk3 = stk2.popBack();
		PDeque<Integer> stk4 = stk3.pushBack(3);

		assert stk1.back() == 1;
		assert stk2.back() == 2;
		assert stk3.back() == 1;
		assert stk4.back() == 3;

		final int n = 1000000;
		for (int i = 0; i < n; i++) {
			stk = stk.pushBack(i);
		}
		for (int i = 0; i < n; i++) {
			PDeque<Integer> t = stk.pushBack(1);
			Integer v = t.back();
			Assertions.assertEquals(v, 1);
		}
		for (int i = n - 1; i >= 0; i--) {
			Integer v = stk.back();
			Assertions.assertEquals(v, i);
			stk = stk.popBack();
		}
		Assertions.assertEquals(stk.isEmpty(), true);
		System.out.println("testDequeAsStackBack() pass");
	}
	
	public static void testDequeAsStackFront(final PDeque<Integer> empty) {
		PDeque<Integer> stk = empty;
		PDeque<Integer> stk1 = stk.pushFront(1);
		PDeque<Integer> stk2 = stk1.pushFront(2);
		PDeque<Integer> stk3 = stk2.popFront();
		PDeque<Integer> stk4 = stk3.pushFront(3);

		assert stk1.front() == 1;
		assert stk2.front() == 2;
		assert stk3.front() == 1;
		assert stk4.front() == 3;

		final int n = 1000000;
		for (int i = 0; i < n; i++) {
			stk = stk.pushFront(i);
		}
		for (int i = 0; i < n; i++) {
			PDeque<Integer> t = stk.pushFront(1);
			Integer v = t.front();
			Assertions.assertEquals(1, v);
		}
		Assertions.assertEquals(stk.size(), n);
		for (int i = n - 1; i >= 0; i--) {
			Integer v = stk.front();
			Assertions.assertEquals(i, v);
			stk = stk.popFront();
		}
		Assertions.assertEquals(stk.isEmpty(), true);
		System.out.println("testDequeAsStackFront() pass");
	}

	public static void testDequeAsQueue(final PDeque<Integer> empty) {
		PDeque<Integer> que = empty;
		PDeque<Integer> que1 = que.pushBack(1);
		PDeque<Integer> que2 = que1.pushBack(2);
		PDeque<Integer> que3 = que2.popFront();
		PDeque<Integer> que4 = que3.pushBack(3);

		assert que1.front() == 1;
		assert que2.front() == 1;
		assert que3.front() == 2;
		assert que4.front() == 2;

		for (int i = 0; i < 1000000; i++) {
			que = que.pushBack(i);
			Assertions.assertEquals(que.size(), i+1L);
		}
		for (int i = 0; i < 1000000; i++) {
			PDeque<Integer> t = que.pushBack(1);
			Integer v = t.front();
			Assertions.assertEquals(v, 0);
		}
		for (int i = 0; i < 1000000; i++) {
			Integer v = que.front();
			Assertions.assertEquals(v, i);
			assert v == i;
			que = que.popFront();
			Assertions.assertEquals(que.size(), 1000000 - i - 1);
		}
		System.out.println("testDequeAsQueue() pass");
	}

	public static void testDequeAsRevQueue(final PDeque<Integer> empty) {
		PDeque<Integer> que = empty;
		PDeque<Integer> que1 = que.pushFront(1);
		PDeque<Integer> que2 = que1.pushFront(2);
		PDeque<Integer> que3 = que2.popBack();
		PDeque<Integer> que4 = que3.pushFront(3);

		assert que1.back() == 1;
		assert que2.back() == 1;
		assert que3.back() == 2;
		assert que4.back() == 2;

		for (int i = 0; i < 1000000; i++) {
			que = que.pushFront(i);
			Assertions.assertEquals(que.size(), i+1L);
		}
		for (int i = 0; i < 1000000; i++) {
			PDeque<Integer> t = que.pushFront(1);
			Integer v = t.back();
			Assertions.assertEquals(v, 0);
		}
		for (int i = 0; i < 1000000; i++) {
			Integer v = que.back();
			Assertions.assertEquals(v, i);
			assert v == i;
			que = que.popBack();
			Assertions.assertEquals(que.size(), 1000000 - i - 1);
		}
		System.out.println("testDequeAsRevQueue() pass");
	}
}
