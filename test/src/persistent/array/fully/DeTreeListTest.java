package persistent.array.fully;

import org.junit.jupiter.api.Test;

import persistent.PListTestUtil;

public class DeTreeListTest {
	@Test
	public void testPushBackGet() {
		PListTestUtil.testPushBackGet(DeTreeList.create());
	}

	@Test
	public void testPopBackGet() {
		PListTestUtil.testPopBackGet(DeTreeList.create());
	}

	@Test
	public void testPushBackSet() {
		PListTestUtil.testPushBackSet(DeTreeList.create());
	}

	@Test
	public void testToString() {
		PListTestUtil.testToString(DeTreeList.create());
	}

	@Test
	public void testDemo() {
		PListTestUtil.testDemo(DeTreeList.create());
	}

	@Test
	public void testZigZag() {
		PListTestUtil.testZigZag(DeTreeList.create());
	}
}
