package xyz.eginez.junit.launcher;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import org.graalvm.nativeimage.ImageSingletons;
import org.junit.platform.console.options.CommandLineOptions;
import org.junit.platform.console.options.Details;
import org.junit.platform.console.options.PicocliCommandLineOptionsParser;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.function.Supplier;

@TargetClass(value = PicocliCommandLineOptionsParser.class)
final class  org_junit_platform_console_options_PicocliCommandLineOptionsParser {

    @Substitute
    //TODO fix this
    public CommandLineOptions parse(String... arguments) {
        final CommandLineOptions commandLineOptions = new CommandLineOptions();
        commandLineOptions.setSelectedClasses(Collections.singletonList("xyz.eginez.junit.TestSimple"));
        commandLineOptions.setScanModulepath(false);
        return commandLineOptions;
    }
}

@TargetClass(org.junit.platform.console.tasks.ConsoleTestExecutor.class)
final class org_junit_platform_console_tasks_ConsoleTextExecutor {

    @Alias
    private Supplier<Launcher> launcherSupplier;

    @Alias
    private native SummaryGeneratingListener registerListeners(PrintWriter out, Launcher launcher);

    @Alias
    private native void printSummary(TestExecutionSummary summary, PrintWriter out);

    @Substitute
    private TestExecutionSummary executeTests(PrintWriter out) {
        final TestPlan testPlan = ImageSingletons.lookup(TestPlan.class);
        final Launcher launcher = launcherSupplier.get();
        SummaryGeneratingListener summaryListener = registerListeners(out, launcher);
        launcher.execute(testPlan);

        TestExecutionSummary summary = summaryListener.getSummary();

        if (summary.getTotalFailureCount() > 0 ) {
            printSummary(summary, out);
        }

        return summary;
    }

}

public class JUnitConsoleExecutorSupport {
}
