package pl.edu.pw.mini.gapso.sample;

public abstract class Sample {
    public abstract double[] getX();

    public abstract double getY();

    public double getDistance(Sample otherSample) {
        final double[] thisX = this.getX();
        final int thisDim = thisX.length;
        final double[] otherX = otherSample.getX();
        final int otherDim = otherX.length;
        assert thisDim == otherDim;
        double distance = 0.0;
        for (int i = 0; i < thisDim; ++i) {
            final double v = otherX[i] - thisX[i];
            distance += v * v;
        }
        return Math.sqrt(distance);
    }
}
