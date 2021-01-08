package pl.edu.pw.mini.gapso.bounds;

import org.junit.Assert;
import org.junit.Test;
import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.sample.SingleSample;

import java.util.ArrayList;
import java.util.List;

public class SimpleBoundsTest {

    @Test
    public void createBoundsFromSamples() {
        List<Sample> samples = new ArrayList<>();
        Bounds boundsFromSamples = SimpleBounds.createBoundsFromSamples(samples);
        Assert.assertNull(boundsFromSamples);
        samples.add(new SingleSample(new double[]{0.0, 1.0}, 0));
        samples.add(new SingleSample(new double[]{1.0, 0.0}, 0));

        boundsFromSamples = SimpleBounds.createBoundsFromSamples(samples);
        Assert.assertArrayEquals(new double[]{0.0, 0.0}, boundsFromSamples.getLower(), 0);
        Assert.assertArrayEquals(new double[]{1.0, 1.0}, boundsFromSamples.getUpper(), 0);
    }
}