package pl.edu.pw.mini.gapso.initializer;

import org.apache.commons.math3.distribution.UniformRealDistribution;
import pl.edu.pw.mini.gapso.bounds.Bounds;
import pl.edu.pw.mini.gapso.optimizer.SamplingOptimizer;
import pl.edu.pw.mini.gapso.utils.Generator;

public class RandomInitializer extends Initializer {

    public static final String NAME = "Random";

    @Override
    public double[] getNextSample(Bounds bounds) {
        final int dim = bounds.getLower().length;
        double[] sample = new double[dim];
        for (int dimIdx = 0; dimIdx < dim; ++dimIdx) {
            final double lower = bounds.getLower()[dimIdx];
            final double upper = bounds.getUpper()[dimIdx];
            if (lower != upper) {
                UniformRealDistribution uniformRealDistribution =
                        new UniformRealDistribution(Generator.RANDOM, lower, upper);
                sample[dimIdx] = uniformRealDistribution.sample();
            } else {
                sample[dimIdx] = lower;
            }
        }
        return sample;
    }

    @Override
    public boolean canSample() {
        return true;
    }

    @Override
    public void registerObjectsWithOptimizer(SamplingOptimizer optimizer) {
        //NOTHING TO DO HERE
    }

    @Override
    public void resetInitializer(boolean hardReset) {
        //NOTHING TO DO HERE
    }
}
