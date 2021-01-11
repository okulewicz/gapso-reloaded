package pso;

public interface Problem {
    double[] evaluateFunction(double[] x);

    int getDimension();

    double[] getSmallestValuesOfInterest();

    double getSmallestValueOfInterest(int index);

    double[] getLargestValuesOfInterest();

    double getLargestValueOfInterest(int index);

    String getId();

    String getName();

    long getEvaluations();

    long getIndex();

    boolean isFinalTargetHit();
}
