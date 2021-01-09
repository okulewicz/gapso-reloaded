package pl.edu.pw.mini.gapso.function;

public class PartiallyFlatLinearFunction extends FunctionWhiteBox {
    @Override
    public double getValue(double[] x) {
        return -2 * x[0] + 1;
    }

    @Override
    public double[] getOptimumLocation() {
        return new double[]{Double.POSITIVE_INFINITY, Double.NaN};
    }
}
