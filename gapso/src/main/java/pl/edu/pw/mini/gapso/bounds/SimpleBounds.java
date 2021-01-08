package pl.edu.pw.mini.gapso.bounds;

import pl.edu.pw.mini.gapso.sample.Sample;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;

public class SimpleBounds extends Bounds {

    private final double[] lowerBounds;
    private final double[] upperBounds;

    public SimpleBounds(double[] lowerBounds, double[] upperBounds) {
        this.lowerBounds = Arrays.copyOf(lowerBounds, lowerBounds.length);
        this.upperBounds = Arrays.copyOf(upperBounds, upperBounds.length);
    }

    public static Bounds createBoundsFromSamples(List<Sample> samples) {
        if (samples == null || samples.isEmpty())
            return null;
        int dim = samples.get(0).getX().length;
        double[] lower = new double[dim];
        double[] upper = new double[dim];
        Arrays.fill(lower, Double.POSITIVE_INFINITY);
        Arrays.fill(upper, Double.NEGATIVE_INFINITY);
        for (int dimIdx = 0; dimIdx < dim; ++dimIdx) {
            int finalDimIdx = dimIdx;
            OptionalDouble optLower = samples.stream().mapToDouble(s -> s.getX()[finalDimIdx]).min();
            OptionalDouble optUpper = samples.stream().mapToDouble(s -> s.getX()[finalDimIdx]).max();
            if (optLower.isPresent() && optUpper.isPresent()) {
                lower[dimIdx] = optLower.getAsDouble();
                upper[dimIdx] = optUpper.getAsDouble();
            } else {
                return null;
            }
        }
        return new SimpleBounds(lower, upper);
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
