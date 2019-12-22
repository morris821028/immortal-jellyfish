package persistent.stack;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import persistent.PStack;
import persistent.PStackTestUtil;
import persistent.stack.PersistStack;

public class PersistStackTest {
	@Test
	public void testBasic() {
		PersistStack<Integer> stk = PersistStack.create();
		PStackTestUtil.testStack(stk);
	}

	@Test
	public void testToString() {
		PersistStack<Integer> stk = PersistStack.create();
		Assertions.assertEquals("Stack {size = 0, []}", stk.toString());
		stk = stk.push(1);
		stk = stk.push(2);
		Assertions.assertEquals("Stack {size = 2, [2, 1]}", stk.toString());
	}

	@Test
	public void testToArrayList() {
		PStack<Integer> stk = PStack.of(1);
		stk = stk.push(2);
		List<Integer> list = PStack.toArrayList(stk);
		List<Integer> excepted = new ArrayList<>();
		excepted.add(2);
		excepted.add(1);
		Assertions.assertEquals(excepted, list);
	}
}
