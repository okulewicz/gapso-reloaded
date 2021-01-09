package pl.edu.pw.mini.gapso.model;

import org.apache.commons.math3.linear.SingularMatrixException;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import pl.edu.pw.mini.gapso.bounds.Bounds;
import pl.edu.pw.mini.gapso.sample.Sample;

import java.util.List;

public abstract class Model {
    public double[] getOptimumLocation(List<Sample> samples, Bounds bounds) {
        if (samples == null || samples.isEmpty())
            return null;
        if (samples.size() < getMinSamplesCount(samples.get(0).getX().length))
            return null;
        int dim = samples.get(0).getX().length;
        OLSMultipleLinearRegression olslm = putDataIntoModel(samples, dim);

        try {
            return computeLinearModelOptimum(olslm, bounds, dim);
        } catch (SingularMatrixException ex) {
            return null;
        }
    }

    protected abstract double[] computeLinearModelOptimum(OLSMultipleLinearRegression olslm, Bounds bounds, int dim);

    protected abstract OLSMultipleLinearRegression putDataIntoModel(List<Sample> samples, int dim);

    protected abstract int getMinSamplesCount(int dim);
}
