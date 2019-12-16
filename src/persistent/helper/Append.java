package persistent.helper;

import persistent.PStack;
import persistent.stack.AppendStack;

public class Append {
	private Append() {}
	
	public static <T> PStack<T> create(PStack<T> l, PStack<T> r) {
		return AppendStack.create(l, r);
	}

	public static <T> PStack<T> create(T l, PStack<T> r) {
		return AppendStack.create(l, r);
	}
}
