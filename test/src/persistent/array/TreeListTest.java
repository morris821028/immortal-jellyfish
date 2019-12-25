package persistent.array;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import persistent.PList;

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
			fib[i] = fib[i-1] + fib[i-2];
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
			a = a.set(i, n-i);
		
		for (int i = 0; i < a.size(); i++)
			Assertions.assertEquals(n-i, a.get(i));
	}
}

