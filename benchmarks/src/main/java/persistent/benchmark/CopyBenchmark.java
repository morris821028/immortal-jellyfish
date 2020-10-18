package persistent.benchmark;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import persistent.PQueue;
import persistent.queue.PreEvalQueue;
import persistent.queue.RealtimeQueue;

@Warmup(iterations = 1, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 2, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class CopyBenchmark {
	final int prefixSize = 5;
	final int testIteration = 10000;
	final int testDescendant = 100;

	@Benchmark
	public void testRtQueueDerived(Blackhole blackhole) {
		PQueue<Integer> q = RealtimeQueue.create();
		for (int i = 0; i < prefixSize; i++)
			q = q.push(i);

		for (int i = 0; i < testIteration; i++) {
			for (int j = 0; j < testDescendant; j++) {
				PQueue<Integer> r = q.push(j);
				blackhole.consume(r);
			}
		}
		blackhole.consume(q);
	}

	@Benchmark
	public void testListDerived(Blackhole blackhole) {
		LinkedList<Integer> q = new LinkedList<>();
		for (int i = 0; i < prefixSize; i++)
			q.add(i);
		for (int i = 0; i < testIteration; i++) {
			for (int j = 0; j < testDescendant; j++) {
				LinkedList<Integer> r = new LinkedList<>(q);
				r.add(j);
				blackhole.consume(r);
			}
		}
		blackhole.consume(q);
	}
}
