package persistent.benchmark;

import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

public class Main {
	public static void main(String[] args) {

		Options options = new OptionsBuilder().include(QueueBenchmark.class.getSimpleName())
				.warmupTime(TimeValue.milliseconds(100)).measurementTime(TimeValue.seconds(1))
				.addProfiler(GCProfiler.class).forks(1).build();
		try {
			new Runner(options).run();
		} catch (RunnerException e) {
			e.printStackTrace();
		}
	}
}
