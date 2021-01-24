package pl.edu.pw.mini.gapso.sample.sampler;

import pl.edu.pw.mini.gapso.bounds.Bounds;
import pl.edu.pw.mini.gapso.bounds.SimpleBounds;
import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.utils.Generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AllSamplesSampler extends Sampler {
    List<Sample> samples = new ArrayList<>();
    private double[] lowerBounds;
    private double[] upperBounds;

    @Override
    public boolean tryStoreSample(Sample sample) {
        final double[] x = sample.getX();
        if (lowerBounds == null || upperBounds == null) {
            lowerBounds = Arrays.copyOf(x, x.length);
            upperBounds = Arrays.copyOf(x, x.length);
        } else {
            for (int d = 0; d < x.length; ++d) {
                lowerBounds[d] = Math.min(lowerBounds[d], x[d]);
                upperBounds[d] = Math.max(upperBounds[d], x[d]);
            }
        }
        samples.add(sample);
        samplesCount++;
        return true;
    }

    public List<Sample> getSamples(int samplesCount) {
        List<Integer> integers =
                Generator.getUniqueIntegerSequence(0, samples.size(), samplesCount);
        List<Sample> returnedSamples = new ArrayList<>();
        for (int i : integers) {
            returnedSamples.add(samples.get(i));
        }
        return returnedSamples;
    }

    public void clear() {
        samples.clear();
    }

    public Bounds getBounds() {
        return new SimpleBounds(lowerBounds, upperBounds);
    }
}
