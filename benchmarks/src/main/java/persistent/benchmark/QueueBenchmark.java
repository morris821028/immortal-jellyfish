package persistent.benchmark;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
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
public class QueueBenchmark {
	@Param({ "1000000" })
	int queSize;

	@Benchmark
	public void testOnlyPushRealtime(Blackhole blackhole) {
		PQueue<Integer> q = RealtimeQueue.create();
		for (int i = 0; i < queSize; i++)
			q = q.push(i);
		blackhole.consume(q);
	}

	@Benchmark
	public void testOnlyPushPreEval(Blackhole blackhole) {
		PQueue<Integer> q = PreEvalQueue.create();
		for (int i = 0; i < queSize; i++)
			q = q.push(i);
		blackhole.consume(q);
	}

	@Benchmark
	public void testPopPushRealtime(Blackhole blackhole) {
		PQueue<Integer> q = RealtimeQueue.create();
		for (int i = 0; i < queSize; i++)
			q = q.push(i);
		for (int i = 0; i < queSize; i++) {
			PQueue<Integer> p = q.push(i);
			blackhole.consume(p);
		}
		for (int i = 0; i < queSize; i++)
			q = q.pop();
		blackhole.consume(q);
	}

	@Benchmark
	public void testPopPushPreEval(Blackhole blackhole) {
		PQueue<Integer> q = PreEvalQueue.create();
		for (int i = 0; i < queSize; i++)
			q = q.push(i);
		for (int i = 0; i < queSize; i++) {
			PQueue<Integer> p = q.push(i);
			blackhole.consume(p);
		}
		for (int i = 0; i < queSize; i++)
			q = q.pop();
		blackhole.consume(q);
	}

	private PQueue<Integer> rtQueue;
	private PQueue<Integer> peQueue;

	@Setup
	public void setup() {
		rtQueue = RealtimeQueue.create();
		for (int i = 0; i < queSize; i++)
			rtQueue = rtQueue.push(i);

		peQueue = PreEvalQueue.create();
		for (int i = 0; i < queSize; i++)
			peQueue = peQueue.push(i);
	}

	@TearDown
	public void teardown() {
	}

	@Benchmark
	public void testFrontRealtime(Blackhole blackhole) {
		for (int i = 0; i < queSize; i++) {
			Integer v = rtQueue.front();
			blackhole.consume(v);
		}
	}

	@Benchmark
	public void testFrontPreEval(Blackhole blackhole) {
		for (int i = 0; i < queSize; i++) {
			Integer v = peQueue.front();
			blackhole.consume(v);
		}
	}

	@Benchmark
	public void testSizeRealtime(Blackhole blackhole) {
		for (int i = 0; i < queSize; i++) {
			int sz = rtQueue.size();
			blackhole.consume(sz);
		}
	}

	@Benchmark
	public void testSizePreEval(Blackhole blackhole) {
		for (int i = 0; i < queSize; i++) {
			int sz = peQueue.size();
			blackhole.consume(sz);
		}
	}

	@Benchmark
	public void testIsEmptyRealtime(Blackhole blackhole) {
		for (int i = 0; i < queSize; i++) {
			blackhole.consume(rtQueue.isEmpty());
		}
	}

	@Benchmark
	public void testIsEmptyPreEval(Blackhole blackhole) {
		for (int i = 0; i < queSize; i++) {
			blackhole.consume(peQueue.isEmpty());
		}
	}

	@Benchmark
	public void testPushCopyRealtime(Blackhole blackhole) {
		for (int i = 0; i < queSize; i++) {
			blackhole.consume(rtQueue.push(i));
		}
	}

	@Benchmark
	public void testPushCopyPreEval(Blackhole blackhole) {
		for (int i = 0; i < queSize; i++) {
			blackhole.consume(peQueue.push(i));
		}
	}

	@Benchmark
	public void testOnlyPopRealtime(Blackhole blackhole) {
		PQueue<Integer> q = rtQueue;
		for (int i = 0; i < queSize; i++) {
			q = q.pop();
			blackhole.consume(q);
		}
		for (int i = 0; i < queSize; i++) {
			blackhole.consume(rtQueue.pop());
		}
	}

	@Benchmark
	public void testOnlyPopPreEval(Blackhole blackhole) {
		PQueue<Integer> q = peQueue;
		for (int i = 0; i < queSize; i++) {
			q = q.pop();
			blackhole.consume(q);
		}
		for (int i = 0; i < queSize; i++) {
			blackhole.consume(peQueue.pop());
		}
	}
}
