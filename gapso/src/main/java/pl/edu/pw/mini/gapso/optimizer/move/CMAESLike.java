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
    private double[] oldM;
    private double[] newM;
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

    public CMAESLike(MoveConfiguration configuration) {
        super(configuration);
        resetState(0);
    }

    @Override
    public double[] getNext(Particle currentParticle, List<Particle> particleList) {
        final int length = currentParticle.getBest().getX().length;
        //TODO: consider taking only particles produced by CMA-ES between iterations
        final int lambda = particleList.size();
        if (isFirstInIteration) {
            try {
                final List<Sample> samples = particleList.stream().map(Particle::getBest)
                        .sorted(Comparator.comparingDouble(Sample::getY)).collect(Collectors.toList());
                if (!isInitialized) {
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
                //mvnd = new MultivariateNormalDistribution(Generator.RANDOM, newM, C.scalarMultiply(sigma).getData());
            } catch (Exception ex) {
                resetState(0);
                setWeight(0.0);
                return null;
            }
        }
        //TODO: consider if not count also other evaluations so add lamba at begining of iteration
        counteval += 1;
        NormalDistribution nd = new NormalDistribution(Generator.RANDOM, 0, 1);
        double[] normals = nd.sample(length);
        return MatrixUtils.createRealVector(newM).add(
                B.preMultiply(D.ebeMultiply(MatrixUtils.createRealVector(normals))).mapMultiply(sigma)
        ).toArray();
    }

    public void initializeParameters(int length, int lambda) {
        //selection
        dimension = length;
        isInitialized = true;
        mu = Math.min((4 + (int) Math.floor(3 * Math.log(dimension))) / 2, (int) Math.round(lambda * 0.5));
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
        initializeCovMatrix();

        chiN = Math.sqrt(dimension) * (1.0 - 1.0 / (4.0 * dimension) + 1 / (21.0 * dimension * dimension));
        counteval = 0;
        isInitialized = true;
    }

    public void initializeCovMatrix() {
        pc = MatrixUtils.createRealMatrix(dimension, 1);
        ps = MatrixUtils.createRealMatrix(dimension, 1);
        sigma = 0.3;
        double[] diag = new double[dimension];
        Arrays.fill(diag, 1.0);
        B = MatrixUtils.createRealIdentityMatrix(dimension);
        D = MatrixUtils.createRealVector(diag);
        C = B.multiply(MatrixUtils.createRealDiagonalMatrix(diag)).multiply(B.transpose());
        invsqrtC = B.multiply(MatrixUtils.createRealDiagonalMatrix(diag)).multiply(B.transpose());
        eigeneval = 0;
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
        if (Double.isInfinite(sigma)) {
            initializeCovMatrix();
        }
        if (Double.isNaN(sigma)) {
            initializeCovMatrix();
        }

        if (counteval - eigeneval > lambda/(c1+cmu)/dimension/10.0) {// otherwise MaxCountExceededException when sampling from multivariate
            eigeneval = counteval;
            for (int i = 0; i < C.getColumnDimension(); ++i) {
                for (int j = i + 1; j < C.getRowDimension(); ++j) {
                    C.setEntry(j, i, C.getEntry(i, j));
                }
            }
            try {
                EigenDecomposition eg = new EigenDecomposition(C);
                D = MatrixUtils.createRealVector(eg.getRealEigenvalues()); //eigen values
                RealVector OverD = MatrixUtils.createRealVector(eg.getRealEigenvalues());
                B = eg.getV(); //eigen vectors
                for (int i = 0; i < D.getDimension(); ++i) {
                    D.setEntry(i, Math.sqrt(D.getEntry(i)));
                    OverD.setEntry(i, 1.0 / D.getEntry(i));
                }
                invsqrtC = B.multiply(MatrixUtils.createRealDiagonalMatrix(OverD.toArray())).multiply(B.transpose());
                if (D.getMaxValue() > 1e7 * D.getMinValue()) {
                    initializeCovMatrix();
                }
            } catch (MaxCountExceededException ex) {
                initializeCovMatrix();
            }
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
        oldM = null;
        isFirstInIteration = false;
        isInitialized = false;
        resetWeight();
    }

    @Override
    public void registerPersonalImprovement(double deltaY) {

    }

    @Override
    public void newIteration() {
        isFirstInIteration = true;
    }
}
