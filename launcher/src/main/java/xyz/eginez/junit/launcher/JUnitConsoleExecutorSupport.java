package xyz.eginez.junit.launcher;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import org.graalvm.nativeimage.ImageSingletons;
import org.junit.platform.commons.PreconditionViolationException;
import org.junit.platform.console.options.CommandLineOptions;
import org.junit.platform.console.options.PicocliCommandLineOptionsParser;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.core.LauncherConfig;
import org.junit.platform.launcher.core.LauncherFactory;

import java.util.Arrays;
import java.util.Collections;

//TODO fix this
@TargetClass(value = PicocliCommandLineOptionsParser.class)
final class  org_junit_platform_console_options_PicocliCommandLineOptionsParser {

    @Substitute
    public CommandLineOptions parse(String... arguments) {
        final CommandLineOptions commandLineOptions = new CommandLineOptions();
        commandLineOptions.setScanModulepath(true);
        return commandLineOptions;
    }
}

@TargetClass(LauncherFactory.class)
final class org_junit_platform_launcher_core_LauncherFactory {

    @Substitute
    public static Launcher create() throws PreconditionViolationException {
        return ImageSingletons.lookup(Launcher.class);
    }

    @Substitute
    public static Launcher create(LauncherConfig config) throws PreconditionViolationException {
        return ImageSingletons.lookup(Launcher.class);
    }
}

public class JUnitConsoleExecutorSupport {
}
