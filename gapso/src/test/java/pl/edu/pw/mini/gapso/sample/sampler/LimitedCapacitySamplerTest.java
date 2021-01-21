package pl.edu.pw.mini.gapso.sample.sampler;

import org.junit.Assert;
import org.junit.Test;
import pl.edu.pw.mini.gapso.sample.Sample;

public class LimitedCapacitySamplerTest {

    @Test
    public void getSamplesCount() {
        final int expectedMaxSize = 20;
        LimitedCapacitySampler sampler = new LimitedCapacitySampler(expectedMaxSize);
        for (int i = 0; i < 100; ++i) {
            Sample sample = new Sample() {
                @Override
                public double[] getX() {
                    return new double[0];
                }

                @Override
                public double getY() {
                    return 0;
                }
            };
            sampler.tryStoreSample(sample);
            if (i > expectedMaxSize - 2) {
                Assert.assertEquals("Failed for " + i, expectedMaxSize, sampler.getSamplesCount());
            } else {
                Assert.assertTrue("Failed for " + i, expectedMaxSize > sampler.getSamplesCount());
            }
            Assert.assertEquals(sampler.getSamplesCount(), sampler.getSamples().size());
        }
    }
}