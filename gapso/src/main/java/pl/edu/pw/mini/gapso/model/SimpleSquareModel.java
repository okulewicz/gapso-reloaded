package pl.edu.pw.mini.gapso.model;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import pl.edu.pw.mini.gapso.bounds.Bounds;
import pl.edu.pw.mini.gapso.sample.Sample;

import java.util.List;

public class SimpleSquareModel extends Model {

    @Override
    protected double[] computeLinearModelOptimum(Bounds bounds, int dim, OLSMultipleLinearRegression olslm) {
        double[] boundedPeak;
        boundedPeak = new double[dim];
        double[] ba = olslm.estimateRegressionParameters();
        for (int i = 0; i < dim; i++) {
            if (ba[i + dim + 1] > 0) {
                boundedPeak[i] += getBoundedCoordinate(bounds, -ba[i + 1] / ba[i + dim + 1] / 2.0, i);
            } else if (ba[i + dim + 1] == 0) {
                boundedPeak[i] += getBoundedCoordinate(bounds, ba[i + 1] > 0 ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY, i);
            } else if (linearCutValue(ba[i + dim + 1], ba[i + 1], bounds.getLower()[i])
                    < linearCutValue(ba[i + dim + 1], ba[i + 1], bounds.getUpper()[i])) {
                boundedPeak[i] += bounds.getLower()[i];
            } else {
                boundedPeak[i] += bounds.getUpper()[i];
            }
        }
        return boundedPeak;
    }

    private double linearCutValue(double a, double b, double x) {
        return a * x * x + b * x;
    }

    private double getBoundedCoordinate(Bounds bounds, double value, int dimension) {
        if (value < bounds.getLower()[dimension])
            return bounds.getLower()[dimension];
        return Math.min(value, bounds.getUpper()[dimension]);
    }

    @Override
    protected OLSMultipleLinearRegression putDataIntoModel(List<Sample> samples, int dim) {
        OLSMultipleLinearRegression olslm = new OLSMultipleLinearRegression();
        int samplesCount = samples.size();
        double[] y = new double[samplesCount];
        double[][] x = new double[samplesCount][];

        for (int i = 0; i < samplesCount; ++i) {
            y[i] = samples.get(i).getY();
            x[i] = new double[2 * dim];
            for (int j = 0; j < dim; ++j) {
                double xj = samples.get(i).getX()[j];
                x[i][j] = xj;
                x[i][j + dim] = xj * xj;
            }
        }
        olslm.newSampleData(y, x);
        return olslm;
    }

    @Override
    protected int getMinSamplesCount(int dim) {
        return 2 * dim + 1;
    }
}
