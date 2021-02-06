package pl.edu.pw.mini.gapso.optimizer.move;

import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import pl.edu.pw.mini.gapso.configuration.MoveConfiguration;
import pl.edu.pw.mini.gapso.optimizer.Particle;
import pl.edu.pw.mini.gapso.optimizer.SamplingOptimizer;
import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.utils.Generator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CMAESLike extends Move {
    public static final String NAME = "CMA-ES-like";
    private boolean isFirstInIteration;
    private double[] oldM;
    private double[] newM;
    private MultivariateNormalDistribution mvnd;
    private boolean isInitialized;
    private int dimension;
    private int mu;
    private double[] weights;
    private double mueff;
    private double cc;
    private double cs;
    private double sigma;
    private double c1;
    private double cmu;
    private double damps;
    private RealMatrix pc;
    private RealMatrix ps;
    private RealMatrix B;
    private RealMatrix D;
    private RealMatrix C;
    private RealMatrix invsqrtC;
    private int eigeneval;
    private double chiN;
    private int counteval;

    public CMAESLike(MoveConfiguration configuration) {
        super(configuration);
        resetState(0);
    }

    @Override
    public double[] getNext(Particle currentParticle, List<Particle> particleList) {
        if (isFirstInIteration) {
            try {
                final List<Sample> samples = particleList.stream().map(Particle::getBest)
                        .sorted(Comparator.comparingDouble(Sample::getY)).collect(Collectors.toList());
                if (!isInitialized) {
                    final int length = currentParticle.getBest().getX().length;
                    final int lambda = samples.size();
                    initializeParameters(length, lambda);
                }
                if (oldM == null) {
                    computeOldMu(samples);
                } else {
                    oldM = newM;
                }
                newM = computeMean(samples);
                computeCovarianceMatrixAndUpdateSigma(samples);
                isFirstInIteration = false;
                mvnd = new MultivariateNormalDistribution(Generator.RANDOM, newM, C.scalarMultiply(sigma).getData());
            } catch (Exception ex) {
                return null;
            }
        }
        //TODO: consider if not count also other evaluations so add lamba at begining of iteration
        counteval += 1;
        assert mvnd != null;
        return mvnd.sample();
    }

    public void initializeParameters(int length, int lambda) {
        //selection
        dimension = length;
        isInitialized = true;
        mu = (int) Math.round(lambda * 0.5);
        weights = computeWeights();
        double weightssum = Arrays.stream(weights).sum();
        double sumweightssquare = Arrays.stream(weights).map(w -> w * w).sum();
        mueff = weightssum * weightssum / sumweightssquare;
        //adaptation
        cc = (4.0 + mueff / dimension) / (dimension + 4.0 + 2.0 * mueff / dimension);
        cs = (mueff + 2.0) / (dimension + mueff + 5.0);
        c1 = 2.0 / ((dimension + 1.3) * (dimension + 1.3) + mueff);
        cmu = Math.min(1.0 - c1, 2.0 * (mueff - 2.0 + 1.0 / mueff) / ((dimension + 2.0) * (dimension + 2.0) + mueff));
        damps = 1.0 + 2.0 * Math.max(0.0, Math.sqrt((mueff - 1.0) / (dimension + 1.0)) - 1.0) + cs;
        //dynamic parameters
        pc = MatrixUtils.createRealMatrix(dimension, 1);
        ps = MatrixUtils.createRealMatrix(dimension, 1);
        B = MatrixUtils.createRealIdentityMatrix(dimension);
        double[] diag = new double[dimension];
        Arrays.fill(diag, 1.0);
        D = MatrixUtils.createRealDiagonalMatrix(diag);
        D = D.scalarAdd(1);
        C = B.multiply(D.power(2)).multiply(B.transpose());
        invsqrtC = B.multiply(MatrixUtils.inverse(D)).multiply(B.transpose());
        eigeneval = 0;
        chiN = Math.sqrt(dimension) * (1.0 - 1.0 / (4.0 * dimension) + 1 / (21.0 * dimension * dimension));
        counteval = 0;
        isInitialized = true;
    }

    public void computeCovarianceMatrixAndUpdateSigma(List<Sample> samples) {
        int lambda = samples.size();
        final RealMatrix xold = MatrixUtils.createColumnRealMatrix(oldM);
        final RealMatrix xmean = MatrixUtils.createColumnRealMatrix(newM);
        final RealMatrix normalizedMeanDiff = xmean.subtract(xold).scalarMultiply(1.0 / sigma);
        ps = ps.scalarMultiply(1.0 - cs).add(
                invsqrtC.multiply(normalizedMeanDiff).scalarMultiply(Math.sqrt(cs * (2.0 - cs) * mueff))
        );
        boolean hsigb = ps.getNorm() / Math.sqrt(1.0 - Math.pow(1.0 - cs, 2.0 * counteval / lambda)) / chiN < (1.4 + 2.0 / (dimension + 1.0));
        double hsig = hsigb ? 1.0 : 0.0;
        pc = pc.scalarMultiply((1 - cc)).add(
                normalizedMeanDiff.scalarMultiply(hsig * Math.sqrt(cc * (2 - cc) * mueff))
        );


        double[][] y = new double[mu][];
        for (int sIdx = 0; sIdx < mu; ++sIdx) {
            y[sIdx] = new double[dimension];
            for (int dimIdx = 0; dimIdx < dimension; ++dimIdx) {
                y[sIdx][dimIdx] = samples.get(sIdx).getX()[dimIdx] - oldM[dimIdx];
            }
        }
        final RealMatrix artmp = MatrixUtils.createRealMatrix(y).scalarMultiply(1.0 / sigma);

        final RealMatrix oldCImpact = C.scalarMultiply(1 - c1 - cmu);
        final RealMatrix rankOneUpdate = (pc.multiply(pc.transpose()).add(
                C.scalarMultiply((1 - hsig) * cc * (2 - cc)))).scalarMultiply(c1);
        final RealMatrix rankMuUpdate = artmp.transpose().multiply(MatrixUtils.createRealDiagonalMatrix(weights)).multiply(artmp).scalarMultiply(cmu);
        C = oldCImpact.add(rankOneUpdate).add(rankMuUpdate);

        sigma = sigma * Math.exp((cs/damps)*((ps).getNorm()/chiN - 1));

        if (counteval - eigeneval > lambda/(c1+cmu)/dimension/10.0) {// otherwise MaxCountExceededException when sampling from multivariate
            eigeneval = counteval;
            for (int i = 0; i < C.getColumnDimension(); ++i) {
                for (int j = i; j < C.getRowDimension(); ++j) {
                    C.setEntry(j, i, C.getEntry(i, j));
                }
            }
            EigenDecomposition eg = new EigenDecomposition(C);
            D = eg.getD(); //eigen values
            B = eg.getV(); //eigen vectors
            for (int i = 0; i < D.getRowDimension(); ++i) {
                D.setEntry(i,i, Math.sqrt(D.getEntry(i,i)));
            }
            invsqrtC = B.multiply(MatrixUtils.inverse(D)).multiply(B.transpose());
        }
    }

    public double[] computeMean(List<Sample> samples) {
        double[] mean = new double[dimension];
        for (int sIdx = 0; sIdx < mu; ++sIdx) {
            for (int dimIdx = 0; dimIdx < dimension; ++dimIdx) {
                mean[dimIdx] += weights[sIdx] * samples.get(sIdx).getX()[dimIdx];
            }
        }
        return mean;
    }

    public double[] computeWeights() {
        double[] w = new double[mu];
        double basew = Math.log((double) mu + 0.5);
        double sumw = 0.0;
        for (int i = 0; i < mu; ++i) {
            w[i] = basew - Math.log((double) i + 1);
            sumw += w[i];
        }
        for (int i = 0; i < mu; ++i) {
            w[i] /= sumw;
        }
        return w;
    }

    public void computeOldMu(List<Sample> samples) {
        sigma = 0.3;
        oldM = new double[dimension];
        for (int i = 0; i < dimension; ++i) {
            final int dim = i;
            oldM[i] = samples.stream().mapToDouble(s -> s.getX()[dim]).average().orElse(0.0);
        }
    }

    @Override
    public void registerObjectsWithOptimizer(SamplingOptimizer samplingOptimizer) {

    }

    @Override
    public void resetState(int particleCount) {
        isFirstInIteration = false;
        isInitialized = false;
    }

    @Override
    public void registerPersonalImprovement(double deltaY) {

    }

    @Override
    public void newIteration() {
        isFirstInIteration = true;
    }
}
