package pl.edu.pw.mini.gapso.initializer;

import org.junit.Assert;
import org.junit.Test;
import pl.edu.pw.mini.gapso.bounds.Bounds;
import pl.edu.pw.mini.gapso.bounds.SimpleBounds;
import pl.edu.pw.mini.gapso.function.ChaoticFunction;
import pl.edu.pw.mini.gapso.function.RastriginFunction;
import pl.edu.pw.mini.gapso.optimizer.RandomOptimizer;
import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.sample.SingleSample;

import java.util.ArrayList;
import java.util.List;

public class GlobalModelInitializerTest {

    @Test
    public void assessSamplingAbility() {
        GlobalModelInitializer modelInitializer = new GlobalModelInitializer();
        final int dimension = 10;
        RastriginFunction rastriginFunction = new RastriginFunction(dimension);
        RandomOptimizer randomOptimizer = new RandomOptimizer();
        Assert.assertFalse(modelInitializer.canSample());
        modelInitializer.registerObjectsWithOptimizer(randomOptimizer);
        randomOptimizer.optimize(rastriginFunction);
        Assert.assertFalse(modelInitializer.canSample());
        modelInitializer.resetInitializer(false);
        Assert.assertTrue(modelInitializer.canSample());
        for (int testNo = 0; testNo < 10; ++testNo) {
            double[] cum = new double[dimension];
            int samplesCount = 30;
            List<Sample> samplesList = new ArrayList<>();
            for (int i = 0; i < samplesCount; ++i) {
                double[] x = modelInitializer.getNextSample(rastriginFunction.getBounds());
                double y = rastriginFunction.getValue(x);
                Sample sample = new SingleSample(x, y);
                samplesList.add(sample);
                for (int j = 0; j < dimension; ++j) {
                    cum[j] += x[j];
                }
            }
            for (int j = 0; j < dimension; ++j) {
                cum[j] /= samplesCount;
            }
            Assert.assertTrue(getDistance(cum, rastriginFunction.getOptimumLocation()) < 1e-1);
            Assert.assertTrue(getMaxDistance(cum, rastriginFunction.getOptimumLocation()) < 1e-1);
            Assert.assertTrue(getMinDistance(cum, rastriginFunction.getOptimumLocation()) < 1e-2);
            Bounds bounds = SimpleBounds.createBoundsFromSamples(samplesList);
            Assert.assertTrue("Failed at " + testNo, bounds.strictlyContain(rastriginFunction.getOptimumLocation()));
        }
        modelInitializer.resetInitializer(true);
        modelInitializer.registerObjectsWithOptimizer(randomOptimizer);
        Assert.assertFalse(modelInitializer.canSample());
        ChaoticFunction chaoticFunction = new ChaoticFunction(dimension);
        randomOptimizer.optimize(chaoticFunction);
        Assert.assertFalse(modelInitializer.canSample());
        modelInitializer.resetInitializer(false);
        Assert.assertFalse(modelInitializer.canSample());
    }

    public double getMaxDistance(double[] x1, double[] x2) {
        double dist = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < x1.length; ++i) {
            dist = Math.max(Math.abs(x1[i] - x2[i]), dist);
        }
        return dist;
    }

    public double getMinDistance(double[] x1, double[] x2) {
        double dist = Double.POSITIVE_INFINITY;
        for (int i = 0; i < x1.length; ++i) {
            dist = Math.min(Math.abs(x1[i] - x2[i]), dist);
        }
        return dist;
    }

    public double getDistance(double[] x1, double[] x2) {
        double dist = 0.0;
        for (int i = 0; i < x1.length; ++i) {
            dist += (x1[i] - x2[i]) * (x1[i] - x2[i]);
        }
        return Math.sqrt(dist);
    }
}