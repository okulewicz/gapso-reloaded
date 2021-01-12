package pl.edu.pw.mini.gapso.model;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import pl.edu.pw.mini.gapso.bounds.Bounds;
import pl.edu.pw.mini.gapso.sample.Sample;

import java.util.List;

public class SimpleSquareModel extends Model {

    @Override
    protected double[] computeLinearModelOptimum(OLSMultipleLinearRegression olslm, Bounds bounds, int dim) {
        double[] boundedPeak;
        boundedPeak = new double[dim];
        double[] ba = olslm.estimateRegressionParameters();
        for (int i = 0; i < dim; i++) {
            //needs to be significantly positive
            final double ai = ba[i + dim + 1];
            final double bi = ba[i + 1];
            final double lower = bounds.getLower()[i];
            final double upper = bounds.getUpper()[i];
            if (ai > 1e-8) {
                boundedPeak[i] += getBoundedCoordinate(-bi / ai / 2.0, lower, upper);
            } else {
                if (linearCutValue(ai, bi, lower)
                        < linearCutValue(ai, bi, upper)) {
                    boundedPeak[i] += lower;
                } else {
                    boundedPeak[i] += upper;
                }
            }
        }
        return boundedPeak;
    }

    private double linearCutValue(double a, double b, double x) {
        return a * x * x + b * x;
    }

    private double getBoundedCoordinate(double value, double lowerBound, double upperBound) {
        return Math.min(
                Math.max(
                        value,
                        lowerBound),
                upperBound);
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
    public int getMinSamplesCount(int dim) {
        return 2 * dim + 1;
    }
}
