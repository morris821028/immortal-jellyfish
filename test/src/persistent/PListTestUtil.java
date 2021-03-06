package persistent;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;

public class PListTestUtil {
	private PListTestUtil() {
	}

	public static void testPushBackGet(PList<Integer> empty) {
		PList<Integer> a = empty;

		for (int i = 0; i < 32; i++) {
			a = a.pushBack(i);
			for (int j = 0; j <= i; j++)
				Assertions.assertEquals(j, a.get(j));
		}

		a = empty;
		for (int i = 0; i < 1000000; i++) {
			a = a.pushBack(i);
		}

		for (int i = 0; i < a.size(); i++)
			Assertions.assertEquals(i, a.get(i));
	}

	public static void testPopBackGet(PList<Integer> empty) {
		PList<Integer> a = empty;

		for (int i = 0; i < 32; i++) {
			a = a.pushBack(i);
		}

		for (int i = 31; i >= 0; i--) {
			a = a.popBack();
			for (int j = 0; j < i; j++)
				Assertions.assertEquals(j, a.get(j));
		}

		a = empty;
		for (int i = 0; i < 1000000; i++) {
			a = a.pushBack(i);
		}

		for (int i = 0; i < 1000000; i++) {
			try {
				a = a.popBack();
			} catch (Exception e) {
				System.out.printf("e %d\n", i);
				Assertions.fail(e);
			}
		}
	}

	public static void testPushBackSet(PList<Integer> empty) {
		PList<Integer> a = empty;
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

		a = empty;
		int n = 1000000;
		for (int i = 0; i < 1000000; i++) {
			a = a.pushBack(i);
		}

		for (int i = 0; i < n; i++)
			a = a.set(i, n - i);

		for (int i = 0; i < a.size(); i++)
			Assertions.assertEquals(n - i, a.get(i));
	}

	public static void testToString(PList<Object> empty) {
		PList<Object> a = empty;
		a = a.pushBack(3);
		a = a.pushBack(".");
		a = a.pushBack(1);
		a = a.pushBack(4);

		Assertions.assertEquals("{size=4, [3, ., 1, 4]}", a.toString());
	}

	public static void testDemo(PList<Integer> empty) {
		PList<Integer> a = empty;
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

	public static void testZigZag(PList<Integer> empty) {
		PList<Integer> a = empty;
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

	public static void testEquals(PList<Integer> empty) {
		PList<Integer> a = empty;
		a = a.pushBack(5);
		a = a.pushBack(7);
		PList<Integer> b = empty;
		b = b.pushBack(5);
		b = b.pushBack(7);
		Assertions.assertTrue(a.equals(b));
		Assertions.assertTrue(b.equals(a));

		Set<PList<Integer>> set = new HashSet<>();
		set.add(a);
		set.add(b);
		Assertions.assertEquals(set.size(), 1);

		for (int i = 0; i < 10000; i++) {
			a = a.pushBack(i);
			b = b.pushBack(i);
			Assertions.assertEquals(a, b);
		}

		b = b.pushBack(5);
		Assertions.assertFalse(a.equals(b));
		Assertions.assertFalse(b.equals(a));
	}
}
