package persistent.example.lib;

import java.util.ArrayList;

public class MutablePath {
	private final ArrayList<String> list;

	public MutablePath() {
		list = new ArrayList<>();
	}

	public MutablePath(String str) {
		list = new ArrayList<>();
		list.add(str);
	}

	public MutablePath(MutablePath parentPath, String str) {
		list = new ArrayList<>(parentPath.list);
		list.add(str);
	}

	public void add(String str) {
		list.add(str);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String s : list) {
			sb.append("/");
			sb.append(s);
		}
		return sb.toString();
	}
}
