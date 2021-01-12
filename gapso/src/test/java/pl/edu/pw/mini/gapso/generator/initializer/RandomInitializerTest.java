package pl.edu.pw.mini.gapso.generator.initializer;

import org.junit.Assert;
import org.junit.Test;
import pl.edu.pw.mini.gapso.bounds.Bounds;
import pl.edu.pw.mini.gapso.bounds.SimpleBounds;
import pl.edu.pw.mini.gapso.initializer.Initializer;
import pl.edu.pw.mini.gapso.initializer.RandomInitializer;

public class RandomInitializerTest {

    @Test
    public void getNextSample() {
        Initializer initializer = new RandomInitializer();
        Bounds bounds = new SimpleBounds(new double[]{-1.0, -1.5}, new double[]{2.0, -0.5});
        for (int i = 0; i < 100; ++i) {
            double[] sample = initializer.getNextSample(bounds);
            for (int j = 0; j < bounds.getLower().length; ++j) {
                Assert.assertTrue(sample[j] >= bounds.getLower()[j]);
                Assert.assertTrue(sample[j] <= bounds.getUpper()[j]);
            }
        }
    }
}