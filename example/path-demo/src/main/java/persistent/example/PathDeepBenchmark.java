package persistent.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import persistent.example.lib.ImmutablePath;
import persistent.example.lib.MutablePath;

@Measurement(iterations = 3, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class PathDeepBenchmark {
	@Param({ "8", "16", "32", "64", "128" })
	int deepSize;

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
	public void testDeepMutablePath() {
		getAllDeepPathsByMutable(deepSize);
	}

	@Benchmark
	public void testDeepImmutablePath() {
		getAllDeepPathsByImmutable(deepSize);
	}
}
