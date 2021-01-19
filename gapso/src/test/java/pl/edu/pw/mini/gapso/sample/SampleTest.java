package pl.edu.pw.mini.gapso.sample;

import org.junit.Assert;
import org.junit.Test;

public class SampleTest {

    @Test
    public void getDistance() {
        Sample s1 = new SingleSample(new double[]{-2.0, -2.0}, 0.0);
        Sample s2 = new SingleSample(new double[]{-2.0, 2.0}, 0.0);
        Sample s3 = new SingleSample(new double[]{2.0, 2.0}, 0.0);
        Assert.assertEquals(0.0, s1.getDistance(s1), 0.0);
        Assert.assertEquals(4.0, s1.getDistance(s2), 0.0);
        Assert.assertEquals(4.0, s2.getDistance(s1), 0.0);
        Assert.assertEquals(4.0, s2.getDistance(s3), 0.0);
        Assert.assertEquals(4.0, s3.getDistance(s2), 0.0);
        Assert.assertEquals(4.0 * Math.sqrt(2), s1.getDistance(s3), 1e-8);
        Assert.assertEquals(4.0 * Math.sqrt(2), s3.getDistance(s1), 1e-8);
    }
}