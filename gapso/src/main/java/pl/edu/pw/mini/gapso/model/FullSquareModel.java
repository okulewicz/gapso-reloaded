package pl.edu.pw.mini.gapso.model;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import pl.edu.pw.mini.gapso.bounds.Bounds;
import pl.edu.pw.mini.gapso.sample.Sample;

import java.util.List;

public class FullSquareModel extends Model {
    @Override
    protected double[] computeLinearModelOptimum(OLSMultipleLinearRegression olslm, Bounds bounds, int dim) {
        return new double[0];
    }

    @Override
    protected OLSMultipleLinearRegression putDataIntoModel(List<Sample> samples, int dim) {
        return null;
    }

    @Override
    protected int getMinSamplesCount(int dim) {
        return ((dim + 1) * dim) / 2 + dim + 1;
    }
}
