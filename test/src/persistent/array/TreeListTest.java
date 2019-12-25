package persistent.array;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import persistent.PList;

public class TreeListTest {
	@Test
	public void testPushBackGet() {
		PList<Integer> a = TreeList.create();

		for (int i = 0; i < 32; i++) {
			a = a.pushBack(i);
			for (int j = 0; j <= i; j++)
				Assertions.assertEquals(j, a.get(j));
		}

		a = TreeList.create();
		for (int i = 0; i < 1000000; i++) {
			a = a.pushBack(i);
		}

		for (int i = 0; i < a.size(); i++)
			Assertions.assertEquals(i, a.get(i));
	}
}
