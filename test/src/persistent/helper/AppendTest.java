package persistent.helper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import persistent.PStack;
import persistent.helper.Append;
import persistent.util.PCollections;

public class AppendTest {
	@Test
	public void testCreate() {
		PStack<Integer> p = PCollections.emptyStack();
		p = p.push(Integer.valueOf(2));
		p = Append.create(PCollections.emptyStack(), p);
		Assertions.assertEquals(1, p.size());
		Assertions.assertEquals(2, p.top());
		Assertions.assertEquals(false, p.isEmpty());
	}

	@Test
	public void testCreateTwo() {
		PStack<Integer> p = PCollections.emptyStack();
		p = p.push(Integer.valueOf(2));
		PStack<Integer> q = PCollections.emptyStack();
		q = q.push(Integer.valueOf(4));
		q = q.push(Integer.valueOf(3));
		PStack<Integer> r = Append.create(p, q);
		Assertions.assertEquals(3, r.size());
		Assertions.assertEquals(2, r.top());
		Assertions.assertEquals(false, r.isEmpty());

		Assertions.assertEquals(3, r.pop().top());
		Assertions.assertEquals(2, r.pop().size());
	}
}
