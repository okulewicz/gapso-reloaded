package pl.edu.pw.mini.gapso.bounds;

import org.apache.commons.math3.exception.NumberIsTooLargeException;

import java.util.Arrays;

public class BoundsWithStatistics extends Bounds {
    private final double[] lowerBounds;
    private final double[] upperBounds;

    public BoundsWithStatistics(Bounds bounds) {
        this.lowerBounds = Arrays.copyOf(bounds.getLower(), bounds.getLower().length);
        this.upperBounds = Arrays.copyOf(bounds.getUpper(), bounds.getUpper().length);
        for (int i = 0; i < lowerBounds.length; ++i) {
            if (this.lowerBounds[i] > this.upperBounds[i]) {
                throw new NumberIsTooLargeException(lowerBounds[i], upperBounds[i], true);
            }
        }
    }

    @Override
    public double[] getLower() {
        return lowerBounds;
    }

    @Override
    public double[] getUpper() {
        return upperBounds;
    }

}
