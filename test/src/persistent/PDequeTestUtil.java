package persistent;

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

		Assertions.assertEquals(1, stk1.back());
		Assertions.assertEquals(2, stk2.back());
		Assertions.assertEquals(1, stk3.back());
		Assertions.assertEquals(3, stk4.back());

		final int n = 1000000;
		Assertions.assertEquals(stk.isEmpty(), true);
		for (int i = 0; i < n; i++) {
			stk = stk.pushBack(i);
		}
		for (int i = 0; i < n; i++) {
			PDeque<Integer> t = stk.pushBack(1);
			Integer v = t.back();
			Assertions.assertEquals(v, 1);
			Assertions.assertEquals(stk.size(), n);
		}
		for (int i = n - 1; i >= 0; i--) {
			Integer v = stk.back();
			Assertions.assertEquals(v, i);
			Assertions.assertEquals(stk.size(), i + 1);
			Assertions.assertEquals(stk.isEmpty(), false);
			stk = stk.popBack();
			Assertions.assertEquals(stk.size(), i);
		}
		Assertions.assertEquals(stk.isEmpty(), true);
		Assertions.assertEquals(stk.size(), 0);
		System.out.println("testDequeAsStackBack() pass");
	}

	public static void testDequeAsStackFront(final PDeque<Integer> empty) {
		PDeque<Integer> stk = empty;
		PDeque<Integer> stk1 = stk.pushFront(1);
		PDeque<Integer> stk2 = stk1.pushFront(2);
		PDeque<Integer> stk3 = stk2.popFront();
		PDeque<Integer> stk4 = stk3.pushFront(3);

		Assertions.assertEquals(1, stk1.front());
		Assertions.assertEquals(2, stk2.front());
		Assertions.assertEquals(1, stk3.front());
		Assertions.assertEquals(3, stk4.front());

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

		Assertions.assertEquals(1, que1.front());
		Assertions.assertEquals(1, que2.front());
		Assertions.assertEquals(2, que3.front());
		Assertions.assertEquals(2, que4.front());

		int n = 1000000;
		for (int i = 0; i < n; i++) {
			que = que.pushBack(i);
			Assertions.assertEquals(que.size(), i + 1L);
			Assertions.assertEquals(que.isEmpty(), false);
		}
		for (int i = 0; i < n; i++) {
			PDeque<Integer> t = que.pushBack(1);
			Integer v = t.front();
			Assertions.assertEquals(v, 0);
			Assertions.assertEquals(t.size(), n + 1);
			Assertions.assertEquals(t.isEmpty(), false);
		}
		for (int i = 0; i < n; i++) {
			Integer v = que.front();
			Assertions.assertEquals(v, i);
			Assertions.assertEquals(que.size(), n - i);
			Assertions.assertEquals(que.isEmpty(), false);
			que = que.popFront();
			Assertions.assertEquals(que.size(), n - i - 1);
		}
		System.out.println("testDequeAsQueue() pass");
	}

	public static void testDequeAsRevQueue(final PDeque<Integer> empty) {
		PDeque<Integer> que = empty;
		PDeque<Integer> que1 = que.pushFront(1);
		PDeque<Integer> que2 = que1.pushFront(2);
		PDeque<Integer> que3 = que2.popBack();
		PDeque<Integer> que4 = que3.pushFront(3);

		Assertions.assertEquals(1, que1.back());
		Assertions.assertEquals(1, que2.back());
		Assertions.assertEquals(2, que3.back());
		Assertions.assertEquals(2, que4.back());

		int n = 1000000;
		for (int i = 0; i < n; i++) {
			que = que.pushFront(i);
			Assertions.assertEquals(i + 1L, que.size());
		}
		for (int i = 0; i < n; i++) {
			PDeque<Integer> t = que.pushFront(1);
			Integer v = t.back();
			Assertions.assertEquals(0, v);
			Assertions.assertEquals(t.size(), n + 1);
			Assertions.assertEquals(t.isEmpty(), false);
		}
		for (int i = 0; i < n; i++) {
			Integer v = que.back();
			Assertions.assertEquals(i, v);
			Assertions.assertEquals(que.size(), n - i);
			Assertions.assertEquals(que.isEmpty(), false);
			que = que.popBack();
			Assertions.assertEquals(n - i - 1, que.size());
		}
		System.out.println("testDequeAsRevQueue() pass");
	}

	public static void testDequeAsSliding(final PDeque<Integer> empty) {
		PDeque<Integer> que = empty;
		PDeque<Integer> que1 = que.pushFront(1);
		PDeque<Integer> que2 = que1.pushBack(2);
		PDeque<Integer> que3 = que2.popFront();
		PDeque<Integer> que4 = que3.pushFront(3);

		Assertions.assertEquals(1, que1.back());
		Assertions.assertEquals(2, que2.back());
		Assertions.assertEquals(2, que3.front());
		Assertions.assertEquals(2, que4.back());

		int m = 100000;
		int n = 1000000;
		for (int i = 0; i < m; i++) {
			que = que.pushBack(i);
			Assertions.assertEquals(i + 1L, que.size());
			Assertions.assertEquals(que.size(), i + 1);
			Assertions.assertEquals(que.isEmpty(), false);
		}

		for (int i = m; i < n; i++) {
			Integer v = que.back();
			Assertions.assertEquals(i - 1, v);
			v = que.front();
			Assertions.assertEquals(i - m, v);
			que = que.popFront();
			que = que.pushBack(i);
			Assertions.assertEquals(m, que.size());
		}

		que1 = que;
		for (int i = 0; i < m; i++) {
			Integer v = que1.back();
			Assertions.assertEquals(n - 1, v);
			v = que1.front();
			Assertions.assertEquals(n - m + i, v);
			que1 = que1.popFront();
			Assertions.assertEquals(m - i - 1, que1.size());
		}

		que2 = que;
		for (int i = 0; i < m; i++) {
			Integer v = que2.back();
			Assertions.assertEquals(n - i - 1, v);
			v = que2.front();
			Assertions.assertEquals(n - m, v);
			que2 = que2.popBack();
			Assertions.assertEquals(m - i - 1, que2.size());
		}

		System.out.println("testDequeAsSliding() pass");
	}

	public static void testZigZag(final PDeque<Integer> empty) {
		PDeque<Integer> que = empty;

		int n = 1000000;
		for (int i = 1; i <= n; i++) {
			que = que.pushBack(i);
			que = que.pushFront(-i);
			Assertions.assertEquals(2 * i, que.size());
			Assertions.assertEquals(i, que.back());
			Assertions.assertEquals(-i, que.front());
		}

		Assertions.assertEquals(2 * n, que.size());

		PDeque<Integer> dcom = que;

		for (int i = n; i >= 1; i--) {
			Assertions.assertEquals(-i, dcom.front());
			dcom = dcom.popFront();
			Assertions.assertEquals(2 * i - 1, dcom.size());
			Assertions.assertEquals(i, dcom.back());
			dcom = dcom.popBack();
			Assertions.assertEquals(2 * i - 2, dcom.size());
		}

		dcom = que;

		for (int i = n; i >= 1; i--) {
			Assertions.assertEquals(i + n, dcom.size());
			Assertions.assertEquals(i, dcom.back());
			dcom = dcom.popBack();
			Assertions.assertEquals(i + n - 1, dcom.size());
		}
		for (int i = -1; i >= -n; i--) {
			Assertions.assertEquals(n + i + 1, dcom.size());
			Assertions.assertEquals(i, dcom.back());
			dcom = dcom.popBack();
			Assertions.assertEquals(n + i, dcom.size());
		}

		dcom = que;
		Assertions.assertEquals(2 * n, que.size());

		for (int i = -n; i <= -1; i++) {
			Assertions.assertEquals(n - i, dcom.size());
			Assertions.assertEquals(i, dcom.front());
			dcom = dcom.popFront();
			Assertions.assertEquals(n - i - 1, dcom.size());
		}
		for (int i = 1; i <= n; i++) {
			Assertions.assertEquals(n - i + 1, dcom.size());
			Assertions.assertEquals(i, dcom.front());
			dcom = dcom.popFront();
			Assertions.assertEquals(n - i, dcom.size());
		}

		System.out.println("testZigZag() pass");
	}
}
