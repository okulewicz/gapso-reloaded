package pl.edu.pw.mini.gapso.sample;

public class UpdatableSample extends Sample {
    private Sample _sample;

    public UpdatableSample(Sample sample) {
        _sample = sample;
    }

    @Override
    public double[] getX() {
        return _sample.getX();
    }

    @Override
    public double getY() {
        return _sample.getY();
    }

    public void updateSample(Sample sample) {
        _sample = sample;
    }
}
