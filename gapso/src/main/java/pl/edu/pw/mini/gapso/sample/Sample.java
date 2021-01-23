package pl.edu.pw.mini.gapso.sample;

public abstract class Sample {
    public abstract double[] getX();

    public abstract double getY();

    public double getDistance(Sample otherSample) {
        final double[] otherX = otherSample.getX();
        return getDistance(otherX);
    }

    public double getDistance(double[] otherX) {
        final double[] thisX = this.getX();
        final int thisDim = thisX.length;
        final int otherDim = otherX.length;
        assert thisDim == otherDim;
        double distance = 0.0;
        for (int i = 0; i < thisDim; ++i) {
            final double v = otherX[i] - thisX[i];
            distance += v * v;
        }
        return Math.sqrt(distance);
    }

    public double getDistanceInDimension(double[] refPoint, int dim) {
        double distance = 0.0;
        double[] samplePoint = this.getX();
        for (int i = 0; i < refPoint.length && i < samplePoint.length; ++i) {
            if (i != dim) {
                distance += (refPoint[i] - samplePoint[i]) * (refPoint[i] - samplePoint[i]);
            }
        }
        return Math.sqrt(distance);
    }
}
