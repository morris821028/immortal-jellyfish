package persistent.example;

import java.util.ArrayList;
import java.util.List;

import persistent.example.lib.ImmutablePath;
import persistent.example.lib.MutablePath;

public class PathDemo {
	private PathDemo() {
	}

	private static void computePathsByImmutable(ImmutablePath parentPath, List<ImmutablePath> paths, int dep) {
		paths.add(parentPath);
		if (dep > 0) {
			computePathsByImmutable(parentPath.add("0"), paths, dep - 1);
			computePathsByImmutable(parentPath.add("1"), paths, dep - 1);
		}
	}

	public static List<ImmutablePath> getAllPathsByImmutable(int dep) {
		List<ImmutablePath> ret = new ArrayList<>();
		computePathsByImmutable(new ImmutablePath(), ret, dep);
		return ret;
	}

	private static void computePathsByMutable(MutablePath parentPath, List<MutablePath> paths, int dep) {
		paths.add(parentPath);
		if (dep > 0) {
			computePathsByMutable(new MutablePath(parentPath, "0"), paths, dep - 1);
			computePathsByMutable(new MutablePath(parentPath, "1"), paths, dep - 1);
		}
	}

	public static List<MutablePath> getAllPathsByMutable(int dep) {
		List<MutablePath> ret = new ArrayList<>();
		computePathsByMutable(new MutablePath(), ret, dep);
		return ret;
	}

	private static void measure(Runnable r) {
		System.gc();
		long start = System.nanoTime();
		r.run();
		long time = System.nanoTime() - start;
		System.out.printf("Time: %f\n\n", time / 10e+9);
	}

	public static void main(String[] args) {
		List<ImmutablePath> a = getAllPathsByImmutable(3);
		List<MutablePath> b = getAllPathsByMutable(3);

		if (a.size() != b.size())
			throw new IllegalStateException();

		for (int i = 0; i < a.size(); i++) {
			if (!a.get(i).toString().equals(b.get(i).toString()))
				throw new IllegalStateException();
		}

		a.clear();
		b.clear();

		measure(() -> {
			for (int i = 0; i < 64; i++)
				getAllPathsByMutable(16);
		});
		measure(() -> {
			for (int i = 0; i < 64; i++)
				getAllPathsByImmutable(16);
		});
	}
}
