package persistent.example.lib;

import persistent.PList;
import persistent.PQueue;
import persistent.util.PCollections;

public class ImmutablePath {
	private final PList<String> list;

	public ImmutablePath() {
		list = PCollections.emptyList();
	}

	public ImmutablePath(String str) {
		list = PList.of(str);
	}

	public ImmutablePath(ImmutablePath parentPath, String str) {
		list = parentPath.list.pushBack(str);
	}

	public ImmutablePath add(String str) {
		return new ImmutablePath(this, str);
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
