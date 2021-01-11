package pso.coco;


/**
 * The benchmark contains a suite and an observer and is able to return the next problem.
 */
public class Benchmark {

    private Suite suite;
    private Observer observer;

    /**
     * Constructor
     */
    public Benchmark(Suite suite, Observer observer) {
        this.suite = suite;
        this.observer = observer;
    }

    /**
     * Function that returns the next problem in the suite. When it comes to the end of the suite,
     * it returns null.
     *
     * @return the next problem in the suite or null when there is no next problem
     * @throws Exception
     */
    public CocoProblem getNextProblem() throws Exception {

        try {
            long problemPointer = CocoJNIReflectionWrapper.cocoSuiteGetNextProblem(suite.getPointer(), observer.getPointer());

            if (problemPointer == 0)
                return null;

            return new CocoProblem(problemPointer);
        } catch (Exception e) {
            throw new Exception("Fetching of next problem failed.\n" + e.toString());
        }
    }

    /**
     * Finalizes the observer and suite. This method needs to be explicitly called in order to log
     * the last results.
     *
     * @throws Exception
     */
    public void finalizeBenchmark() throws Exception {

        try {
            observer.finalizeObserver();
            suite.finalizeSuite();
        } catch (Exception e) {
            throw new Exception("Benchmark finalization failed.\n" + e.toString());
        }
    }
}