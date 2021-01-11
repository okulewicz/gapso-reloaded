package pl.edu.pw.mini.gapso.function;

import pl.edu.pw.mini.gapso.bounds.Bounds;

public class PartiallyFlatLinearFunction extends FunctionWhiteBox {
    @Override
    public double computeValue(double[] x) {
        checkIfOptimumVisited(x);
        return -2 * x[0] + 1;
    }

    @Override
    public int getDimension() {
        return 2;
    }

    @Override
    public Bounds getBounds() {
        return null;
    }

    @Override
    public double[] getOptimumLocation() {
        return new double[]{Double.POSITIVE_INFINITY, Double.NaN};
    }
}
