package pl.edu.pw.mini.gapso.initializer;

import org.junit.Assert;
import org.junit.Test;
import pl.edu.pw.mini.gapso.bounds.Bounds;
import pl.edu.pw.mini.gapso.configuration.BoundsManagerConfiguration;
import pl.edu.pw.mini.gapso.function.Function;
import pl.edu.pw.mini.gapso.function.FunctionWhiteBox;
import pl.edu.pw.mini.gapso.function.RastriginFunction;
import pl.edu.pw.mini.gapso.optimizer.SamplingOptimizer;
import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.sample.Sampler;
import pl.edu.pw.mini.gapso.sample.SingleSample;
import pl.edu.pw.mini.gapso.sample.UpdatableSample;

import java.util.ArrayList;
import java.util.List;

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

    public class RandomOptimizer extends SamplingOptimizer {
        List<Sampler> samplerList = new ArrayList<>();

        @Override
        public void registerSampler(Sampler sampler) {
            samplerList.add(sampler);
        }

        @Override
        public Sample optimize(Function function) {
            function = createSamplingWrapper(function, samplerList);
            Initializer randomInitialize = new RandomInitializer();
            double[] someSampleLocation = randomInitialize.getNextSample(function.getBounds());
            Sample someSample = new SingleSample(someSampleLocation, function.getValue(someSampleLocation));
            UpdatableSample bestSample = new UpdatableSample(someSample);
            for (int i = 0; i < 120 * function.getDimension() * function.getDimension(); ++i) {
                someSampleLocation = randomInitialize.getNextSample(function.getBounds());
                someSample = new SingleSample(someSampleLocation, function.getValue(someSampleLocation));
                if (bestSample.getY() > someSample.getY()) {
                    bestSample.updateSample(someSample);
                }
            }
            return bestSample;
        }
    }
}