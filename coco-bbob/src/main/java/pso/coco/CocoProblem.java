package pso.coco;


import pso.Problem;

/**
 * The problem contains some basic properties of the coco_problem_t structure that can be accessed
 * through its getter functions.
 */
public class CocoProblem implements Problem {

    private long pointer; // Pointer to the coco_problem_t object

    private int dimension;
    private int number_of_objectives;
    private int number_of_constraints;

    private double[] lower_bounds;
    private double[] upper_bounds;

    private String id;
    private String name;

    private long index;

    /**
     * Constructs the problem from the pointer.
     *
     * @param pointer pointer to the coco_problem_t object
     * @throws Exception
     */
    public CocoProblem(long pointer) throws Exception {

        super();
        try {
            this.dimension = CocoJNIReflectionWrapper.cocoProblemGetDimension(pointer);
            this.number_of_objectives = CocoJNIReflectionWrapper.cocoProblemGetNumberOfObjectives(pointer);
            this.number_of_constraints = CocoJNIReflectionWrapper.cocoProblemGetNumberOfConstraints(pointer);

            this.lower_bounds = CocoJNIReflectionWrapper.cocoProblemGetSmallestValuesOfInterest(pointer);
            this.upper_bounds = CocoJNIReflectionWrapper.cocoProblemGetLargestValuesOfInterest(pointer);

            this.id = CocoJNIReflectionWrapper.cocoProblemGetId(pointer);
            this.name = CocoJNIReflectionWrapper.cocoProblemGetName(pointer);

            this.index = CocoJNIReflectionWrapper.cocoProblemGetIndex(pointer);

            this.pointer = pointer;
        } catch (Exception e) {
            throw new Exception("CocoProblem constructor failed.\n" + e.toString());
        }
    }

    /**
     * Evaluates the function in point x and returns the result as an array of doubles.
     *
     * @param x
     * @return the result of the function evaluation in point x
     */
    @Override
    public double[] evaluateFunction(double[] x) {
        return CocoJNIReflectionWrapper.cocoEvaluateFunction(this.pointer, x);
    }

    /**
     * Evaluates the constraint in point x and returns the result as an array of doubles.
     *
     * @param x
     * @return the result of the constraint evaluation in point x
     */
    public double[] evaluateConstraint(double[] x) {
        return CocoJNIReflectionWrapper.cocoEvaluateConstraint(this.pointer, x);
    }

    // Getters
    public long getPointer() {
        return this.pointer;
    }

    @Override
    public int getDimension() {
        return this.dimension;
    }

    public int getNumberOfObjectives() {
        return this.number_of_objectives;
    }

    public int getNumberOfConstraints() {
        return this.number_of_constraints;
    }

    @Override
    public double[] getSmallestValuesOfInterest() {
        return this.lower_bounds;
    }

    @Override
    public double getSmallestValueOfInterest(int index) {
        return this.lower_bounds[index];
    }

    @Override
    public double[] getLargestValuesOfInterest() {
        return this.upper_bounds;
    }

    @Override
    public double getLargestValueOfInterest(int index) {
        return this.upper_bounds[index];
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public long getEvaluations() {

        return CocoJNIReflectionWrapper.cocoProblemGetEvaluations(pointer);
    }

    @Override
    public long getIndex() {
        return this.index;
    }

    @Override
    public boolean isFinalTargetHit() {
        return (CocoJNIReflectionWrapper.cocoProblemIsFinalTargetHit(pointer) == 1);
    }

    /* toString method */
    @Override
    public String toString() {
        return this.getId();
    }
}