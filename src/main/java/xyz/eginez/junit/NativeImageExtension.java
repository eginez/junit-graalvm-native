package xyz.eginez.junit;

import org.graalvm.nativeimage.ImageInfo;
import org.graalvm.nativeimage.hosted.RuntimeReflection;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class NativeImageExtension implements TestWatcher, AfterAllCallback {
    Set<Class<?>> testClasses = new HashSet<>();

    @Override
    public void testSuccessful(ExtensionContext context) {
        testClasses.add(context.getRequiredTestClass());
    }


    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        final String all = testClasses.stream().map(Class::getName).collect(Collectors.joining(","));
        System.out.println(all);
    }
}
