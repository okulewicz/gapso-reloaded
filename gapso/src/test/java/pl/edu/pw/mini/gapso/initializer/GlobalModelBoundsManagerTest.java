package pl.edu.pw.mini.gapso.initializer;

import org.junit.Assert;
import org.junit.Test;
import pl.edu.pw.mini.gapso.bounds.Bounds;
import pl.edu.pw.mini.gapso.configuration.BoundsManagerConfiguration;
import pl.edu.pw.mini.gapso.function.FunctionWhiteBox;
import pl.edu.pw.mini.gapso.function.RastriginFunction;
import pl.edu.pw.mini.gapso.optimizer.RandomOptimizer;
import pl.edu.pw.mini.gapso.optimizer.SamplingOptimizer;

public class GlobalModelBoundsManagerTest {
    @Test
    public void getBounds() {
        for (int dimension = 1; dimension < 21; ++dimension) {
            FunctionWhiteBox rastrigin = new RastriginFunction(dimension);
            double[] optimum = rastrigin.getOptimumLocation();
            Assert.assertEquals(0.0, rastrigin.getValue(optimum), 1e-8);

            Initializer randomInitialize = new RandomInitializer();
            double[] someSample = randomInitialize.getNextSample(rastrigin.getBounds());
            Assert.assertNotEquals(0.0, rastrigin.getValue(someSample), 1e-8);

            SamplingOptimizer samplingOptimizer = new RandomOptimizer();
            BoundsManagerConfiguration boundsManagerConfiguration =
                    new BoundsManagerConfiguration(GlobalModelBoundsManager.NAME, null);
            GlobalModelBoundsManager boundsManager = new GlobalModelBoundsManager(boundsManagerConfiguration);
            boundsManager.setInitialBounds(rastrigin.getBounds());
            boundsManager.registerObjectsWithOptimizer(samplingOptimizer);

            samplingOptimizer.optimize(rastrigin);
            Bounds bounds = boundsManager.getBounds();
            Assert.assertTrue("Failed for dim = " + dimension, bounds.contain(optimum));
            Assert.assertTrue("Model not utilized for dim = " + dimension, boundsManager.isModelUtilized());
        }
    }

}