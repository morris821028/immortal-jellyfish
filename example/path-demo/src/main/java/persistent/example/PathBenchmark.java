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
import org.openjdk.jmh.annotations.Param;
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
	@Param({ "1", "2", "4", "8", "16" })
	int fullSize;

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

	@Benchmark
	public void testMutablePath() {
		getAllPathsByMutable(fullSize);
	}

	@Benchmark
	public void testImmutablePath() {
		getAllPathsByImmutable(fullSize);
	}
}
