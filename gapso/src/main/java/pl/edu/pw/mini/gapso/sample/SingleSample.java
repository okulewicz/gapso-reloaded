package pl.edu.pw.mini.gapso.sample;

import java.util.Arrays;

public class SingleSample extends Sample {

    private final double[] _x;
    private final double _y;

    public SingleSample(double[] x, double y) {
        this._x = Arrays.copyOf(x, x.length);
        this._y = y;
    }

    @Override
    public double[] getX() {
        return _x;
    }

    @Override
    public double getY() {
        return _y;
    }
}
