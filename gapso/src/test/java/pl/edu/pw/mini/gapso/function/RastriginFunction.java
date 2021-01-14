package pl.edu.pw.mini.gapso.function;

import pl.edu.pw.mini.gapso.bounds.Bounds;
import pl.edu.pw.mini.gapso.bounds.SimpleBounds;
import pl.edu.pw.mini.gapso.utils.Generator;

public class RastriginFunction extends FunctionWhiteBox {

    private final int _dimension;
    private final Bounds _bounds;
    private final double[] _optimum;
    private final double[] _scale;

    public RastriginFunction(int dimension) {
        _dimension = dimension;
        double[] lower = new double[_dimension];
        double[] upper = new double[_dimension];
        _optimum = new double[dimension];
        _scale = new double[dimension];
        for (int i = 0; i < _dimension; ++i) {
            lower[i] = -5.12;
            upper[i] = 5.12;
            double spread = upper[i] - lower[i];
            double mean = (upper[i] + lower[i]) / 2.0;
            _optimum[i] = Generator.RANDOM.nextDouble() * spread / 2.0
                    + mean - spread / 4.0;
            _scale[i] = Generator.RANDOM.nextDouble() * 0.5 + 0.5;
        }
        _bounds = new SimpleBounds(lower, upper);

    }

    @Override
    public double[] getOptimumLocation() {
        return _optimum;
    }

    @Override
    protected double computeValue(double[] x) {
        assert x.length == _dimension;
        double result = 0.0;
        for (int i = 0; i < _dimension; ++i) {
            double z = _scale[i] * (x[i] - _optimum[i]);
            result += z * z - Math.cos(2 * Math.PI * z) + 1;
        }
        return result;
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
