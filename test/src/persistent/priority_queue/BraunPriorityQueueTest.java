package persistent.priority_queue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import persistent.PPriorityQueue;

public class BraunPriorityQueueTest {
	@Test
	public void testBasic() {
		PPriorityQueue<Integer> p = BraunPriorityQueue.<Integer>create((a, b) -> Integer.compare(a, b));

		p = p.push(30);
		Assertions.assertEquals(30, p.top());
		Assertions.assertEquals(1, p.size());
		p = p.push(100);
		Assertions.assertEquals(30, p.top());
		Assertions.assertEquals(2, p.size());
		p = p.push(25);
		Assertions.assertEquals(25, p.top());
		Assertions.assertEquals(3, p.size());
		p = p.push(40);
		Assertions.assertEquals(25, p.top());
		Assertions.assertEquals(4, p.size());

		p = p.pop();
		Assertions.assertEquals(30, p.top());
		Assertions.assertEquals(3, p.size());
		p = p.pop();
		Assertions.assertEquals(40, p.top());
		Assertions.assertEquals(2, p.size());
		p = p.pop();
		Assertions.assertEquals(100, p.top());
		Assertions.assertEquals(1, p.size());
		p = p.pop();
		Assertions.assertEquals(true, p.isEmpty());
	}

	@Test
	public void testSequential() {
		PPriorityQueue<Integer> empty = BraunPriorityQueue.<Integer>create(Integer::compare);

		List<Integer> v = new ArrayList<>();
		int n = 1000000;
		for (int i = 0; i < n; i++)
			v.add(i);

		PPriorityQueue<Integer> p = empty;
		for (int i = 0; i < n; i++) {
			p = p.push(v.get(i));
			Assertions.assertEquals(0, p.top());
			Assertions.assertEquals(i+1, p.size());
		}
		
		for (int i = 0; i < n; i++) {
			Assertions.assertEquals(i, p.top());
			p = p.pop();
			Assertions.assertEquals(n - i - 1, p.size());
		}
	}
	
	@Test
	public void testSequentialReversed() {
		PPriorityQueue<Integer> empty = BraunPriorityQueue.<Integer>create(Integer::compare);

		List<Integer> v = new ArrayList<>();
		int n = 1000000;
		for (int i = n-1; i >= 0; i--)
			v.add(i);

		PPriorityQueue<Integer> p = empty;
		for (int i = 0; i < n; i++) {
			p = p.push(v.get(i));
			Assertions.assertEquals(v.get(i), p.top());
			Assertions.assertEquals(i+1, p.size());
		}
		
		for (int i = 0; i < n; i++) {
			Assertions.assertEquals(i, p.top());
			p = p.pop();
			Assertions.assertEquals(n - i - 1, p.size());
		}
	}
}
