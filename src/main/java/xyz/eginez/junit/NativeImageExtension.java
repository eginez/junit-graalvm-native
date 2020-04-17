package xyz.eginez.junit;

import com.oracle.svm.core.c.function.CEntryPointOptions;
import com.oracle.svm.hosted.ImageClassLoader;
import com.oracle.svm.hosted.NativeImageClassLoader;
import com.oracle.svm.hosted.NativeImageGenerator;
import com.oracle.svm.hosted.NativeImageGeneratorRunner;
import com.oracle.svm.hosted.NativeImageOptions;
import com.oracle.svm.hosted.analysis.Inflation;
import com.oracle.svm.hosted.code.CEntryPointData;
import com.oracle.svm.hosted.image.AbstractBootImage;
import com.oracle.svm.hosted.option.HostedOptionCustomizer;
import com.oracle.svm.hosted.option.HostedOptionParser;
import org.graalvm.collections.Pair;
import org.graalvm.compiler.debug.DebugContext;
import org.graalvm.compiler.debug.DebugHandlersFactory;
import org.graalvm.compiler.options.OptionValues;
import org.graalvm.nativeimage.c.function.CEntryPoint;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ForkJoinPool;

public class NativeImageExtension {
    public static void compileMethod(Method method) {
        final String[] classpath = System.getProperty("java.class.path").split(File.pathSeparator);
        System.out.println("Creating class loader with" + String.join("\n ", Arrays.asList(classpath)));
        NativeImageClassLoader classLoader = NativeImageGeneratorRunner.installNativeImageClassLoader(classpath);
        ImageClassLoader imageClassLoader = ImageClassLoader.create(NativeImageGenerator.defaultPlatform(classLoader), classpath, classLoader);

        HostedOptionParser optionParser = new HostedOptionParser(imageClassLoader);
        final HostedOptionCustomizer customizer = new HostedOptionCustomizer(optionParser);

        AbstractBootImage.NativeImageKind kind = AbstractBootImage.NativeImageKind.SHARED_LIBRARY;

        final CEntryPointData cEntryPointData = CEntryPointData.create(method, method.getName(), CEntryPointData.DEFAULT_NAME_TRANSFORMATION,
                "", CEntryPointOptions.NoPrologue.class, CEntryPointOptions.NoEpilogue.class,
                CEntryPoint.FatalExceptionHandler.class, CEntryPointOptions.Publish.SymbolOnly);
        final Pair<Method, CEntryPointData> entryPoint = Pair.create(method, cEntryPointData);

        final OptionValues parsedHostedOptions = new OptionValues(optionParser.getHostedValues());
        DebugContext debug = DebugContext.create(parsedHostedOptions, DebugHandlersFactory.LOADER);
        //DebugContext debug = new DebugContext.Builder(parsedHostedOptions).build();
        ForkJoinPool analysisExecutor = Inflation.createExecutor(debug, NativeImageOptions.getMaximumNumberOfAnalysisThreads(parsedHostedOptions));
        ForkJoinPool compilationExecutor = Inflation.createExecutor(debug, NativeImageOptions.getMaximumNumberOfConcurrentThreads(parsedHostedOptions));

        final NativeImageGenerator nativeImageGenerator = new NativeImageGenerator(imageClassLoader, customizer, entryPoint);
        System.out.println("Init image build");
        final HashMap<Method, CEntryPointData> entryPData = new HashMap<>();
        entryPData.put(method, cEntryPointData);
        nativeImageGenerator.run(entryPData, null, method.getName(), kind, null, compilationExecutor, analysisExecutor, optionParser.getRuntimeOptionNames());

    }

    public void add() {
        System.out.println("1 + 1 == 2");
    }


    public static void main(String[] args) throws Exception {
        final Method add = NativeImageExtension.class.getDeclaredMethod("add");
        compileMethod(add);
    }
}
