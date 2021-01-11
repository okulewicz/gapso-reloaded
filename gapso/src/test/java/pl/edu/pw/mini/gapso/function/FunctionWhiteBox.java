package pl.edu.pw.mini.gapso.function;

import java.util.Arrays;

public abstract class FunctionWhiteBox extends Function {
    private boolean optimumVisited = false;

    public abstract double[] getOptimumLocation();

    protected boolean checkIfOptimumVisited(double[] x) {
        optimumVisited = Arrays.equals(x, getOptimumLocation());
        return optimumVisited;
    }

    @Override
    public final boolean isTargetReached() {
        return optimumVisited;
    }

    public void resetOptimumVisitedState() {
        optimumVisited = false;
    }
}
