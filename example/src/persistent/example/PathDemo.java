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

	/**
	 * Bad practice, because we compose a path in O(n^2) time. It is a common
	 * mistake if we use mutable objects.
	 */
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

	public static List<ImmutablePath> getAllDeepPathsByImmutable(int dep) {
		List<ImmutablePath> ret = new ArrayList<>();
		ret.add(new ImmutablePath());
		for (int i = 0; i < dep; i++) {
			ImmutablePath p = ret.get(ret.size() - 1);
			ret.add(p.add("0"));
			ret.add(p.add("1"));
		}
		return ret;
	}

	public static List<MutablePath> getAllDeepPathsByMutable(int dep) {
		List<MutablePath> ret = new ArrayList<>();
		ret.add(new MutablePath());
		for (int i = 0; i < dep; i++) {
			MutablePath p = ret.get(ret.size() - 1);
			ret.add(new MutablePath(p, "0"));
			ret.add(new MutablePath(p, "1"));
		}
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

		// no advantage by immutable in short size
		measure(() -> {
			for (int i = 0; i < 64; i++)
				getAllPathsByMutable(8);
		});
		measure(() -> {
			for (int i = 0; i < 64; i++)
				getAllPathsByImmutable(8);
		});

		// has advantage in bigger size
		measure(() -> {
			for (int i = 0; i < 64; i++)
				getAllDeepPathsByMutable(64);
		});
		measure(() -> {
			for (int i = 0; i < 64; i++)
				getAllDeepPathsByImmutable(64);
		});
	}
}
