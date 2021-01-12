package pl.edu.pw.mini.gapso.sample;

public abstract class Sampler {
    protected int samplesCount = 0;

    public abstract boolean tryStoreSample(Sample sample);

    public int getSamplesCount() {
        return samplesCount;
    }
}
