package persistent.example;

import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import persistent.example.lib.ImmutablePath;
import persistent.example.lib.MutablePath;

@Warmup(iterations = 3, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 3, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class PathBenchmark {
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

	@Benchmark
	public void testMutablePath8() {
		getAllPathsByMutable(8);
	}

	@Benchmark
	public void testImmutablePath8() {
		getAllPathsByImmutable(8);
	}

	@Benchmark
	public void testDeepMutablePath64() {
		getAllDeepPathsByMutable(64);
	}

	@Benchmark
	public void testDeepImmutablePath64() {
		getAllDeepPathsByImmutable(8);
	}
}
