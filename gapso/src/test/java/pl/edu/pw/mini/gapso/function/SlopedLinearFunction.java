package pl.edu.pw.mini.gapso.function;

import pl.edu.pw.mini.gapso.bounds.Bounds;

public class SlopedLinearFunction extends FunctionWhiteBox {
    @Override
    public double computeValue(double[] x) {
        return 2 * x[0] - 3 * x[1] + 1;
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
        return new double[]{Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY};
    }
}
