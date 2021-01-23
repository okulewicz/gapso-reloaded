package pl.edu.pw.mini.gapso.bounds;

public abstract class Bounds {
    public abstract double[] getLower();

    public abstract double[] getUpper();

    public boolean contain(double[] sample) {
        final double[] lower = getLower();
        final double[] upper = getUpper();
        for (int i = 0; i < sample.length; ++i) {
            if (sample[i] < lower[i]) {
                return false;
            }
            if (sample[i] > upper[i]) {
                return false;
            }
        }
        return true;
    }

    public boolean strictlyContain(double[] sample) {
        final double[] lower = getLower();
        final double[] upper = getUpper();
        for (int i = 0; i < sample.length; ++i) {
            if (sample[i] <= lower[i]) {
                return false;
            }
            if (sample[i] >= upper[i]) {
                return false;
            }
        }
        return true;
    }
}
