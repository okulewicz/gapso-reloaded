import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * This class contains the declaration of all the CocoJNI functions.
 */
public class CocoJNI {
    public static final String LIBNAME = "CocoJNI-2.3.0";
    public static final String LIBNAMEDLL = LIBNAME + ".dll";

    /* Load the library */
    static {
        URL libname = Thread.currentThread().getContextClassLoader().getResource(LIBNAMEDLL);
        try {
            if (libname != null) {
                File dllFile = new File(LIBNAMEDLL);
                if (!dllFile.exists()) {
                    FileUtils.copyURLToFile(libname, dllFile);
                }
                System.loadLibrary(LIBNAME);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Native methods */
    public static native void cocoSetLogLevel(String logLevel);

    // Observer
    public static native long cocoGetObserver(String observerName, String observerOptions);

    public static native void cocoFinalizeObserver(long observerPointer);

    public static native long cocoProblemAddObserver(long problemPointer, long observerPointer);

    public static native long cocoProblemRemoveObserver(long problemPointer, long observerPointer);

    // Suite
    public static native long cocoGetSuite(String suiteName, String suiteInstance, String suiteOptions);

    public static native void cocoFinalizeSuite(long suitePointer);

    // CocoProblem
    public static native long cocoSuiteGetNextProblem(long suitePointer, long observerPointer);

    public static native long cocoSuiteGetProblem(long suitePointer, long problemIndex);

    // Functions
    public static native double[] cocoEvaluateFunction(long problemPointer, double[] x);

    public static native double[] cocoEvaluateConstraint(long problemPointer, double[] x);

    // Getters
    public static native int cocoProblemGetDimension(long problemPointer);

    public static native int cocoProblemGetNumberOfObjectives(long problemPointer);

    public static native int cocoProblemGetNumberOfConstraints(long problemPointer);

    public static native double[] cocoProblemGetSmallestValuesOfInterest(long problemPointer);

    public static native double[] cocoProblemGetLargestValuesOfInterest(long problemPointer);

    public static native String cocoProblemGetId(long problemPointer);

    public static native String cocoProblemGetName(long problemPointer);

    public static native long cocoProblemGetEvaluations(long problemPointer);

    public static native long cocoProblemGetIndex(long problemPointer);

    public static native int cocoProblemIsFinalTargetHit(long problemPointer);

}
