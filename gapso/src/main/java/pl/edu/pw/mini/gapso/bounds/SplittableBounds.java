package pl.edu.pw.mini.gapso.bounds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SplittableBounds extends Bounds {
    private static final double FACTOR = 1.0;
    private final Bounds _bounds;

    public SplittableBounds(Bounds bounds) {
        _bounds = bounds;
    }

    @Override
    public double[] getLower() {
        return _bounds.getLower();
    }

    @Override
    public double[] getUpper() {
        return _bounds.getUpper();
    }

    public List<SplittableBounds> Split(double[] x) {
        double[] lower = _bounds.getLower();
        double[] upper = _bounds.getUpper();
        int widestDim = getWidestDim(lower, upper);
        double split1 = (x[widestDim] + lower[widestDim]) / 2.0;
        double split2 = (x[widestDim] + upper[widestDim]) / 2.0;
        double[] lower1 = Arrays.copyOf(lower, lower.length);
        double[] lower2 = Arrays.copyOf(lower, lower.length);
        double[] lower3 = Arrays.copyOf(lower, lower.length);
        double[] upper1 = Arrays.copyOf(upper, upper.length);
        double[] upper2 = Arrays.copyOf(upper, upper.length);
        double[] upper3 = Arrays.copyOf(upper, upper.length);
        lower2[widestDim] = upper1[widestDim] = split1;
        lower3[widestDim] = upper2[widestDim] = split2;
        List<SplittableBounds> splittableBoundsList = new ArrayList<>();
        splittableBoundsList.add(new SplittableBounds(
                new SimpleBounds(
                        lower1, upper1
                )
        ));
        splittableBoundsList.add(new SplittableBounds(
                new SimpleBounds(
                        lower2, upper2
                )
        ));
        splittableBoundsList.add(new SplittableBounds(
                new SimpleBounds(
                        lower3, upper3
                )
        ));
        return splittableBoundsList;
    }

    private int getWidestDim(double[] lower, double[] upper) {
        int widestDim = -1;
        double widestSpread = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < lower.length; ++i) {
            if (upper[i] - lower[i] > widestSpread) {
                widestSpread = upper[i] - lower[i];
                widestDim = i;
            }
        }
        return widestDim;
    }

    public boolean areBoundsTooThinToSplit() {
        double[] lower = _bounds.getLower();
        double[] upper = _bounds.getUpper();
        int widestDim = getWidestDim(lower, upper);
        return (upper[widestDim] - lower[widestDim] < FACTOR);
    }
}
