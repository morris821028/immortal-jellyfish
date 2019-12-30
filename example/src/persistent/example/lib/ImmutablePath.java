package persistent.example.lib;

import persistent.PQueue;
import persistent.util.PCollections;

public class ImmutablePath {
	private final PQueue<String> list;

	public ImmutablePath() {
		list = PCollections.emptyQueue();
	}

	public ImmutablePath(String str) {
		list = PQueue.of(str);
	}

	public ImmutablePath(ImmutablePath parentPath, String str) {
		list = parentPath.list.push(str);
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
