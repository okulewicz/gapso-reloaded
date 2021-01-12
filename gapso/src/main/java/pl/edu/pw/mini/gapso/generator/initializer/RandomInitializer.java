package pl.edu.pw.mini.gapso.generator.initializer;

import org.apache.commons.math3.distribution.UniformRealDistribution;
import pl.edu.pw.mini.gapso.bounds.Bounds;
import pl.edu.pw.mini.gapso.generator.Generator;

public class RandomInitializer extends Initializer {

    public static final String NAME = "Random";

    @Override
    public double[] getNextSample(Bounds bounds) {
        final int dim = bounds.getLower().length;
        double[] sample = new double[dim];
        for (int dimIdx = 0; dimIdx < dim; ++dimIdx) {
            final double lower = bounds.getLower()[dimIdx];
            final double upper = bounds.getUpper()[dimIdx];
            UniformRealDistribution uniformRealDistribution =
                    new UniformRealDistribution(Generator.RANDOM, lower, upper);
            sample[dimIdx] = uniformRealDistribution.sample();
        }
        return sample;
    }

    @Override
    protected boolean canSample() {
        return true;
    }
}
