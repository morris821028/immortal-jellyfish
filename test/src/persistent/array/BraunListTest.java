package persistent.array;

import org.junit.jupiter.api.Test;

import persistent.PListTestUtil;

public class BraunListTest {
	@Test
	public void testPushBackGet() {
		PListTestUtil.testPushBackGet(BraunList.create());
	}

	@Test
	public void testPopBackGet() {
		PListTestUtil.testPopBackGet(BraunList.create());
	}

	@Test
	public void testPushBackSet() {
		PListTestUtil.testPushBackSet(BraunList.create());
	}

	@Test
	public void testToString() {
		PListTestUtil.testToString(BraunList.create());
	}

	@Test
	public void testDemo() {
		PListTestUtil.testDemo(BraunList.create());
	}

	@Test
	public void testZigZag() {
		PListTestUtil.testZigZag(BraunList.create());
	}
}
