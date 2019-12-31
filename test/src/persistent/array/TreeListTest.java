package persistent.array;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import persistent.PList;
import persistent.util.PCollections;

public class TreeListTest {
	@Test
	public void testPushBackGet() {
		PList<Integer> a = TreeList.create();

		for (int i = 0; i < 32; i++) {
			a = a.pushBack(i);
			for (int j = 0; j <= i; j++)
				Assertions.assertEquals(j, a.get(j));
		}

		a = TreeList.create();
		for (int i = 0; i < 1000000; i++) {
			a = a.pushBack(i);
		}

		for (int i = 0; i < a.size(); i++)
			Assertions.assertEquals(i, a.get(i));
	}

	@Test
	public void testPopBackGet() {
		PList<Integer> a = TreeList.create();

		for (int i = 0; i < 32; i++) {
			a = a.pushBack(i);
		}

		for (int i = 31; i >= 0; i--) {
			a = a.popBack();
			for (int j = 0; j < i; j++)
				Assertions.assertEquals(j, a.get(j));
		}

		a = TreeList.create();
		for (int i = 0; i < 1000000; i++) {
			a = a.pushBack(i);
		}

		for (int i = 0; i < 1000000; i++) {
			a = a.popBack();
		}
	}

	@Test
	public void testPushBackSet() {
		PList<Integer> a = TreeList.create();
		int[] fib = new int[32];
		fib[0] = 1;
		fib[1] = 1;
		for (int i = 2; i < 32; i++) {
			fib[i] = fib[i - 1] + fib[i - 2];
		}

		for (int i = 0; i < 32; i++)
			a = a.pushBack(null);

		a = a.set(0, 1);
		Assertions.assertEquals(1, a.get(0));
		a = a.set(1, 1);
		Assertions.assertEquals(1, a.get(0));
		Assertions.assertEquals(1, a.get(1));
		for (int i = 2; i < 20; i++) {
			a = a.set(i, a.get(i - 1) + a.get(i - 2));
			for (int j = 0; j <= i; j++)
				Assertions.assertEquals(fib[j], a.get(j), "" + j);
		}

		a = TreeList.create();
		int n = 1000000;
		for (int i = 0; i < 1000000; i++) {
			a = a.pushBack(i);
		}

		for (int i = 0; i < n; i++)
			a = a.set(i, n - i);

		for (int i = 0; i < a.size(); i++)
			Assertions.assertEquals(n - i, a.get(i));
	}

	@Test
	public void testToString() {
		PList<Object> a = TreeList.create();
		a = a.pushBack(3);
		a = a.pushBack(".");
		a = a.pushBack(1);
		a = a.pushBack(4);

		Assertions.assertEquals("{size=4, [3, ., 1, 4]}", a.toString());
	}

	@Test
	public void testDemo() {
		PList<Integer> a = PCollections.emptyList();
		for (int i = 0; i < 10; i++)
			a = a.pushBack(null);
		a = a.set(0, 1);
		a = a.set(1, 1);
		for (int i = 2; i < 10; i++)
			a = a.set(i, a.get(i - 1) + a.get(i - 2));
		Assertions.assertEquals("{size=10, [1, 1, 2, 3, 5, 8, 13, 21, 34, 55]}", a.toString());
		a = a.popBack();
		Assertions.assertEquals("{size=9, [1, 1, 2, 3, 5, 8, 13, 21, 34]}", a.toString());
	}

	@Test
	public void testZigZag() {
		PList<Integer> a = TreeList.create();
		a = a.pushBack(0);
		a = a.pushBack(1);
		for (int i = 0; i < 200000; i++) {
			for (int j = 2; j < 32; j++)
				a = a.pushBack(j);
			for (int j = 2; j < 32; j++)
				a = a.popBack();
		}
		Assertions.assertEquals(a.size(), 2);
		for (int i = 0; i < 1000000; i++) {
			Assertions.assertNotNull(a.popBack());
		}
		for (int i = 0; i < 1000000; i++) {
			Assertions.assertEquals(a.get(0), 0);
			Assertions.assertEquals(a.get(1), 1);
		}
	}
}
