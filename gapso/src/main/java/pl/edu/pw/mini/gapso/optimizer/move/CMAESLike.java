package pl.edu.pw.mini.gapso.optimizer.move;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
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
    private RealVector D;
    private RealMatrix C;
    private RealMatrix invsqrtC;
    private int eigeneval;
    private double chiN;
    private int counteval;
    private RealMatrix xOld;
    private RealMatrix xNew;

    public CMAESLike(MoveConfiguration configuration) {
        super(configuration);
        resetState(0);
    }

    public static RealMatrix computeRankMuUpdate(int mu, int dimension, double[] weights, double cmu, double sigma, RealMatrix xold, List<Sample> samples) {
        final RealMatrix artmp = computeArtMp(mu, dimension, sigma, xold, samples);
        return artmp.transpose().multiply(MatrixUtils.createRealDiagonalMatrix(weights)).multiply(artmp).scalarMultiply(cmu);
    }

    public static RealMatrix computeArtMp(int mu, int dimension, double sigma, RealMatrix xold, List<Sample> samples) {
        double[][] y = new double[mu][];
        for (int sIdx = 0; sIdx < mu; ++sIdx) {
            y[sIdx] = new double[dimension];
            for (int dimIdx = 0; dimIdx < dimension; ++dimIdx) {
                y[sIdx][dimIdx] = samples.get(sIdx).getX()[dimIdx] - xold.getEntry(dimIdx, 0);
            }
        }
        return MatrixUtils.createRealMatrix(y).scalarMultiply(1.0 / sigma);
    }

    public static EigenParts computeEigenDecomposition(RealMatrix C) {
        EigenParts parts = new EigenParts();
        EigenDecomposition eg = new EigenDecomposition(C);
        final double[] eigenvalues = eg.getRealEigenvalues();
        parts.B = eg.getV(); //eigen vectors
        parts.D = MatrixUtils.createRealVector(eigenvalues); //eigen values
        for (int i = 0; i < parts.D.getDimension(); ++i) {
            parts.D.setEntry(i, Math.sqrt(parts.D.getEntry(i)));
        }
        return parts;
    }

    public static RealMatrix computeSQRTCInvert(RealMatrix B, RealVector D) {
        RealVector OverD = MatrixUtils.createRealVector(new double[D.getDimension()]);
        for (int i = 0; i < D.getDimension(); ++i) {
            OverD.setEntry(i, 1.0 / D.getEntry(i));
        }
        return B.multiply(MatrixUtils.createRealDiagonalMatrix(OverD.toArray())).multiply(B.transpose());
    }

    public static RealMatrix computeC(
            List<Sample> samples, double hsig,
            double cc, double c1, RealMatrix pc,
            RealMatrix c, int mu, int dimension,
            double[] weights, double cmu, double sigma, RealMatrix xold) {
        final RealMatrix rankOneUpdate = computeRankOneUpdateWithCorrection(cc, c1, hsig, pc, c);
        final RealMatrix rankMuUpdate = computeRankMuUpdate(mu, dimension, weights,
                cmu, sigma, xold, samples);
        final RealMatrix oldCImpact = c.scalarMultiply(1 - c1 - cmu);
        return oldCImpact.add(rankOneUpdate).add(rankMuUpdate);
    }

    public static double computeHSig(int lambda, RealMatrix ps, double cs, int counteval, double chiN, int dimension) {
        boolean hsigb = ps.getFrobeniusNorm() / Math.sqrt(1.0 - Math.pow(1.0 - cs, 2.0 * counteval / lambda)) / chiN < (1.4 + 2.0 / (dimension + 1.0));
        return hsigb ? 1.0 : 0.0;
    }

    public static RealMatrix computeNormalizedDiff(double sigma, RealMatrix xold, RealMatrix xmean) {
        return xmean.subtract(xold).scalarMultiply(1.0 / sigma);
    }

    public static RealMatrix computePS(double cs, double mueff, RealMatrix invsqrtC, RealMatrix ps, RealMatrix normalizedMeanDiff) {
        return ps.scalarMultiply(1.0 - cs).add(
                invsqrtC.multiply(normalizedMeanDiff).scalarMultiply(Math.sqrt(cs * (2.0 - cs) * mueff))
        );
    }

    public static RealMatrix computePC(RealMatrix normalizedMeanDiff, double hsig, RealMatrix pc, double cc, double mueff) {
        return pc.scalarMultiply((1 - cc)).add(
                normalizedMeanDiff.scalarMultiply(hsig * Math.sqrt(cc * (2 - cc) * mueff))
        );
    }

    public void initializeCovMatrix(List<Sample> samples) {
        pc = MatrixUtils.createRealMatrix(dimension, 1);
        ps = MatrixUtils.createRealMatrix(dimension, 1);
        double[] mean = computeAverageMean(samples);
        double sigmaSum = 0.0;
        for (int i = 0; i < mean.length; ++i) {
            int finalI = i;
            sigmaSum += Math.sqrt(samples.stream()
                    .mapToDouble(s -> (s.getX()[finalI] - mean[finalI]) * (s.getX()[finalI] - mean[finalI]))
                    .average().orElse(1.0));
        }

        sigma = sigmaSum / (double) mean.length;
        double[] diag = new double[dimension];
        Arrays.fill(diag, 1.0);
        B = MatrixUtils.createRealIdentityMatrix(dimension);
        D = MatrixUtils.createRealVector(diag);
        C = B.multiply(MatrixUtils.createRealDiagonalMatrix(diag)).multiply(B.transpose());
        invsqrtC = computeSQRTCInvert(B, D);
        eigeneval = 0;
    }

    public static RealMatrix computeRankOneUpdateWithCorrection(double cc, double c1, double hsig, RealMatrix pc, RealMatrix C) {
        return (pc.multiply(pc.transpose()).add(
                C.scalarMultiply((1 - hsig) * cc * (2 - cc)))).scalarMultiply(c1);
    }

    public static double[] computeWeightedMean(double[] weights, List<Sample> samples) {
        assert samples.size() > 0;
        final int dimension = samples.get(0).getX().length;
        double[] mean = new double[dimension];
        for (int sIdx = 0; sIdx < weights.length; ++sIdx) {
            for (int dimIdx = 0; dimIdx < dimension; ++dimIdx) {
                mean[dimIdx] += weights[sIdx] * samples.get(sIdx).getX()[dimIdx];
            }
        }
        return mean;
    }

    public static double[] computeWeights(int mu) {
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

    public static double[] computeAverageMean(List<Sample> samples) {
        assert samples.size() > 0;
        int dimension = samples.get(0).getX().length;
        double[] meanArray = new double[dimension];
        for (int i = 0; i < dimension; ++i) {
            final int dim = i;
            meanArray[i] = samples.stream().mapToDouble(s -> s.getX()[dim]).average().orElse(0.0);
        }
        return meanArray;
    }

    ;

    public void initializeParameters(int length, int lambda, List<Sample> samples) {
        //selection
        dimension = length;
        isInitialized = true;
        mu = Math.min((4 + (int) Math.floor(3 * Math.log(dimension))) / 2, (int) Math.round(lambda * 0.5));
        weights = computeWeights(mu);
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
        initializeCovMatrix(samples);

        chiN = Math.sqrt(dimension) * (1.0 - 1.0 / (4.0 * dimension) + 1 / (21.0 * dimension * dimension));
        counteval = 0;
        isInitialized = true;
    }

    public static double[] scaleAndRotateVector(double[] normals, RealMatrix xNew, RealMatrix b, RealVector d, double sigma) {
        return xNew.getColumnVector(0).add(
                b.preMultiply(d.ebeMultiply(MatrixUtils.createRealVector(normals))).mapMultiply(sigma)
        ).toArray();
    }

    @Override
    public double[] getNext(Particle currentParticle, List<Particle> particleList) {
        final int length = currentParticle.getBest().getX().length;
        //TODO: consider taking only particles produced by CMA-ES between iterations
        final int lambda = particleList.size();
        if (isFirstInIteration) {

            final List<Sample> samples = particleList.stream().map(Particle::getCurrent)
                    .sorted(Comparator.comparingDouble(Sample::getY)).collect(Collectors.toList());
            if (!isInitialized) {
                initializeParameters(length, lambda, samples);
            }
            if (xOld == null) {
                xOld = MatrixUtils.createColumnRealMatrix(computeAverageMean(samples));
            } else {
                xOld = xNew;
            }
            xNew = MatrixUtils.createColumnRealMatrix(computeWeightedMean(weights, samples));
            try {
                computeCovarianceMatrixAndUpdateSigma(samples);
            } catch (Exception ex) {
                return null;
            }
            isFirstInIteration = false;
            //mvnd = new MultivariateNormalDistribution(Generator.RANDOM, newM, C.scalarMultiply(sigma).getData());

        }
        //TODO: consider if not count also other evaluations so add lamba at begining of iteration
        counteval += 1;
        NormalDistribution nd = new NormalDistribution(Generator.RANDOM, 0, 1);
        double[] normals = nd.sample(length);
        return scaleAndRotateVector(normals, xNew, B, D, sigma);
    }

    public void computeCovarianceMatrixAndUpdateSigma(List<Sample> samples) throws Exception {
        int lambda = samples.size();
        final RealMatrix normalizedMeanDiff = computeNormalizedDiff(sigma, xOld, xNew);
        ps = computePS(cs, mueff, invsqrtC, ps, normalizedMeanDiff);
        double hsig = computeHSig(lambda, ps, cs, counteval, chiN, dimension);
        hsig = 1.0;
        pc = computePC(normalizedMeanDiff, hsig, pc, cc, mueff);
        C = computeC(samples, hsig, cc, c1, pc, C, mu, dimension, weights, cmu, sigma, xOld);
        sigma = sigma * Math.exp((cs / damps) * ((ps).getFrobeniusNorm() / chiN - 1));
        if (Double.isInfinite(sigma) || sigma > 10000) {
            throw new Exception();
        }
        if (Double.isNaN(sigma)) {
            throw new Exception();
        }
/*
        if (counteval - eigeneval > lambda / (c1 + cmu) / dimension / 10.0) {// otherwise MaxCountExceededException when sampling from multivariate
            eigeneval = counteval;

 */
            for (int i = 0; i < C.getColumnDimension(); ++i) {
                for (int j = i + 1; j < C.getRowDimension(); ++j) {
                    C.setEntry(j, i, C.getEntry(i, j));
                }
            }
            try {
                EigenParts parts = computeEigenDecomposition(C);
                B = parts.B;
                D = parts.D;
                invsqrtC = computeSQRTCInvert(B, D);
                if (D.getMaxValue() > 1e7 * D.getMinValue()) {
                    throw new Exception();
                }
            } catch (MaxCountExceededException ex) {
                throw new Exception();
            }
            /*
        }
             */
    }

    @Override
    public void resetState(int particleCount) {
        xOld = null;
        isFirstInIteration = false;
        isInitialized = false;
        resetWeight();
    }

    @Override
    public void registerObjectsWithOptimizer(SamplingOptimizer samplingOptimizer) {

    }

    static class EigenParts {
        public RealMatrix B;
        public RealVector D;
    }

    @Override
    public void registerPersonalImprovement(double deltaY) {

    }

    @Override
    public void newIteration() {
        isFirstInIteration = true;
    }
}
