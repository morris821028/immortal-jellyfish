package test;

import org.junit.jupiter.api.Assertions;

import persistent.PStack;

public class PStackTestUtil {
	private PStackTestUtil() {
	}

	public static void testStack(PStack<Integer> empty) {
		PStack<Integer> stk = empty;
		PStack<Integer> stk1 = stk.push(1);
		PStack<Integer> stk2 = stk1.push(2);
		PStack<Integer> stk3 = stk2.pop();
		PStack<Integer> stk4 = stk3.push(3);

		Assertions.assertEquals(1, stk1.top());
		Assertions.assertEquals(2, stk2.top());
		Assertions.assertEquals(1, stk3.top());
		Assertions.assertEquals(3, stk4.top());

		final int n = 1000000;
		for (int i = 0; i < n; i++) {
			stk = stk.push(i);
			Assertions.assertEquals(i + 1, stk.size());
		}
		for (int i = 0; i < n; i++) {
			PStack<Integer> t = stk.push(1);
			Integer v = t.top();
			Assertions.assertEquals(1, v);
		}
		for (int i = n - 1; i >= 0; i--) {
			Integer v = stk.top();
			Assertions.assertEquals(i, v);
			stk = stk.pop();
		}
		System.out.println("testStack() pass");
	}
}
