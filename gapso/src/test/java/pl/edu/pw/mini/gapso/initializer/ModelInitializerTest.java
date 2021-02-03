package pl.edu.pw.mini.gapso.initializer;

import org.junit.Assert;
import org.junit.Test;
import pl.edu.pw.mini.gapso.function.ConvexSquareFunction;
import pl.edu.pw.mini.gapso.function.Function;
import pl.edu.pw.mini.gapso.optimizer.MySamplingOptimizer;

public class ModelInitializerTest {

    @Test
    public void canSample() {
        MySamplingOptimizer samplingOptimizer = new MySamplingOptimizer();

        RandomInitializer generator = new RandomInitializer();
        ModelInitializer initializer = new ModelInitializer();
        Assert.assertFalse(initializer.canSample());
        Assert.assertFalse(initializer.canSample());
        for (int i = 0; i < 2; ++i) {
            Assert.assertEquals(0, samplingOptimizer.samplerList.size());
            initializer.registerObjectsWithOptimizer(samplingOptimizer);
            Assert.assertEquals(1, samplingOptimizer.samplerList.size());
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
            //FULL SQUARE MODEL
            //TODO: this logic of test does not accommodate new way the model works - taking into account R squared
            function.getValue(generator.getNextSample(function.getBounds()));
            Assert.assertTrue(initializer.canSample());
            initializer.getNextSample(function.getBounds());
            initializer.resetInitializer(false);
            Assert.assertTrue(initializer.canSample());
            samplingOptimizer.samplerList.clear();
            initializer.resetInitializer(true);
            Assert.assertFalse(initializer.canSample());
        }
    }

}