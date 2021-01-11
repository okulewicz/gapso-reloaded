package pso.coco.gapso;

import pl.edu.pw.mini.gapso.bounds.Bounds;
import pl.edu.pw.mini.gapso.bounds.SimpleBounds;
import pl.edu.pw.mini.gapso.function.Function;
import pso.Problem;

public class GAPSOFunctionProblemWrapper extends Function {
    private final Problem _problem;
    private final double[] smallestValuesOfInterest;
    private final double[] largestValuesOfInterest;

    public GAPSOFunctionProblemWrapper(Problem problem) {
        _problem = problem;
        smallestValuesOfInterest = _problem.getSmallestValuesOfInterest();
        largestValuesOfInterest = _problem.getLargestValuesOfInterest();
    }

    @Override
    protected double computeValue(double[] x) {
        return _problem.evaluateFunction(x)[0];
    }

    @Override
    public boolean isTargetReached() {
        return _problem.isFinalTargetHit();
    }

    @Override
    public int getDimension() {
        return _problem.getDimension();
    }

    @Override
    public Bounds getBounds() {
        return new SimpleBounds(smallestValuesOfInterest,
                largestValuesOfInterest);
    }
}
