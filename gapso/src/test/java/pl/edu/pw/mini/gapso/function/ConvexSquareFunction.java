package pl.edu.pw.mini.gapso.function;

public class ConvexSquareFunction extends FunctionWhiteBox {
    @Override
    public double getValue(double[] x) {
        return
                +1 * x[0] * x[0]
                        + 2 * x[0] * x[1]
                        + 5 * x[1] * x[1]
                        - 2 * x[0]
                        - 4 * x[1]
                        + 1;
    }

    @Override
    public double[] getOptimumLocation() {
        return new double[]{0.75, 0.25};
    }
}
