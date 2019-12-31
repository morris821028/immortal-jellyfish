package persistent.helper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import persistent.PStack;
import persistent.util.PCollections;

public class TakeTest {
	@Test
	public void testTakeRecursive() {
		PStack<Integer> p = PCollections.emptyStack();

		final int n = 1000000;
		for (int i = 0; i < n; i++)
			p = p.push(i);
		for (int i = n; i >= 1; i--)
			p = Take.create(i, p);
		for (int i = 0; i < n; i++) {
			PStack<Integer> t = p;
			Assertions.assertEquals(n - 1, t.top());
			Assertions.assertEquals(true, t.pop().isEmpty());
		}
	}
}
