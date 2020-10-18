package persistent;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;

import java.io.PrintWriter;
import java.util.Scanner;

import static org.junit.platform.engine.discovery.ClassNameFilter.includeClassNamePatterns;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

public class MainTest {
	SummaryGeneratingListener listener = new SummaryGeneratingListener();

	public void runAll() {
		LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
				.selectors(selectPackage("persistent")).filters(includeClassNamePatterns(".*Test"))
				.build();
		Launcher launcher = LauncherFactory.create();
		TestPlan testPlan = launcher.discover(request);
		launcher.registerTestExecutionListeners(listener);
		launcher.execute(request);
	}

	// for JMC profiler
	public static void main(String[] argMainTests) {
		Scanner cin = new Scanner(System.in);
		cin.hasNext();

		MainTest runner = new MainTest();
		runner.runAll();

		TestExecutionSummary summary = runner.listener.getSummary();
		summary.printTo(new PrintWriter(System.out));

		cin.next();
		cin.hasNext();
	}
}
