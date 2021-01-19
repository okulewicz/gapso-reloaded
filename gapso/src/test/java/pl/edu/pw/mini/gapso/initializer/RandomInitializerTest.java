package pl.edu.pw.mini.gapso.initializer;

import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.junit.Assert;
import org.junit.Test;
import pl.edu.pw.mini.gapso.bounds.Bounds;
import pl.edu.pw.mini.gapso.bounds.SimpleBounds;

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
        Bounds degenerateBounds = new SimpleBounds(new double[]{-1.0, -1.5}, new double[]{2.0, -1.5});
        Assert.assertEquals(degenerateBounds.getUpper()[1], degenerateBounds.getUpper()[1], 0.0);
        Assert.assertEquals(degenerateBounds.getLower()[1], initializer.getNextSample(degenerateBounds)[1], 0.0);

        try {
            Bounds badBounds = new SimpleBounds(new double[]{-1.0, -1.4}, new double[]{2.0, -1.5});
            Assert.fail("Should not create bounds");
            initializer.getNextSample(badBounds);
            Assert.fail("Should not generate sample from bad bounds");
        } catch (NumberIsTooLargeException ex) {
            ex.getMessage();
        }
    }
}