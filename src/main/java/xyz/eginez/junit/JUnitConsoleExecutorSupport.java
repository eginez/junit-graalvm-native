package xyz.eginez.junit;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import org.junit.platform.console.options.CommandLineOptions;
import org.junit.platform.console.options.PicocliCommandLineOptionsParser;
import org.junit.platform.console.tasks.ConsoleTestExecutor;

import java.util.Collections;
import java.util.Optional;

@TargetClass(value = PicocliCommandLineOptionsParser.class)
final class  org_junit_platform_console_options_PicocliCommandLineOptionsParser {

    @Substitute
    public CommandLineOptions parse(String... arguments) {
        final CommandLineOptions commandLineOptions = new CommandLineOptions();
        commandLineOptions.setSelectedClasses(Collections.singletonList("xyz.eginez.junit.TestSimple"));
        return commandLineOptions;
    }
}

public class JUnitConsoleExecutorSupport {
}
