package test.stack;

import org.junit.jupiter.api.Test;

import persistent.stack.PersistStack;
import test.PStackTestUtil;

public class PersistStackTest {
	@Test
	public void testBasic() {
		PersistStack<Integer> stk = PersistStack.create();
		PStackTestUtil.testStack(stk);
	}
}
