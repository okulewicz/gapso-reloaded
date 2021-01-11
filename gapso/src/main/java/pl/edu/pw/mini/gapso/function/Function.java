package pl.edu.pw.mini.gapso.function;

import pl.edu.pw.mini.gapso.bounds.Bounds;

public abstract class Function {
    private int evaluationsCount;

    public Function() {
        evaluationsCount = 0;
    }

    public double getValue(double[] x) {
        ++evaluationsCount;
        return computeValue(x);
    }

    protected abstract double computeValue(double[] x);

    public abstract boolean isTargetReached();

    public abstract int getDimension();

    public abstract Bounds getBounds();

    public int getEvaluationsCount() {
        return evaluationsCount;
    }

}
