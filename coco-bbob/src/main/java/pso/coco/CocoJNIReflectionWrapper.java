package pso.coco;

import java.lang.reflect.Method;

/**
 * This class contains the declaration of all the CocoJNI functions.
 */
public class CocoJNIReflectionWrapper {
    private static Class clazz;
    private static Method cocoEvaluateFunction;

    static {
        try {
            clazz = Class.forName("CocoJNI");
            cocoEvaluateFunction = clazz.getMethod("cocoEvaluateFunction", long.class, double[].class);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /* Native methods */
    public static void cocoSetLogLevel(String logLevel) {
        try {
            clazz.getMethod("cocoSetLogLevel", String.class).invoke(null, logLevel);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    // Observer
    public static long cocoGetObserver(String observerName, String observerOptions) {
        try {
            return (long) clazz.getMethod("cocoGetObserver", String.class, String.class).invoke(null, observerName, observerOptions);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void cocoFinalizeObserver(long observerPointer) {
        try {
            clazz.getMethod("cocoFinalizeObserver", long.class).invoke(null, observerPointer);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static long cocoProblemAddObserver(long problemPointer, long observerPointer) {
        try {
            return (long) clazz.getMethod("cocoProblemAddObserver", long.class, long.class).invoke(null, problemPointer, observerPointer);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static long cocoProblemRemoveObserver(long problemPointer, long observerPointer) {
        try {
            return (long) clazz.getMethod("cocoProblemRemoveObserver", long.class, long.class).invoke(null, problemPointer, observerPointer);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    // Suite
    public static long cocoGetSuite(String suiteName, String suiteInstance, String suiteOptions) {
        try {
            return (long) clazz.getMethod("cocoGetSuite", String.class, String.class, String.class).invoke(null, suiteName, suiteInstance, suiteOptions);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void cocoFinalizeSuite(long suitePointer) {
        try {
            clazz.getMethod("cocoFinalizeSuite", long.class).invoke(null, suitePointer);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    // CocoProblem
    public static long cocoSuiteGetNextProblem(long suitePointer, long observerPointer) {
        try {
            return (long) clazz.getMethod("cocoSuiteGetNextProblem", long.class, long.class).invoke(null, suitePointer, observerPointer);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static long cocoSuiteGetProblem(long suitePointer, long problemIndex) {
        try {
            return (long) clazz.getMethod("cocoSuiteGetProblem", long.class, long.class).invoke(null, suitePointer, problemIndex);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    // Functions
    public static double[] cocoEvaluateFunction(long problemPointer, double[] x) {
        try {
            return (double[]) cocoEvaluateFunction.invoke(null, problemPointer, x);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static double[] cocoEvaluateConstraint(long problemPointer, double[] x) {
        try {
            return (double[]) clazz.getMethod("cocoEvaluateConstraint", long.class, double[].class).invoke(null, problemPointer, x);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    // Getters
    public static int cocoProblemGetDimension(long problemPointer) {
        try {
            return (int) clazz.getMethod("cocoProblemGetDimension", long.class).invoke(null, problemPointer);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static int cocoProblemGetNumberOfObjectives(long problemPointer) {
        try {
            return (int) clazz.getMethod("cocoProblemGetNumberOfObjectives", long.class).invoke(null, problemPointer);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static int cocoProblemGetNumberOfConstraints(long problemPointer) {
        try {
            return (int) clazz.getMethod("cocoProblemGetNumberOfConstraints", long.class).invoke(null, problemPointer);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static double[] cocoProblemGetSmallestValuesOfInterest(long problemPointer) {
        try {
            return (double[]) clazz.getMethod("cocoProblemGetSmallestValuesOfInterest", long.class).invoke(null, problemPointer);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static double[] cocoProblemGetLargestValuesOfInterest(long problemPointer) {
        try {
            return (double[]) clazz.getMethod("cocoProblemGetLargestValuesOfInterest", long.class).invoke(null, problemPointer);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String cocoProblemGetId(long problemPointer) {
        try {
            return (String) clazz.getMethod("cocoProblemGetId", long.class).invoke(null, problemPointer);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String cocoProblemGetName(long problemPointer) {
        try {
            return (String) clazz.getMethod("cocoProblemGetName", long.class).invoke(null, problemPointer);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static long cocoProblemGetEvaluations(long problemPointer) {
        try {
            return (long) clazz.getMethod("cocoProblemGetEvaluations", long.class).invoke(null, problemPointer);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static long cocoProblemGetIndex(long problemPointer) {
        try {
            return (long) clazz.getMethod("cocoProblemGetIndex", long.class).invoke(null, problemPointer);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static int cocoProblemIsFinalTargetHit(long problemPointer) {
        try {
            return (int) clazz.getMethod("cocoProblemIsFinalTargetHit", long.class).invoke(null, problemPointer);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void main(String[] args) {
        CocoJNIReflectionWrapper.cocoSetLogLevel("info");
    }
}
