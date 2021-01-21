package pl.edu.pw.mini.gapso.sample.sampler;

import pl.edu.pw.mini.gapso.sample.Sample;

public abstract class Sampler {
    protected int samplesCount = 0;

    public abstract boolean tryStoreSample(Sample sample);

    public int getSamplesCount() {
        return samplesCount;
    }
}
