package persistent.array;

import org.junit.jupiter.api.Test;

import persistent.PListTestUtil;

public class TreeListTest {
	@Test
	public void testPushBackGet() {
		PListTestUtil.testPushBackGet(TreeList.create());
	}

	@Test
	public void testPopBackGet() {
		PListTestUtil.testPopBackGet(TreeList.create());
	}

	@Test
	public void testPushBackSet() {
		PListTestUtil.testPushBackSet(TreeList.create());
	}

	@Test
	public void testToString() {
		PListTestUtil.testToString(TreeList.create());
	}

	@Test
	public void testDemo() {
		PListTestUtil.testDemo(TreeList.create());
	}

	@Test
	public void testZigZag() {
		PListTestUtil.testZigZag(TreeList.create());
	}
}
