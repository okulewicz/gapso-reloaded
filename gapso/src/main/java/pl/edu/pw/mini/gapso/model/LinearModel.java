package pl.edu.pw.mini.gapso.model;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import pl.edu.pw.mini.gapso.bounds.Bounds;
import pl.edu.pw.mini.gapso.sample.Sample;

import java.util.List;

public class LinearModel extends Model {

    @Override
    protected double[] computeLinearModelOptimum(OLSMultipleLinearRegression olslm, Bounds bounds, int dim) {
        double[] modelOptimumLocation;
        modelOptimumLocation = new double[dim];
        double[] ba = olslm.estimateRegressionParameters();
        for (int i = 0; i < dim; i++) {
            if (ba[i + 1] > 1e-8) {
                modelOptimumLocation[i] = bounds.getLower()[i];
            } else if (ba[i + 1] < -1e-8) {
                modelOptimumLocation[i] = bounds.getUpper()[i];
            } else {
                modelOptimumLocation[i] = (bounds.getUpper()[i] + bounds.getLower()[i]) / 2.0;
            }
        }
        return modelOptimumLocation;
    }

    @Override
    protected OLSMultipleLinearRegression putDataIntoModel(List<Sample> samples, int dim) {
        OLSMultipleLinearRegression olslm = new OLSMultipleLinearRegression();
        int samplesCount = samples.size();
        double[] y = new double[samplesCount];
        double[][] x = new double[samplesCount][];

        for (int i = 0; i < samplesCount; ++i) {
            y[i] = samples.get(i).getY();
            x[i] = new double[dim];
            System.arraycopy(samples.get(i).getX(), 0, x[i], 0, dim);
        }
        olslm.newSampleData(y, x);
        return olslm;
    }

    @Override
    public int getMinSamplesCount(int dim) {
        return dim + 1;
    }
}
