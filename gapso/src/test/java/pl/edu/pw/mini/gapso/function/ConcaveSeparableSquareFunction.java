package pl.edu.pw.mini.gapso.function;

import pl.edu.pw.mini.gapso.bounds.Bounds;

public class ConcaveSeparableSquareFunction extends FunctionWhiteBox {
    @Override
    public double computeValue(double[] x) {
        return -2 * x[0] * x[0] + 3 * x[0] - 4 * x[1] * x[1] - 3 * x[1] + 1;
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
        return new double[]{Double.NaN, Double.NaN};
    }
}
