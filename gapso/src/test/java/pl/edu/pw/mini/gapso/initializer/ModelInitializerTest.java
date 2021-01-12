package pl.edu.pw.mini.gapso.initializer;

import org.junit.Assert;
import org.junit.Test;
import pl.edu.pw.mini.gapso.function.ConvexSquareFunction;
import pl.edu.pw.mini.gapso.function.Function;
import pl.edu.pw.mini.gapso.optimizer.SamplingOptimizer;
import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.sample.Sampler;

import java.util.ArrayList;
import java.util.List;

public class ModelInitializerTest {

    @Test
    public void canSample() {
        MySamplingOptimizer samplingOptimizer = new MySamplingOptimizer();

        RandomInitializer generator = new RandomInitializer();
        ModelInitializer initializer = new ModelInitializer();
        Assert.assertFalse(initializer.canSample());
        Assert.assertFalse(initializer.canSample());
        for (int i = 0; i < 2; ++i) {
            initializer.registerObjectsWithOptimizer(samplingOptimizer);
            Function function = samplingOptimizer.wrapFunction(new ConvexSquareFunction());
            //LINEAR MODEL
            function.getValue(generator.getNextSample(function.getBounds()));
            Assert.assertFalse(initializer.canSample());
            function.getValue(generator.getNextSample(function.getBounds()));
            Assert.assertFalse(initializer.canSample());
            function.getValue(generator.getNextSample(function.getBounds()));
            Assert.assertTrue(initializer.canSample());
            initializer.getNextSample(function.getBounds());
            Assert.assertFalse(initializer.canSample());
            //SQUARE MODEL
            function.getValue(generator.getNextSample(function.getBounds()));
            function.getValue(generator.getNextSample(function.getBounds()));
            Assert.assertTrue(initializer.canSample());
            initializer.getNextSample(function.getBounds());
            Assert.assertFalse(initializer.canSample());
            //FULL SQUARE MODEL
            function.getValue(generator.getNextSample(function.getBounds()));
            Assert.assertTrue(initializer.canSample());
            initializer.getNextSample(function.getBounds());
            Assert.assertFalse(initializer.canSample());
            initializer.resetInitializer();
            Assert.assertFalse(initializer.canSample());
        }
    }

    private static class MySamplingOptimizer extends SamplingOptimizer {
        public List<Sampler> samplerList = new ArrayList<>();

        public Function wrapFunction(Function function) {
            return createSamplingWrapper(function, samplerList);
        }

        @Override
        public Sample optimize(Function function) {
            return null;
        }

        @Override
        public void registerSampler(Sampler sampler) {
            samplerList.add(sampler);
        }

    }
}