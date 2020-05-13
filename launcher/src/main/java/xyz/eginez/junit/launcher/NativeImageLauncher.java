package xyz.eginez.junit.launcher;

import org.graalvm.nativeimage.ImageSingletons;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;

public class NativeImageLauncher implements Launcher {
    private final Launcher delegate;

    public NativeImageLauncher(Launcher delegate) {
        this.delegate = delegate;
    }

    @Override
    public void registerTestExecutionListeners(TestExecutionListener... listeners) {
        delegate.registerTestExecutionListeners(listeners);
    }

    @Override
    public TestPlan discover(LauncherDiscoveryRequest launcherDiscoveryRequest) {
        return ImageSingletons.lookup(TestPlan.class);
    }

    @Override
    public void execute(LauncherDiscoveryRequest launcherDiscoveryRequest, TestExecutionListener... listeners) {
        TestPlan testPlan = ImageSingletons.lookup(TestPlan.class);
        execute(testPlan, listeners);
    }

    @Override
    public void execute(TestPlan testPlan, TestExecutionListener... listeners) {
        delegate.execute(testPlan, listeners);
    }
}
