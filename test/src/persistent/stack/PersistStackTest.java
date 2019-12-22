package persistent.stack;

import org.junit.jupiter.api.Test;

import persistent.PStackTestUtil;
import persistent.stack.PersistStack;

public class PersistStackTest {
	@Test
	public void testBasic() {
		PersistStack<Integer> stk = PersistStack.create();
		PStackTestUtil.testStack(stk);
	}
}
