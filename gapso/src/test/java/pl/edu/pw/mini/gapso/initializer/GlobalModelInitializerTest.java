package pl.edu.pw.mini.gapso.initializer;

import org.junit.Assert;
import org.junit.Test;
import pl.edu.pw.mini.gapso.function.ChaoticFunction;
import pl.edu.pw.mini.gapso.function.RastriginFunction;
import pl.edu.pw.mini.gapso.optimizer.RandomOptimizer;

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
        double[] cum = new double[dimension];
        for (int i = 0; i < 10; ++i) {
            double[] x = modelInitializer.getNextSample(rastriginFunction.getBounds());
            for (int j = 0; j < dimension; ++j) {
                cum[j] += x[j];
            }
            System.out.println(getDistance(x, rastriginFunction.getOptimumLocation()));
            System.out.println(getMaxDistance(x, rastriginFunction.getOptimumLocation()));
            System.out.println(getMinDistance(x, rastriginFunction.getOptimumLocation()));
        }
        for (int j = 0; j < dimension; ++j) {
            cum[j] /= 10;
        }
        System.out.println(getDistance(cum, rastriginFunction.getOptimumLocation()));
        System.out.println(getMaxDistance(cum, rastriginFunction.getOptimumLocation()));
        System.out.println(getMinDistance(cum, rastriginFunction.getOptimumLocation()));
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