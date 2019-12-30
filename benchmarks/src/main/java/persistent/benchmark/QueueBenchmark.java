package persistent.benchmark;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import persistent.PQueue;
import persistent.queue.PreEvalQueue;
import persistent.queue.RealtimeQueue;

@Warmup(iterations = 2, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 2, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class QueueBenchmark {
	@Param({ "1000", "100000", "1000000" })
	int queSize;

	@Benchmark
	public void testPushRealtime() {
		PQueue<Integer> q = RealtimeQueue.create();
		for (int i = 0; i < queSize; i++)
			q = q.push(i);
	}

	@Benchmark
	public void testPushPreEval() {
		PQueue<Integer> q = PreEvalQueue.create();
		for (int i = 0; i < queSize; i++)
			q = q.push(i);
	}
}
