package pl.edu.pw.mini.gapso.model;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import pl.edu.pw.mini.gapso.bounds.Bounds;
import pl.edu.pw.mini.gapso.sample.Sample;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FullSquareModel extends Model {
    public static final String NAME = "FullSquare";

    @Override
    protected double[] computeLinearModelOptimum(OLSMultipleLinearRegression olslm, Bounds bounds, int dim) {
        double[] ba = olslm.estimateRegressionParameters();
        Map<String, Double> parameters = decodeParametersFromFactors(dim, ba);
        OLSMultipleLinearRegression derivativesOlslm = createDerivativesEquation(dim, parameters);
        double[] optimum = derivativesOlslm.estimateRegressionParameters();
        if (!bounds.contain(optimum)) {
            final double[] lower = bounds.getLower();
            final double[] upper = bounds.getUpper();
            for (int i = 0; i < optimum.length; ++i) {
                optimum[i] = Math.max(optimum[i], lower[i]);
                optimum[i] = Math.min(optimum[i], upper[i]);
            }
        }
        return optimum;
    }

    private OLSMultipleLinearRegression createDerivativesEquation(int dim, Map<String, Double> parameters) {
        double[] y = new double[dim + 1];
        double[][] x = new double[dim + 1][];
        for (int dervIdx = 0; dervIdx < dim; ++dervIdx) {
            x[dervIdx] = new double[dim];
            y[dervIdx] = -parameters.get("x" + dervIdx);
            for (int dimIdx = 0; dimIdx < dim; ++dimIdx) {
                String key;
                if (dimIdx > dervIdx) {
                    key = "x" + dervIdx + "x" + dimIdx;
                } else {
                    key = "x" + dimIdx + "x" + dervIdx;
                }
                if (dimIdx != dervIdx) {
                    x[dervIdx][dimIdx] = parameters.get(key);
                } else {
                    x[dervIdx][dimIdx] = 2 * parameters.get(key);
                }
            }
        }
        x[dim] = new double[dim];
        y[dim] = 0.0;
        OLSMultipleLinearRegression derivativesOlslm = new OLSMultipleLinearRegression();
        derivativesOlslm.setNoIntercept(true);
        derivativesOlslm.newSampleData(y, x);
        return derivativesOlslm;
    }

    private Map<String, Double> decodeParametersFromFactors(int dim, double[] ba) {
        Map<String, Double> parameters = new HashMap<>();
        int offset = dim;
        for (int j = 0; j < dim; ++j) {
            parameters.put("x" + j, ba[j + 1]);
            parameters.put("x" + j + "x" + j, ba[j + dim + 1]);
            offset += dim - j;
            for (int k = 0; k < dim - j - 1; ++k) {
                parameters.put("x" + j + "x" + (j + k + 1), ba[k + offset + 1]);
            }
        }
        return parameters;
    }

    @Override
    protected OLSMultipleLinearRegression putDataIntoModel(List<Sample> samples, int dim) {
        OLSMultipleLinearRegression olslm = new OLSMultipleLinearRegression();
        int samplesCount = samples.size();
        double[] y = new double[samplesCount];
        double[][] x = new double[samplesCount][];

        for (int i = 0; i < samplesCount; ++i) {
            y[i] = samples.get(i).getY();
            x[i] = new double[getMinSamplesCount(dim) - 1];
            int offset = dim;
            for (int j = 0; j < dim; ++j) {
                double xj = samples.get(i).getX()[j];
                x[i][j] = xj;
                x[i][j + dim] = xj * xj;
                offset += dim - j;
                for (int k = 0; k < dim - j - 1; ++k) {
                    double xk = samples.get(i).getX()[j + k + 1];
                    x[i][k + offset] = xj * xk;
                }
            }
        }
        olslm.newSampleData(y, x);
        return olslm;
    }

    @Override
    public int getMinSamplesCount(int dim) {
        return ((dim + 1) * dim) / 2 + dim + 1;
    }
}
