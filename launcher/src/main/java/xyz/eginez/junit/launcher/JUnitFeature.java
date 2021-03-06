package xyz.eginez.junit.launcher;

import com.oracle.svm.core.annotate.AutomaticFeature;
import org.graalvm.nativeimage.ImageSingletons;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeClassInitialization;
import org.graalvm.nativeimage.hosted.RuntimeReflection;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AutomaticFeature
public class JUnitFeature implements Feature {

    @Override
    public void beforeAnalysis(BeforeAnalysisAccess access) {
        RuntimeClassInitialization.initializeAtBuildTime("org.junit");
        RuntimeClassInitialization.initializeAtBuildTime(NativeImageLauncher.class);

        Launcher launcher = LauncherFactory.create();
        registerTestPlan(launcher);
        saveCommandLineOptions(launcher);
        ImageSingletons.add(Launcher.class, new NativeImageLauncher(launcher));
    }

    private static void saveCommandLineOptions(Launcher launcher) {
    }

    private static void registerTestPlan(Launcher launcher) {
        // Run a a junit launcher to discover tests and register classes for reflection
        URLClassLoader contextClassLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
        URL[] urls = contextClassLoader.getURLs();
        Set<Path> classpathRoots = Stream.of(urls)//
                .map(url -> {
                    try {
                        return url.toURI();
                    } catch (URISyntaxException e) {
                        throw new RuntimeException("Test discovery failed due to" +
                                "URL to URI conversion problems of the classpath roots.", e);
                    }
                })
                .map(Paths::get)//
                //do not look in graalvm archives
                .filter(p -> !p.toString().contains("graalvm"))
                .peek(path -> System.out.println(path.toString()))
                .collect(Collectors.toSet());

        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()//
                .selectors(DiscoverySelectors.selectClasspathRoots(classpathRoots))//
                .build();
        TestPlan tests = launcher.discover(request);
        registerForReflection(tests);
        ImageSingletons.add(TestPlan.class, tests);

    }

    private static void registerForReflection(TestPlan testPlan) {
        testPlan.getRoots().stream()//
                .flatMap(rootIdentifier -> testPlan.getDescendants(rootIdentifier).stream())//
                .map(TestIdentifier::getSource)//
                .filter(Optional::isPresent)//
                .map(Optional::get)//
                .forEach((TestSource testSource) -> {
                    if (testSource instanceof ClassSource) {
                        ClassSource classSource = (ClassSource) testSource;
                        System.out.println("Registering " + classSource.getClassName());
                        RuntimeReflection.registerForReflectiveInstantiation(classSource.getJavaClass());
                    } else if (testSource instanceof MethodSource) {
                        // ignore
                    } else {
                        throw new RuntimeException("Unsupported TestSource type: " + testSource);
                    }
                });
    }
}
