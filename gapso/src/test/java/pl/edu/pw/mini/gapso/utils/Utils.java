package pl.edu.pw.mini.gapso.utils;

import pl.edu.pw.mini.gapso.bounds.Bounds;

public class Utils {
    public static double getBoundedValue(Bounds bounds, int i, double[] expectedOptimumLocation) {
        double boundedValue = Math.min(Math.max(expectedOptimumLocation[i], bounds.getLower()[i]), bounds.getUpper()[i]);
        if (!Double.isNaN(boundedValue)) {
            return boundedValue;
        } else {
            return (bounds.getLower()[i] + bounds.getUpper()[i]) / 2.0;
        }
    }
}
