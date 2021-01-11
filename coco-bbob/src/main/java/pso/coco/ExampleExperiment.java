package pso.coco;


import java.util.Arrays;

/**
 * An example of benchmarking random search on a COCO suite.
 * <p>
 * Set the parameter BUDGET_MULTIPLIER to suit your needs.
 */
public class ExampleExperiment {
    /**
     * The maximal number of independent restarts allowed for an algorithm that
     * restarts itself.
     */
    private static final int INDEPENDENT_RESTARTS = 10000;
    /**
     * The problem to be optimized (needed in order to simplify the interface
     * between the optimization algorithm and the COCO platform).
     */
    private static CocoProblem PROBLEM;


//
//    /**
//     * The random seed. Change if needed.
//     */
//    public static final long RANDOM_SEED = 0xdeadbeef;

    /**
     * The main method initializes the random number generator and calls the
     * example experiment on the bi-objective suite.
     */
    public static void main(String[] args) {
        /* Change the log level to "warning" to get less output */
        CocoJNIReflectionWrapper.cocoSetLogLevel("info");

        /* Start the actual experiments on a test suite and use a matching logger, for
         * example one of the following:
         *
         *   bbob                 24 unconstrained noiseless single-objective functions
         *   bbob-biobj           55 unconstrained noiseless bi-objective functions
         *   bbob-biobj-ext       92 unconstrained noiseless bi-objective functions
         *   bbob-largescale      24 unconstrained noiseless single-objective functions in large dimension
         *
         * Adapt to your need. Note that the experiment is run according
         * to the settings, defined in exampleExperiment(...) below.
         */
        exampleExperiment("bbob", "bbob");

        System.out.println("Done!");
        System.out.flush();
    }

    /**
     * A simple example of benchmarking random search on a given suite with
     * default instances that can serve also as a timing experiment.
     *
     * @param suiteName    Name of the suite (e.g. "bbob" or "bbob-biobj").
     * @param observerName Name of the observer matching with the chosen
     *                     suite
     *                     (e.g. "bbob-biobj" when using the "bbob-biobj-ext" suite).
     */
    private static void exampleExperiment(String suiteName, String observerName) {

        final BBOBExperimentConfigurator bbobConfigurator = new PropertiesBBOBExperimentConfigurator();
        int dimension;


        try {
            /* Set some options for the observer. See documentation for other options. */
            Benchmark benchmark = configureCOCOBenchmark(suiteName, observerName, bbobConfigurator);

            /* Initialize timing */
            Timing timing = new Timing();
            /* Iterate over all problems in the suite */
            while ((PROBLEM = benchmark.getNextProblem()) != null) {
                if (functionNotOnList(bbobConfigurator, PROBLEM))
                    continue;
                if (dimensionNotOnList(bbobConfigurator, PROBLEM))
                    continue;
                for (int run = 1; run <= 1 + INDEPENDENT_RESTARTS; run++) {
                    //OptimizationAlgorithm optimizationAlgorithm = chooseOptimizationAlgorithm(configurator, bbobConfigurator, PROBLEM, f, locationInitializer, behaviours, POP_SIZE);
                    long evaluationsDone = PROBLEM.getEvaluations();
                }

                /* Keep track of time */
                timing.timeProblem(PROBLEM);
            }

            /* Output the timing data */
            timing.output();

            benchmark.finalizeBenchmark();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        CocoJNIReflectionWrapper.deleteLibFile();
    }

    private static Benchmark configureCOCOBenchmark(String suiteName, String observerName, BBOBExperimentConfigurator bbobConfigurator) throws Exception {
        final String observerOptions = buildObserverOptions(suiteName, bbobConfigurator);

        /* Initialize the suite and observer.
         * For more details on how to change the default options, see
         * http://numbbo.github.io/coco-doc/C/#suite-parameters and
         * http://numbbo.github.io/coco-doc/C/#observer-parameters. */
        Suite suite = new Suite(suiteName, "", "");
        Observer observer = new Observer(observerName, observerOptions);
        return new Benchmark(suite, observer);
    }

    private static void printoutInitialStatus() {
        System.out.println("Running the example experiment... (might take time, be patient)");
        System.out.println("Algorithm ");
        System.out.flush();
    }

    private static String buildObserverOptions(String suiteName, BBOBExperimentConfigurator bbobConfigurator) {
        return "result_folder: temp" +
                " algorithm_name: temp" +
                " algorithm_info: temp" +
                "";
    }

    private static boolean dimensionNotOnList(BBOBExperimentConfigurator bbobConfigurator, CocoProblem PROBLEM) {
        int dimension = PROBLEM.getDimension();
        return Arrays.stream(bbobConfigurator.getDimensionsList()).noneMatch(dm -> dm == dimension);
    }

    private static boolean functionNotOnList(BBOBExperimentConfigurator bbobConfigurator, CocoProblem PROBLEM) {
        return Arrays.stream(bbobConfigurator.getFunctionsList())
                .noneMatch(fName -> PROBLEM.getName().contains(fName + " "))
                && Arrays.stream(bbobConfigurator.getFunctionsList())
                .noneMatch("all"::equals);
    }


}
