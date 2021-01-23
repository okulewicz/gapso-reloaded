package pl.edu.pw.mini.gapso.sample.sampler;

import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.utils.Generator;

import java.util.ArrayList;
import java.util.List;

public class LimitedCapacitySampler extends Sampler {
    private final int maxSize;
    private final List<Sample> samples = new ArrayList<>();

    public LimitedCapacitySampler(int maxSize) {
        super();
        this.maxSize = maxSize;
    }

    @Override
    public boolean tryStoreSample(Sample sample) {
        if (samplesCount < maxSize) {
            samplesCount++;
        } else {
            samples.remove(Generator.RANDOM.nextInt(samples.size()));
        }
        return samples.add(sample);
    }

    public List<Sample> getSamples() {
        return samples;
    }
}
