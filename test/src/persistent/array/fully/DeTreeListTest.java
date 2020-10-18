package persistent.array.fully;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import persistent.PFullyList;
import persistent.PListTestUtil;

public class DeTreeListTest {
	@Test
	public void testPushBackGet() {
		PListTestUtil.testPushBackGet(DeTreeList.create());
	}

	@Test
	public void testPopBackGet() {
		PListTestUtil.testPopBackGet(DeTreeList.create());
	}

	@Test
	public void testPushBackSet() {
		PListTestUtil.testPushBackSet(DeTreeList.create());
	}

	@Test
	public void testToString() {
		PListTestUtil.testToString(DeTreeList.create());
	}

	@Test
	public void testDemo() {
		PListTestUtil.testDemo(DeTreeList.create());
	}

	@Test
	public void testZigZag() {
		PListTestUtil.testZigZag(DeTreeList.create());
	}

	@Test
	public void testZigZagAsDeque() {
		DeTreeList<Integer> que = DeTreeList.create();

		int n = 1000000;
		for (int i = 1; i <= n; i++) {
			que = que.pushBack(i);
			Assertions.assertEquals(i, que.back(), "" + i);
			que = que.pushFront(-i);
			Assertions.assertEquals(2 * i, que.size());
			Assertions.assertEquals(i, que.back(), "" + i);
			Assertions.assertEquals(-i, que.front());
		}

		Assertions.assertEquals(2 * n, que.size());

		PFullyList<Integer> dcom = que;

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

		System.out.println("testZigZagAsDeque() pass");
	}

	@Test
	public void testArrayAsStackBack() {
		PFullyList<Integer> empty = DeTreeList.create();
		PFullyList<Integer> stk = empty;
		PFullyList<Integer> stk1 = stk.pushBack(1);
		PFullyList<Integer> stk2 = stk1.pushBack(2);
		PFullyList<Integer> stk3 = stk2.popBack();
		PFullyList<Integer> stk4 = stk3.pushBack(3);

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
			PFullyList<Integer> t = stk.pushBack(1);
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
		System.out.println("testArrayAsStackBack() pass");
	}

	@Test
	public void testEquals() {
		PListTestUtil.testEquals(DeTreeList.create());
	}
}
