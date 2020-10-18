package persistent.benchmark;

import java.util.LinkedList;
import java.util.List;

import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

public class Main {
	public static void main(String[] args) {
		OptionsBuilder options = new OptionsBuilder();

//		options.include(QueueBenchmark.class.getSimpleName());
		options.include(CopyBenchmark.class.getSimpleName());

		options.warmupTime(TimeValue.milliseconds(100)) //
				.measurementTime(TimeValue.seconds(1)) //
				// .result("result.json") //
				.resultFormat(ResultFormatType.JSON) //
				// .addProfiler(GCProfiler.class) // garbage collection profiler
				.forks(1).build();
		try {
			new Runner(options).run();
		} catch (RunnerException e) {
			e.printStackTrace();
		}
	}
}
