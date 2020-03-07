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

	@Test
	public void testIterator() {
		TreeList<String> a = TreeList.create();
		a = a.pushBack("1");
		a = a.pushBack(null);
		a = a.pushBack("3");
		a = a.pushBack("4");
		a = a.pushBack("5");
		a = a.pushBack("6");
		a = a.pushBack("7");

		for (String v : a) {
			System.out.printf("%s\n", v);
		}
	}

	@Test
	public void testEquals() {
		PListTestUtil.testEquals(TreeList.create());
	}
}
