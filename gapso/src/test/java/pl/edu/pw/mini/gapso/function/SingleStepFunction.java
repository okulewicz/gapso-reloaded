package pl.edu.pw.mini.gapso.function;

import pl.edu.pw.mini.gapso.bounds.Bounds;
import pl.edu.pw.mini.gapso.bounds.SimpleBounds;

public class SingleStepFunction extends Function {

    @Override
    protected double computeValue(double[] x) {
        if (x[1] > 1) {
            return 1.0;
        } else {
            return 0.0;
        }
    }

    @Override
    public boolean isTargetReached() {
        return false;
    }

    @Override
    public int getDimension() {
        return 2;
    }

    @Override
    public Bounds getBounds() {
        return new SimpleBounds(new double[]{-3.0, -3.0},
                new double[]{3.0, 3.0});
    }
}
