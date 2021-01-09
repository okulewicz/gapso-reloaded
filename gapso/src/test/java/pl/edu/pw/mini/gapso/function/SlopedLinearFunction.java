package pl.edu.pw.mini.gapso.function;

public class SlopedLinearFunction extends FunctionWhiteBox {
    @Override
    public double getValue(double[] x) {
        return 2 * x[0] - 3 * x[1] + 1;
    }

    @Override
    public double[] getOptimumLocation() {
        return new double[]{Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY};
    }
}
