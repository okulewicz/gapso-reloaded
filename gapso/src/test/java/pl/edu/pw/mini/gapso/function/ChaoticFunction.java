package pl.edu.pw.mini.gapso.function;

import pl.edu.pw.mini.gapso.bounds.Bounds;
import pl.edu.pw.mini.gapso.bounds.SimpleBounds;
import pl.edu.pw.mini.gapso.utils.Generator;

public class ChaoticFunction extends Function {

    private final int _dimension;
    private final Bounds _bounds;
    private final double[] _optimum;
    private final double[] _scale;

    public ChaoticFunction(int dimension) {
        _dimension = dimension;
        double[] lower = new double[_dimension];
        double[] upper = new double[_dimension];
        _optimum = new double[dimension];
        _scale = new double[dimension];
        for (int i = 0; i < _dimension; ++i) {
            lower[i] = -5.12;
            upper[i] = 5.12;
        }
        _bounds = new SimpleBounds(lower, upper);
    }

    @Override
    protected double computeValue(double[] x) {
        return Generator.RANDOM.nextDouble() * 20.0 - 10.0;
    }

    @Override
    public boolean isTargetReached() {
        return false;
    }

    @Override
    public int getDimension() {
        return _dimension;
    }

    @Override
    public Bounds getBounds() {
        return _bounds;
    }
}
