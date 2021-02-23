package pso.coco;


import org.apache.commons.io.FileUtils;
import pl.edu.pw.mini.gapso.function.Function;
import pl.edu.pw.mini.gapso.optimizer.GAPSOOptimizer;
import pl.edu.pw.mini.gapso.optimizer.Optimizer;
import pl.edu.pw.mini.gapso.sample.Sample;
import pso.coco.gapso.GAPSOFunctionProblemWrapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

/**
 * An example of benchmarking random search on a COCO suite.
 * <p>
 * Set the parameter BUDGET_MULTIPLIER to suit your needs.
 */
public class ExampleExperiment {
    public static final String BBOB_PROPERTIES = "bbob.properties";
    public static final String GIT_PROPERTIES = "git.properties";
    public static final String GAPSO_JSON = "gapso.json";
    public static final String EXDATA = "exdata";
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
        List<String> beforeStartDirectories;
        File exdataDir = getExperimentDataDirHandle();
        String[] fileList = exdataDir.list();
        if (fileList == null) {
            fileList = new String[0];
        }
        beforeStartDirectories = Arrays.asList(fileList);

        final BBOBExperimentConfigurator bbobConfigurator = new PropertiesBBOBExperimentConfigurator();
        int dimension;


        try {


            /* Set some options for the observer. See documentation for other options. */
            Benchmark benchmark = configureCOCOBenchmark(suiteName, observerName, bbobConfigurator);

            String[] afterStartDirectories;
            afterStartDirectories = exdataDir.list();
            assert afterStartDirectories != null;
            for (String afterStartDirectory : afterStartDirectories) {
                if (!beforeStartDirectories.contains(afterStartDirectory)) {
                    copyFile(afterStartDirectory, BBOB_PROPERTIES);
                    copyFile(afterStartDirectory, GAPSO_JSON);
                }
            }

            /* Initialize timing */
            Timing timing = new Timing();
            /* Iterate over all problems in the suite */
            while ((PROBLEM = benchmark.getNextProblem()) != null) {
                if (functionNotOnList(bbobConfigurator, PROBLEM))
                    continue;
                if (dimensionNotOnList(bbobConfigurator, PROBLEM))
                    continue;
                Function function = new GAPSOFunctionProblemWrapper(PROBLEM);
                Optimizer optimizer = new GAPSOOptimizer();
                Sample bestValue = optimizer.optimize(function);
                printOptima(bestValue);
                System.out.println(PROBLEM.getEvaluations());

                /* Keep track of time */
                timing.timeProblem(PROBLEM);
            }

            /* Output the timing data */
            timing.output();

            benchmark.finalizeBenchmark();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void printOptima(Sample bestValue) {
        try (FileOutputStream fos = new FileOutputStream("best-values-" + PROBLEM.getDimension() + ".csv", true)) {
            PrintStream ps = new PrintStream(fos);
            ps.print(PROBLEM.getId());
            ps.print(";");
            ps.print(PROBLEM.getDimension());
            ps.print(";");
            ps.print(PROBLEM.getEvaluations());
            ps.print(";");
            ps.print(PROBLEM.isFinalTargetHit());
            ps.print(";");
            ps.print(bestValue.getY());
            for (int i = 0; i < PROBLEM.getDimension(); ++i) {
                ps.print(";");
                ps.print(bestValue.getX()[i]);
            }
            ps.println();
        } catch (IOException ex) {
            //DO NOTHING
        }
    }

    private static File getExperimentDataDirHandle() {
        File exdataDir = new File(EXDATA);
        assert exdataDir.exists() || exdataDir.mkdir();
        return exdataDir;
    }

    private static void copyFile(String experimentFolder, String fileName) throws IOException {
        File experimentSettings = new File(fileName);
        if (experimentSettings.exists()) {
            File experimentSettingsCopy = new File(EXDATA + "/" + experimentFolder + "/" + fileName);
            FileUtils.copyFile(experimentSettings, experimentSettingsCopy);
        }
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
        return "result_folder: " + bbobConfigurator.getExperimentName() + "-" + bbobConfigurator.getBuildId() +
                " algorithm_name: " + bbobConfigurator.getExperimentName() + "-" + bbobConfigurator.getBuildId() +
                " algorithm_info: " + bbobConfigurator.getExperimentName() +
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
