package pl.edu.pw.mini.gapso.function;

import pl.edu.pw.mini.gapso.bounds.Bounds;
import pl.edu.pw.mini.gapso.bounds.SimpleBounds;

public class ConvexSquareFunction extends FunctionWhiteBox {
    @Override
    public double computeValue(double[] x) {
        checkIfOptimumVisited(x);
        return
                +1 * x[0] * x[0]
                        + 2 * x[0] * x[1]
                        + 5 * x[1] * x[1]
                        - 2 * x[0]
                        - 4 * x[1]
                        + 1;
    }

    @Override
    public int getDimension() {
        return 2;
    }

    @Override
    public Bounds getBounds() {
        return new SimpleBounds(
                new double[]{-2.0, -2.0},
                new double[]{2.0, 2.0}
        );
    }

    @Override
    public double[] getOptimumLocation() {
        return new double[]{0.75, 0.25};
    }
}
