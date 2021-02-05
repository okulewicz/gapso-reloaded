package pl.edu.pw.mini.gapso.optimizer.move;

import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import pl.edu.pw.mini.gapso.configuration.MoveConfiguration;
import pl.edu.pw.mini.gapso.optimizer.Particle;
import pl.edu.pw.mini.gapso.optimizer.SamplingOptimizer;
import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.utils.Generator;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CMAESLike extends Move {
    public static final String NAME = "CMA-ES-like";
    private boolean isFirstInIteration;
    private double[] oldMu;
    private double[] newMu;
    private MultivariateNormalDistribution mvnd;

    public CMAESLike(MoveConfiguration configuration) {
        super(configuration);
        resetState(0);
    }

    @Override
    public double[] getNext(Particle currentParticle, List<Particle> particleList) {
        final int dimension = currentParticle.getBest().getX().length;
        if (isFirstInIteration) {
            final List<Sample> samples = particleList.stream().map(Particle::getBest)
                    .sorted(Comparator.comparingDouble(Sample::getY)).collect(Collectors.toList());
            final int lambda = samples.size();
            if (oldMu == null) {
                computeOldMu(dimension, samples);
            } else {
                oldMu = newMu;
            }
            double[] w = computeWeights(lambda);
            newMu = computeMean(dimension, samples, lambda, w);
            RealMatrix covMatrix = computeCovarianceMatrix(dimension, samples, lambda, w);
            mvnd = new MultivariateNormalDistribution(Generator.RANDOM, newMu, covMatrix.getData());
            isFirstInIteration = false;
        }
        assert mvnd != null;
        return mvnd.sample();
    }

    public RealMatrix computeCovarianceMatrix(int dimension, List<Sample> samples, int lambda, double[] w) {
        RealMatrix covMatrix = MatrixUtils.createRealMatrix(dimension, dimension);
        double[][] y = new double[lambda][];
        for (int sIdx = 0; sIdx < lambda; ++sIdx) {
            y[sIdx] = new double[dimension];
            for (int dimIdx = 0; dimIdx < dimension; ++dimIdx) {
                y[sIdx][dimIdx] = samples.get(sIdx).getX()[dimIdx] - oldMu[dimIdx];
            }
            RealMatrix ymatrix = MatrixUtils.createColumnRealMatrix(y[sIdx]);
            covMatrix = covMatrix
                    .add(
                            ymatrix
                                    .multiply(ymatrix.transpose()).scalarMultiply(w[sIdx]));
        }
        return covMatrix;
    }

    public double[] computeMean(int dimension, List<Sample> samples, int lambda, double[] w) {
        double[] mean = new double[dimension];
        for (int sIdx = 0; sIdx < lambda; ++sIdx) {
            for (int dimIdx = 0; dimIdx < dimension; ++dimIdx) {
                mean[dimIdx] += w[sIdx] * samples.get(sIdx).getX()[dimIdx];
            }
        }
        return mean;
    }

    public double[] computeWeights(int lambda) {
        double[] w = new double[lambda];
        double basew = Math.log((double) lambda + 0.5);
        double sumw = 0.0;
        for (int i = 0; i < lambda; ++i) {
            w[i] = basew - Math.log((double) i + 1);
            sumw += w[i];
        }
        for (int i = 0; i < lambda; ++i) {
            w[i] /= sumw;
        }
        return w;
    }

    public void computeOldMu(int dimension, List<Sample> samples) {
        oldMu = new double[dimension];
        for (int i = 0; i < dimension; ++i) {
            final int dim = i;
            oldMu[i] = samples.stream().mapToDouble(s -> s.getX()[dim]).average().orElse(0.0);
        }
    }

    @Override
    public void registerObjectsWithOptimizer(SamplingOptimizer samplingOptimizer) {

    }

    @Override
    public void resetState(int particleCount) {
        isFirstInIteration = false;
        oldMu = null;
    }

    @Override
    public void registerPersonalImprovement(double deltaY) {

    }

    @Override
    public void newIteration() {
        isFirstInIteration = true;
    }
}
