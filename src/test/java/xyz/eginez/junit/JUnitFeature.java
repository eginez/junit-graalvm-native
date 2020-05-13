package xyz.eginez.junit;

import com.oracle.svm.core.annotate.AutomaticFeature;
import com.oracle.svm.hosted.classinitialization.ClassInitializationConfiguration;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeReflection;
import org.graalvm.nativeimage.impl.ReflectionRegistry;
import org.junit.jupiter.api.Test;

@AutomaticFeature
public class JUnitFeature implements Feature {

    @Override
    public void beforeAnalysis(BeforeAnalysisAccess access) {
        System.out.println("registering for reclection");
        RuntimeReflection.register(Test.class);
        RuntimeReflection.register(TestSimple.class);
    }
}
