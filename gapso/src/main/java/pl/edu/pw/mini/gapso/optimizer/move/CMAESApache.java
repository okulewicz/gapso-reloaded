/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.edu.pw.mini.gapso.optimizer.move;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;
import pl.edu.pw.mini.gapso.configuration.MoveConfiguration;
import pl.edu.pw.mini.gapso.function.Function;
import pl.edu.pw.mini.gapso.optimizer.Particle;
import pl.edu.pw.mini.gapso.optimizer.SamplingOptimizer;
import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.utils.Generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * An implementation of the active Covariance Matrix Adaptation Evolution Strategy (CMA-ES)
 * for non-linear, non-convex, non-smooth, global function minimization.
 * <p>
 * The CMA-Evolution Strategy (CMA-ES) is a reliable stochastic optimization method
 * which should be applied if derivative-based methods, e.g. quasi-Newton BFGS or
 * conjugate gradient, fail due to a rugged search landscape (e.g. noise, local
 * optima, outlier, etc.) of the objective function. Like a
 * quasi-Newton method, the CMA-ES learns and applies a variable metric
 * on the underlying search space. Unlike a quasi-Newton method, the
 * CMA-ES neither estimates nor uses gradients, making it considerably more
 * reliable in terms of finding a good, or even close to optimal, solution.
 * <p>
 * In general, on smooth objective functions the CMA-ES is roughly ten times
 * slower than BFGS (counting objective function evaluations, no gradients provided).
 * For up to <math>N=10</math> variables also the derivative-free simplex
 * direct search method (Nelder and Mead) can be faster, but it is
 * far less reliable than CMA-ES.
 * <p>
 * The CMA-ES is particularly well suited for non-separable
 * and/or badly conditioned problems. To observe the advantage of CMA compared
 * to a conventional evolution strategy, it will usually take about
 * <math>30 N</math> function evaluations. On difficult problems the complete
 * optimization (a single run) is expected to take <em>roughly</em> between
 * <math>30 N</math> and <math> N<sup>2</sup></math>
 * function evaluations.
 * <p>
 * This implementation is translated and adapted from the Matlab version
 * of the CMA-ES algorithm as implemented in module {@code cmaes.m} version 3.51.
 * <p>
 * For more information, please refer to the following links:
 * <ul>
 *  <li><a href="http://www.lri.fr/~hansen/cmaes.m">Matlab code</a></li>
 *  <li><a href="http://www.lri.fr/~hansen/cmaesintro.html">Introduction to CMA-ES</a></li>
 *  <li><a href="http://en.wikipedia.org/wiki/CMA-ES">Wikipedia</a></li>
 * </ul>
 *
 * @since 3.0
 */
public class CMAESApache extends Move {
    public static String NAME = "CMAESApache";
    // global search parameters
    /**
     * Covariance update mechanism, default is active CMA. isActiveCMA = true
     * turns on "active CMA" with a negative update of the covariance matrix and
     * checks for positive definiteness. OPTS.CMA.active = 2 does not check for
     * pos. def. and is numerically faster. Active CMA usually speeds up the
     * adaptation.
     */
    private final boolean isActiveCMA = true;
    /**
     * History of sigma values.
     */
    private final List<Double> statisticsSigmaHistory = new ArrayList<>();
    /**
     * History of mean matrix.
     */
    private final List<RealMatrix> statisticsMeanHistory = new ArrayList<>();
    /**
     * History of fitness values.
     */
    private final List<Double> statisticsFitnessHistory = new ArrayList<>();
    /**
     * History of D matrix.
     */
    private final List<RealMatrix> statisticsDHistory = new ArrayList<>();
    /**
     * Population size, offspring number. The primary strategy parameter to play
     * with, which can be increased from its default value. Increasing the
     * population size improves global search properties in exchange to speed.
     * Speed decreases, as a rule, at most linearly with increasing population
     * size. It is advisable to begin with the default small population size.
     */
    private int lambda; // population size
    /**
     * Determines how often a new random offspring is generated in case it is
     * not feasible / beyond the defined limits, default is 0.
     */
    private int checkFeasableCount;
    private double[] inputSigma;

    // termination criteria
    /**
     * Number of objective variables/problem dimension
     */
    private int dimension;
    /**
     * Defines the number of initial iterations, where the covariance matrix
     * remains diagonal and the algorithm has internally linear time complexity.
     * diagonalOnly = 1 means keeping the covariance matrix always diagonal and
     * this setting also exhibits linear space complexity. This can be
     * particularly useful for dimension > .
     *
     * @see <a href="http://hal.archives-ouvertes.fr/inria-/en">A Simple Modification in CMA-ES</a>
     */
    private int diagonalOnly;
    /**
     * Number of objective variables/problem dimension
     */
    private boolean isMinimize = true;
    /**
     * Indicates whether statistic data is collected.
     */
    private boolean generateStatistics;
    /**
     * Maximal number of iterations allowed.
     */
    private int maxIterations;
    /**
     * Limit for fitness value.
     */
    private double stopFitness;

    // selection strategy parameters
    /**
     * Stop if x-changes larger stopTolUpX.
     */
    private double stopTolUpX;
    /**
     * Stop if x-change smaller stopTolX.
     */
    private double stopTolX;
    /**
     * Stop if fun-changes smaller stopTolFun.
     */
    private double stopTolFun;
    /**
     * Stop if back fun-changes smaller stopTolHistFun.
     */
    private double stopTolHistFun;

    // dynamic strategy parameters and constants
    /**
     * Number of parents/points for recombination.
     */
    private int mu; //
    /**
     * log(mu + 0.5), stored for efficiency.
     */
    private double logMu2;
    /**
     * Array for weighted recombination.
     */
    private RealMatrix weights;
    /**
     * Variance-effectiveness of sum w_i x_i.
     */
    private double mueff; //
    /**
     * Overall standard deviation - search volume.
     */
    private double sigma;
    /**
     * Cumulation constant.
     */
    private double cc;
    /**
     * Cumulation constant for step-size.
     */
    private double cs;
    /**
     * Damping for step-size.
     */
    private double damps;
    /**
     * Learning rate for rank-one update.
     */
    private double ccov1;

    // CMA internal values - updated each generation
    /**
     * Learning rate for rank-mu update'
     */
    private double ccovmu;
    /**
     * Expectation of ||N(0,I)|| == norm(randn(N,1)).
     */
    private double chiN;
    /**
     * Learning rate for rank-one update - diagonalOnly
     */
    private double ccov1Sep;
    /**
     * Learning rate for rank-mu update - diagonalOnly
     */
    private double ccovmuSep;
    /**
     * Objective variables.
     */
    private RealMatrix xmean;
    /**
     * Evolution path.
     */
    private RealMatrix pc;
    /**
     * Evolution path for sigma.
     */
    private RealMatrix ps;
    /**
     * Norm of ps, stored for efficiency.
     */
    private double normps;
    /**
     * Coordinate system.
     */
    private RealMatrix B;
    /**
     * Scaling.
     */
    private RealMatrix D;
    /**
     * B*D, stored for efficiency.
     */
    private RealMatrix BD;
    /**
     * Diagonal of sqrt(D), stored for efficiency.
     */
    private RealMatrix diagD;
    /**
     * Covariance matrix.
     */
    private RealMatrix C;
    /**
     * Diagonal of C, used for diagonalOnly.
     */
    private RealMatrix diagC;
    /**
     * Number of iterations already performed.
     */
    private int iterations;
    /**
     * History queue of best values.
     */
    private double[] fitnessHistory;
    /**
     * Size of history queue of best values.
     */
    private int historySize;
    private boolean isInitialized;
    private boolean firstInIteration;
    private int accumulatedLambda;
    private double bestValue;
    private RealMatrix arxAccumulator;
    private RealMatrix arzAccumulator;

    public CMAESApache(MoveConfiguration configuration) {
        super(configuration);
    }

    /**
     * Pushes the current best fitness value in a history queue.
     *
     * @param vals History queue.
     * @param val  Current best fitness value.
     */
    private static void push(double[] vals, double val) {
        if (vals.length - 1 >= 0) System.arraycopy(vals, 0, vals, 1, vals.length - 1);
        vals[0] = val;
    }

    /**
     * @return History of sigma values.
     */
    public List<Double> getStatisticsSigmaHistory() {
        return statisticsSigmaHistory;
    }

    /**
     * @return History of mean matrix.
     */
    public List<RealMatrix> getStatisticsMeanHistory() {
        return statisticsMeanHistory;
    }

    /**
     * @return History of fitness values.
     */
    public List<Double> getStatisticsFitnessHistory() {
        return statisticsFitnessHistory;
    }

    /**
     * @return History of D matrix.
     */
    public List<RealMatrix> getStatisticsDHistory() {
        return statisticsDHistory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double[] getNext(Particle currentParticle, List<Particle> particleList) throws IllegalStateException {
        RealMatrix arx;
        RealMatrix arz;
        if (firstInIteration) {
            final List<Sample> currentSamples = particleList.stream().map(Particle::getCurrent).collect(Collectors.toList());
            double[] fitness;
            // -------------------- Initialization --------------------------------
            if (!isInitialized) {
                isMinimize = true;
                final Function fitfun = currentParticle.getFunction();
                final double[] guess = computeMeanLocation(particleList);
                inputSigma = computeSigmas(guess, particleList);
                // number of objective variables/problem dimension
                dimension = guess.length;
                lambda = particleList.size();
                initializeCMA(guess);

                iterations = 0;

                arx = DoubleIndex.zeros(dimension, lambda);
                arz = DoubleIndex.zeros(dimension, lambda);

                for (int l = 0; l < lambda; ++l) {
                    final double[] x = currentSamples.get(l).getX();
                    RealMatrix xCol = MatrixUtils.createColumnRealMatrix(x);
                    arx.setColumnMatrix(l, xCol);
                    double[] zvec = new double[dimension];
                    for (int d = 0; d < dimension; ++d) {
                        zvec[d] = (x[d] - guess[d]) / sigma;
                    }
                    arz.setColumn(l, zvec);
                }
                fitness = currentSamples.stream().mapToDouble(Sample::getY).toArray();

                bestValue = currentSamples.stream().mapToDouble(Sample::getY).min().orElse(Double.POSITIVE_INFINITY);
                isInitialized = true;
            } else {
                arx = arxAccumulator;
                arz = arzAccumulator;
                lambda = accumulatedLambda;
                fitness = new double[lambda];
                for (int l = 0; l < lambda; ++l) {
                    double[] x = arx.getColumn(l);
                    Sample matchingSample = currentSamples
                            .stream().filter(s -> Arrays.equals(x, s.getX()))
                            .collect(Collectors.toList()).get(0);
                    fitness[l] = matchingSample.getY();
                }
            }
            arxAccumulator = null;
            arzAccumulator = null;
            initializeLambdaDependentCoefficients();
            accumulatedLambda = 0;

            // Sort by fitness and compute weighted mean into xmean
            final int[] arindex = sortedIndices(fitness);
            // Calculate new xmean, this is selection and recombination
            final RealMatrix xold = xmean; // for speed up of Eq. (2) and (3)
            final RealMatrix bestArx = DoubleIndex.selectColumns(arx, MathArrays.copyOf(arindex, mu));
            xmean = bestArx.multiply(weights);
            final RealMatrix bestArz = DoubleIndex.selectColumns(arz, MathArrays.copyOf(arindex, mu));
            final RealMatrix zmean = bestArz.multiply(weights);
            final boolean hsig = updateEvolutionPaths(zmean, xold);
            if (diagonalOnly <= 0) {
                updateCovariance(hsig, bestArx, arz, arindex, xold);
            } else {
                updateCovarianceDiagonalOnly(hsig, bestArz);
            }
            // Adapt step size sigma - Eq. (5)
            sigma *= FastMath.exp(FastMath.min(1, (normps / chiN - 1) * cs / damps));
            final double bestFitness = fitness[arindex[0]];
            final double worstFitness = fitness[arindex[arindex.length - 1]];
            if (bestValue > bestFitness) {
                bestValue = bestFitness;
            }
            final double[] sqrtDiagC = DoubleIndex.sqrt(diagC).getColumn(0);
            final double[] pcCol = pc.getColumn(0);
            for (int i = 0; i < dimension; i++) {
                if (sigma * FastMath.max(FastMath.abs(pcCol[i]), sqrtDiagC[i]) > stopTolX) {
                    throw new IllegalStateException();
                }
            }
            for (int i = 0; i < dimension; i++) {
                if (sigma * sqrtDiagC[i] > stopTolUpX) {
                    throw new IllegalStateException();
                }
            }
            final double historyBest = DoubleIndex.min(fitnessHistory);
            final double historyWorst = DoubleIndex.max(fitnessHistory);
            if (iterations > 2 &&
                    FastMath.max(historyWorst, worstFitness) -
                            FastMath.min(historyBest, bestFitness) < stopTolFun) {
                throw new IllegalStateException();
            }
            if (iterations > fitnessHistory.length &&
                    historyWorst - historyBest < stopTolHistFun) {
                throw new IllegalStateException();
            }
            // condition number of the covariance matrix exceeds 1e14
            if (DoubleIndex.max(diagD) / DoubleIndex.min(diagD) > 1e7) {
                throw new IllegalStateException();
            }

            // Adjust step size in case of equal function values (flat fitness)
            if (bestValue == fitness[arindex[(int) (0.1 + lambda / 4.)]]) {
                sigma *= FastMath.exp(0.2 + cs / damps);
            }
            if (iterations > 2 && FastMath.max(historyWorst, bestFitness) -
                    FastMath.min(historyBest, bestFitness) == 0) {
                sigma *= FastMath.exp(0.2 + cs / damps);
            }
            // store best in history
            push(fitnessHistory, bestFitness);
            if (generateStatistics) {
                statisticsSigmaHistory.add(sigma);
                statisticsFitnessHistory.add(bestFitness);
                statisticsMeanHistory.add(xmean.transpose());
                statisticsDHistory.add(diagD.transpose().scalarMultiply(1E5));
            }
        }

        // -------------------- Generation Loop --------------------------------

        accumulatedLambda += 1;
        RealMatrix newArxAccumulator = MatrixUtils.createRealMatrix(dimension, accumulatedLambda);
        RealMatrix newArzAccumulator = MatrixUtils.createRealMatrix(dimension, accumulatedLambda);
        for (int l = 0; l < accumulatedLambda - 1; ++l) {
            newArxAccumulator.setColumn(l, arxAccumulator.getColumn(l));
            newArzAccumulator.setColumn(l, arzAccumulator.getColumn(l));
        }
        arxAccumulator = newArxAccumulator;
        arzAccumulator = newArzAccumulator;
        // Generate and evaluate lambda offspring
        boolean isWithinBounds = false;
        double[] x = new double[dimension];
        while (!isWithinBounds) {
            RealMatrix randVector = DoubleIndex.randn1(dimension, 1);
            arzAccumulator.setColumnMatrix(accumulatedLambda - 1, randVector);
            if (diagonalOnly <= 0) {
                arxAccumulator.setColumnMatrix(accumulatedLambda - 1, xmean.add(BD.multiply(arzAccumulator.getColumnMatrix(accumulatedLambda - 1))
                        .scalarMultiply(sigma))); // m + sig * Normal(0,C)
            } else {
                arxAccumulator.setColumnMatrix(accumulatedLambda - 1, xmean.add(DoubleIndex.times(diagD, arzAccumulator.getColumnMatrix(accumulatedLambda - 1))
                        .scalarMultiply(sigma)));
            }
            x = arxAccumulator.getColumn(accumulatedLambda - 1);
            if (currentParticle.getFunction().getBounds().contain(x)) {
                isWithinBounds = true;
            }
        }
        return x;
    }

    private double[] computeSigmas(double[] guess, List<Particle> particleList) {
        final int dimension = guess.length;
        double[] sigmas = new double[dimension];
        for (int d = 0; d < dimension; ++d) {
            int finalD = d;
            sigmas[finalD] = Math.sqrt(particleList
                    .stream()
                    .mapToDouble(p -> (p.getCurrent().getX()[finalD] - guess[finalD]) * (p.getCurrent().getX()[finalD] - guess[finalD]))
                    .average().orElse(1.0));
        }
        return sigmas;
    }

    @Override
    public void registerObjectsWithOptimizer(SamplingOptimizer samplingOptimizer) {

    }

    @Override
    public void resetState(int particleCount) {
        isInitialized = false;
    }

    @Override
    public void registerPersonalImprovement(double deltaY) {

    }

    @Override
    public void newIteration() {
        firstInIteration = true;
    }

    private double[] computeMeanLocation(List<Particle> particleList) {
        final int dimension = particleList.get(0).getCurrent().getX().length;
        double[] mean = new double[dimension];
        for (int d = 0; d < dimension; ++d) {
            int finalD = d;
            mean[finalD] = particleList.stream().mapToDouble(p -> p.getCurrent().getX()[finalD]).average().orElse(0.0);
        }
        return mean;
    }

    /**
     * Initialization of the dynamic search parameters
     *
     * @param guess Initial guess for the arguments of the fitness function.
     */
    private void initializeCMA(double[] guess) {
        if (lambda <= 0) {
            throw new NotStrictlyPositiveException(lambda);
        }
        // initialize sigma
        final double[][] sigmaArray = new double[guess.length][1];
        for (int i = 0; i < guess.length; i++) {
            sigmaArray[i][0] = inputSigma[i];
        }
        final RealMatrix insigma = new Array2DRowRealMatrix(sigmaArray, false);
        sigma = DoubleIndex.max(insigma); // overall standard deviation

        // initialize termination criteria
        stopTolUpX = 1e3 * DoubleIndex.max(insigma);
        stopTolX = 1e-11 * DoubleIndex.max(insigma);
        stopTolFun = 1e-12;
        stopTolHistFun = 1e-13;
        initializeLambdaDependentCoefficients();

        // intialize CMA internal values - updated each generation
        xmean = MatrixUtils.createColumnRealMatrix(guess); // objective variables
        diagD = insigma.scalarMultiply(1 / sigma);
        diagC = DoubleIndex.square(diagD);
        pc = DoubleIndex.zeros(dimension, 1); // evolution paths for C and sigma
        ps = DoubleIndex.zeros(dimension, 1); // B defines the coordinate system
        normps = ps.getFrobeniusNorm();

        B = DoubleIndex.eye(dimension, dimension);
        D = DoubleIndex.ones(dimension, 1); // diagonal D defines the scaling
        BD = DoubleIndex.times(B, DoubleIndex.repmat(diagD.transpose(), dimension, 1));
        C = B.multiply(DoubleIndex.diag(DoubleIndex.square(D)).multiply(B.transpose())); // covariance
        historySize = 10 + (int) (3 * 10 * dimension / (double) lambda);
        fitnessHistory = new double[historySize]; // history of fitness values
        for (int i = 0; i < historySize; i++) {
            fitnessHistory[i] = Double.MAX_VALUE;
        }
    }

    private void initializeLambdaDependentCoefficients() {
        // initialize selection strategy parameters
        mu = lambda / 2; // number of parents/points for recombination
        logMu2 = FastMath.log(mu + 0.5);
        weights = DoubleIndex.log(DoubleIndex.sequence(1, mu, 1)).scalarMultiply(-1).scalarAdd(logMu2);
        double sumw = 0;
        double sumwq = 0;
        for (int i = 0; i < mu; i++) {
            double w = weights.getEntry(i, 0);
            sumw += w;
            sumwq += w * w;
        }
        weights = weights.scalarMultiply(1 / sumw);
        mueff = sumw * sumw / sumwq; // variance-effectiveness of sum w_i x_i

        // initialize dynamic strategy parameters and constants
        cc = (4 + mueff / dimension) /
                (dimension + 4 + 2 * mueff / dimension);
        cs = (mueff + 2) / (dimension + mueff + 3.);
        damps = (1 + 2 * FastMath.max(0, FastMath.sqrt((mueff - 1) /
                (dimension + 1)) - 1)) *
                FastMath.max(0.3,
                        1 - dimension / (1e-6 + maxIterations)) + cs; // minor increment
        ccov1 = 2 / ((dimension + 1.3) * (dimension + 1.3) + mueff);
        ccovmu = FastMath.min(1 - ccov1, 2 * (mueff - 2 + 1 / mueff) /
                ((dimension + 2) * (dimension + 2) + mueff));
        ccov1Sep = FastMath.min(1, ccov1 * (dimension + 1.5) / 3);
        ccovmuSep = FastMath.min(1 - ccov1, ccovmu * (dimension + 1.5) / 3);
        chiN = FastMath.sqrt(dimension) *
                (1 - 1 / ((double) 4 * dimension) + 1 / ((double) 21 * dimension * dimension));
    }

    /**
     * Update of the evolution paths ps and pc.
     *
     * @param zmean Weighted row matrix of the gaussian random numbers generating
     *              the current offspring.
     * @param xold  xmean matrix of the previous generation.
     * @return hsig flag indicating a small correction.
     */
    private boolean updateEvolutionPaths(RealMatrix zmean, RealMatrix xold) {
        ps = ps.scalarMultiply(1 - cs).add(
                B.multiply(zmean).scalarMultiply(
                        FastMath.sqrt(cs * (2 - cs) * mueff)));
        normps = ps.getFrobeniusNorm();
        final boolean hsig = normps /
                FastMath.sqrt(1 - FastMath.pow(1 - cs, 2 * iterations)) /
                chiN < 1.4 + 2 / ((double) dimension + 1);
        pc = pc.scalarMultiply(1 - cc);
        if (hsig) {
            pc = pc.add(xmean.subtract(xold).scalarMultiply(FastMath.sqrt(cc * (2 - cc) * mueff) / sigma));
        }
        return hsig;
    }

    /**
     * Update of the covariance matrix C for diagonalOnly > 0
     *
     * @param hsig    Flag indicating a small correction.
     * @param bestArz Fitness-sorted matrix of the gaussian random values of the
     *                current offspring.
     */
    private void updateCovarianceDiagonalOnly(boolean hsig,
                                              final RealMatrix bestArz) {
        // minor correction if hsig==false
        double oldFac = hsig ? 0 : ccov1Sep * cc * (2 - cc);
        oldFac += 1 - ccov1Sep - ccovmuSep;
        diagC = diagC.scalarMultiply(oldFac) // regard old matrix
                .add(DoubleIndex.square(pc).scalarMultiply(ccov1Sep)) // plus rank one update
                .add((DoubleIndex.times(diagC, DoubleIndex.square(bestArz).multiply(weights))) // plus rank mu update
                        .scalarMultiply(ccovmuSep));
        diagD = DoubleIndex.sqrt(diagC); // replaces eig(C)
        if (diagonalOnly > 1 &&
                iterations > diagonalOnly) {
            // full covariance matrix from now on
            diagonalOnly = 0;
            B = DoubleIndex.eye(dimension, dimension);
            BD = DoubleIndex.diag(diagD);
            C = DoubleIndex.diag(diagC);
        }
    }

    /**
     * Update of the covariance matrix C.
     *
     * @param hsig    Flag indicating a small correction.
     * @param bestArx Fitness-sorted matrix of the argument vectors producing the
     *                current offspring.
     * @param arz     Unsorted matrix containing the gaussian random values of the
     *                current offspring.
     * @param arindex Indices indicating the fitness-order of the current offspring.
     * @param xold    xmean matrix of the previous generation.
     */
    private void updateCovariance(boolean hsig, final RealMatrix bestArx,
                                  final RealMatrix arz, final int[] arindex,
                                  final RealMatrix xold) {
        double negccov = 0;
        if (ccov1 + ccovmu > 0) {
            final RealMatrix arpos = bestArx.subtract(DoubleIndex.repmat(xold, 1, mu))
                    .scalarMultiply(1 / sigma); // mu difference vectors
            final RealMatrix roneu = pc.multiply(pc.transpose())
                    .scalarMultiply(ccov1); // rank one update
            // minor correction if hsig==false
            double oldFac = hsig ? 0 : ccov1 * cc * (2 - cc);
            oldFac += 1 - ccov1 - ccovmu;
            if (isActiveCMA) {
                // Adapt covariance matrix C active CMA
                negccov = (1 - ccovmu) * 0.25 * mueff /
                        (FastMath.pow(dimension + 2, 1.5) + 2 * mueff);
                // keep at least 0.66 in all directions, small popsize are most
                // critical
                final double negminresidualvariance = 0.66;
                // where to make up for the variance loss
                final double negalphaold = 0.5;
                // prepare vectors, compute negative updating matrix Cneg
                final int[] arReverseIndex = DoubleIndex.reverse(arindex);
                RealMatrix arzneg = DoubleIndex.selectColumns(arz, MathArrays.copyOf(arReverseIndex, mu));
                RealMatrix arnorms = DoubleIndex.sqrt(DoubleIndex.sumRows(DoubleIndex.square(arzneg)));
                final int[] idxnorms = sortedIndices(arnorms.getRow(0));
                final RealMatrix arnormsSorted = DoubleIndex.selectColumns(arnorms, idxnorms);
                final int[] idxReverse = DoubleIndex.reverse(idxnorms);
                final RealMatrix arnormsReverse = DoubleIndex.selectColumns(arnorms, idxReverse);
                arnorms = DoubleIndex.divide(arnormsReverse, arnormsSorted);
                final int[] idxInv = DoubleIndex.inverse(idxnorms);
                final RealMatrix arnormsInv = DoubleIndex.selectColumns(arnorms, idxInv);
                // check and set learning rate negccov
                final double negcovMax = (1 - negminresidualvariance) /
                        DoubleIndex.square(arnormsInv).multiply(weights).getEntry(0, 0);
                if (negccov > negcovMax) {
                    negccov = negcovMax;
                }
                arzneg = DoubleIndex.times(arzneg, DoubleIndex.repmat(arnormsInv, dimension, 1));
                final RealMatrix artmp = BD.multiply(arzneg);
                final RealMatrix Cneg = artmp.multiply(DoubleIndex.diag(weights)).multiply(artmp.transpose());
                oldFac += negalphaold * negccov;
                C = C.scalarMultiply(oldFac)
                        .add(roneu) // regard old matrix
                        .add(arpos.scalarMultiply( // plus rank one update
                                ccovmu + (1 - negalphaold) * negccov) // plus rank mu update
                                .multiply(DoubleIndex.times(DoubleIndex.repmat(weights, 1, dimension),
                                        arpos.transpose())))
                        .subtract(Cneg.scalarMultiply(negccov));
            } else {
                // Adapt covariance matrix C - nonactive
                C = C.scalarMultiply(oldFac) // regard old matrix
                        .add(roneu) // plus rank one update
                        .add(arpos.scalarMultiply(ccovmu) // plus rank mu update
                                .multiply(DoubleIndex.times(DoubleIndex.repmat(weights, 1, dimension),
                                        arpos.transpose())));
            }
        }
        updateBD(negccov);
    }

    /**
     * Update B and D from C.
     *
     * @param negccov Negative covariance factor.
     */
    private void updateBD(double negccov) {
        if (ccov1 + ccovmu + negccov > 0 &&
                (iterations % 1. / (ccov1 + ccovmu + negccov) / dimension / 10.) < 1) {
            // to achieve O(N^2)
            C = DoubleIndex.triu(C, 0).add(DoubleIndex.triu(C, 1).transpose());
            // enforce symmetry to prevent complex numbers
            final EigenDecomposition eig = new EigenDecomposition(C);
            B = eig.getV(); // eigen decomposition, B==normalized eigenvectors
            D = eig.getD();
            diagD = DoubleIndex.diag(D);
            if (DoubleIndex.min(diagD) <= 0) {
                for (int i = 0; i < dimension; i++) {
                    if (diagD.getEntry(i, 0) < 0) {
                        diagD.setEntry(i, 0, 0);
                    }
                }
                final double tfac = DoubleIndex.max(diagD) / 1e14;
                C = C.add(DoubleIndex.eye(dimension, dimension).scalarMultiply(tfac));
                diagD = diagD.add(DoubleIndex.ones(dimension, 1).scalarMultiply(tfac));
            }
            if (DoubleIndex.max(diagD) > 1e14 * DoubleIndex.min(diagD)) {
                final double tfac = DoubleIndex.max(diagD) / 1e14 - DoubleIndex.min(diagD);
                C = C.add(DoubleIndex.eye(dimension, dimension).scalarMultiply(tfac));
                diagD = diagD.add(DoubleIndex.ones(dimension, 1).scalarMultiply(tfac));
            }
            diagC = DoubleIndex.diag(C);
            diagD = DoubleIndex.sqrt(diagD); // D contains standard deviations now
            BD = DoubleIndex.times(B, DoubleIndex.repmat(diagD.transpose(), dimension, 1)); // O(n^2)
        }
    }

    /**
     * Sorts fitness values.
     *
     * @param doubles Array of values to be sorted.
     * @return a sorted array of indices pointing into doubles.
     */
    private int[] sortedIndices(final double[] doubles) {
        final DoubleIndex[] dis = new DoubleIndex[doubles.length];
        for (int i = 0; i < doubles.length; i++) {
            dis[i] = new DoubleIndex(doubles[i], i);
        }
        Arrays.sort(dis);
        final int[] indices = new int[doubles.length];
        for (int i = 0; i < doubles.length; i++) {
            indices[i] = dis[i].index;
        }
        return indices;
    }

    /**
     * Get range of values.
     *
     * @param samples Array of valuePenaltyPairs to get range from.
     * @return a double equal to maximum value minus minimum value.
     */
    private double valueRange(final List<Sample> samples) {
        double max = Double.NEGATIVE_INFINITY;
        double min = Double.MAX_VALUE;
        for (Sample vpPair : samples) {
            if (vpPair.getY() > max) {
                max = vpPair.getY();
            }
            if (vpPair.getY() < min) {
                min = vpPair.getY();
            }
        }
        return max - min;
    }

    /**
     * Used to sort fitness values. Sorting is always in lower value first
     * order.
     */
    private static class DoubleIndex implements Comparable<DoubleIndex> {
        /**
         * Value to compare.
         */
        private final double value;
        /**
         * Index into sorted array.
         */
        private final int index;

        /**
         * @param value Value to compare.
         * @param index Index into sorted array.
         */
        DoubleIndex(double value, int index) {
            this.value = value;
            this.index = index;
        }

        /**
         * @param m Input matrix
         * @return Matrix representing the element-wise logarithm of m.
         */
        private static RealMatrix log(final RealMatrix m) {
            final double[][] d = new double[m.getRowDimension()][m.getColumnDimension()];
            for (int r = 0; r < m.getRowDimension(); r++) {
                for (int c = 0; c < m.getColumnDimension(); c++) {
                    d[r][c] = FastMath.log(m.getEntry(r, c));
                }
            }
            return new Array2DRowRealMatrix(d, false);
        }

        /**
         * @param m Input matrix.
         * @return Matrix representing the element-wise square root of m.
         */
        private static RealMatrix sqrt(final RealMatrix m) {
            final double[][] d = new double[m.getRowDimension()][m.getColumnDimension()];
            for (int r = 0; r < m.getRowDimension(); r++) {
                for (int c = 0; c < m.getColumnDimension(); c++) {
                    d[r][c] = FastMath.sqrt(m.getEntry(r, c));
                }
            }
            return new Array2DRowRealMatrix(d, false);
        }

        /**
         * @param m Input matrix.
         * @return Matrix representing the element-wise square of m.
         */
        private static RealMatrix square(final RealMatrix m) {
            final double[][] d = new double[m.getRowDimension()][m.getColumnDimension()];
            for (int r = 0; r < m.getRowDimension(); r++) {
                for (int c = 0; c < m.getColumnDimension(); c++) {
                    double e = m.getEntry(r, c);
                    d[r][c] = e * e;
                }
            }
            return new Array2DRowRealMatrix(d, false);
        }

        /**
         * @param m Input matrix 1.
         * @param n Input matrix 2.
         * @return the matrix where the elements of m and n are element-wise multiplied.
         */
        static RealMatrix times(final RealMatrix m, final RealMatrix n) {
            final double[][] d = new double[m.getRowDimension()][m.getColumnDimension()];
            for (int r = 0; r < m.getRowDimension(); r++) {
                for (int c = 0; c < m.getColumnDimension(); c++) {
                    d[r][c] = m.getEntry(r, c) * n.getEntry(r, c);
                }
            }
            return new Array2DRowRealMatrix(d, false);
        }

        /**
         * @param m Input matrix 1.
         * @param n Input matrix 2.
         * @return Matrix where the elements of m and n are element-wise divided.
         */
        private static RealMatrix divide(final RealMatrix m, final RealMatrix n) {
            final double[][] d = new double[m.getRowDimension()][m.getColumnDimension()];
            for (int r = 0; r < m.getRowDimension(); r++) {
                for (int c = 0; c < m.getColumnDimension(); c++) {
                    d[r][c] = m.getEntry(r, c) / n.getEntry(r, c);
                }
            }
            return new Array2DRowRealMatrix(d, false);
        }

        /**
         * @param m    Input matrix.
         * @param cols Columns to select.
         * @return Matrix representing the selected columns.
         */
        private static RealMatrix selectColumns(final RealMatrix m, final int[] cols) {
            final double[][] d = new double[m.getRowDimension()][cols.length];
            for (int r = 0; r < m.getRowDimension(); r++) {
                for (int c = 0; c < cols.length; c++) {
                    d[r][c] = m.getEntry(r, cols[c]);
                }
            }
            return new Array2DRowRealMatrix(d, false);
        }

        /**
         * @param m Input matrix.
         * @param k Diagonal position.
         * @return Upper triangular part of matrix.
         */
        private static RealMatrix triu(final RealMatrix m, int k) {
            final double[][] d = new double[m.getRowDimension()][m.getColumnDimension()];
            for (int r = 0; r < m.getRowDimension(); r++) {
                for (int c = 0; c < m.getColumnDimension(); c++) {
                    d[r][c] = r <= c - k ? m.getEntry(r, c) : 0;
                }
            }
            return new Array2DRowRealMatrix(d, false);
        }

        /**
         * @param m Input matrix.
         * @return Row matrix representing the sums of the rows.
         */
        private static RealMatrix sumRows(final RealMatrix m) {
            final double[][] d = new double[1][m.getColumnDimension()];
            for (int c = 0; c < m.getColumnDimension(); c++) {
                double sum = 0;
                for (int r = 0; r < m.getRowDimension(); r++) {
                    sum += m.getEntry(r, c);
                }
                d[0][c] = sum;
            }
            return new Array2DRowRealMatrix(d, false);
        }

        /**
         * @param m Input matrix.
         * @return the diagonal n-by-n matrix if m is a column matrix or the column
         * matrix representing the diagonal if m is a n-by-n matrix.
         */
        private static RealMatrix diag(final RealMatrix m) {
            if (m.getColumnDimension() == 1) {
                final double[][] d = new double[m.getRowDimension()][m.getRowDimension()];
                for (int i = 0; i < m.getRowDimension(); i++) {
                    d[i][i] = m.getEntry(i, 0);
                }
                return new Array2DRowRealMatrix(d, false);
            } else {
                final double[][] d = new double[m.getRowDimension()][1];
                for (int i = 0; i < m.getColumnDimension(); i++) {
                    d[i][0] = m.getEntry(i, i);
                }
                return new Array2DRowRealMatrix(d, false);
            }
        }

        /**
         * Copies a column from m1 to m2.
         *
         * @param m1   Source matrix.
         * @param col1 Source column.
         * @param m2   Target matrix.
         * @param col2 Target column.
         */
        private static void copyColumn(final RealMatrix m1, int col1,
                                       RealMatrix m2, int col2) {
            for (int i = 0; i < m1.getRowDimension(); i++) {
                m2.setEntry(i, col2, m1.getEntry(i, col1));
            }
        }

        /**
         * @param n Number of rows.
         * @param m Number of columns.
         * @return n-by-m matrix filled with 1.
         */
        private static RealMatrix ones(int n, int m) {
            final double[][] d = new double[n][m];
            for (int r = 0; r < n; r++) {
                Arrays.fill(d[r], 1);
            }
            return new Array2DRowRealMatrix(d, false);
        }

        /**
         * @param n Number of rows.
         * @param m Number of columns.
         * @return n-by-m matrix of 0 values out of diagonal, and 1 values on
         * the diagonal.
         */
        private static RealMatrix eye(int n, int m) {
            final double[][] d = new double[n][m];
            for (int r = 0; r < n; r++) {
                if (r < m) {
                    d[r][r] = 1;
                }
            }
            return new Array2DRowRealMatrix(d, false);
        }

        /**
         * @param n Number of rows.
         * @param m Number of columns.
         * @return n-by-m matrix of zero values.
         */
        private static RealMatrix zeros(int n, int m) {
            return new Array2DRowRealMatrix(n, m);
        }

        /**
         * @param mat Input matrix.
         * @param n   Number of row replicates.
         * @param m   Number of column replicates.
         * @return a matrix which replicates the input matrix in both directions.
         */
        private static RealMatrix repmat(final RealMatrix mat, int n, int m) {
            final int rd = mat.getRowDimension();
            final int cd = mat.getColumnDimension();
            final double[][] d = new double[n * rd][m * cd];
            for (int r = 0; r < n * rd; r++) {
                for (int c = 0; c < m * cd; c++) {
                    d[r][c] = mat.getEntry(r % rd, c % cd);
                }
            }
            return new Array2DRowRealMatrix(d, false);
        }

        /**
         * @param start Start value.
         * @param end   End value.
         * @param step  Step size.
         * @return a sequence as column matrix.
         */
        private static RealMatrix sequence(double start, double end, double step) {
            final int size = (int) ((end - start) / step + 1);
            final double[][] d = new double[size][1];
            double value = start;
            for (int r = 0; r < size; r++) {
                d[r][0] = value;
                value += step;
            }
            return new Array2DRowRealMatrix(d, false);
        }

        /**
         * @param m Input matrix.
         * @return the maximum of the matrix element values.
         */
        private static double max(final RealMatrix m) {
            double max = -Double.MAX_VALUE;
            for (int r = 0; r < m.getRowDimension(); r++) {
                for (int c = 0; c < m.getColumnDimension(); c++) {
                    double e = m.getEntry(r, c);
                    if (max < e) {
                        max = e;
                    }
                }
            }
            return max;
        }

        /**
         * @param m Input matrix.
         * @return the minimum of the matrix element values.
         */
        private static double min(final RealMatrix m) {
            double min = Double.MAX_VALUE;
            for (int r = 0; r < m.getRowDimension(); r++) {
                for (int c = 0; c < m.getColumnDimension(); c++) {
                    double e = m.getEntry(r, c);
                    if (min > e) {
                        min = e;
                    }
                }
            }
            return min;
        }

        /**
         * @param m Input array.
         * @return the maximum of the array values.
         */
        private static double max(final double[] m) {
            double max = -Double.MAX_VALUE;
            for (double v : m) {
                if (max < v) {
                    max = v;
                }
            }
            return max;
        }

        /**
         * @param m Input array.
         * @return the minimum of the array values.
         */
        private static double min(final double[] m) {
            double min = Double.MAX_VALUE;
            for (double v : m) {
                if (min > v) {
                    min = v;
                }
            }
            return min;
        }

        /**
         * @param indices Input index array.
         * @return the inverse of the mapping defined by indices.
         */
        private static int[] inverse(final int[] indices) {
            final int[] inverse = new int[indices.length];
            for (int i = 0; i < indices.length; i++) {
                inverse[indices[i]] = i;
            }
            return inverse;
        }

        /**
         * @param indices Input index array.
         * @return the indices in inverse order (last is first).
         */
        private static int[] reverse(final int[] indices) {
            final int[] reverse = new int[indices.length];
            for (int i = 0; i < indices.length; i++) {
                reverse[i] = indices[indices.length - i - 1];
            }
            return reverse;
        }

        /**
         * @param size Length of random array.
         * @return an array of Gaussian random numbers.
         */
        private static double[] randn(int size) {
            final double[] randn = new double[size];
            for (int i = 0; i < size; i++) {
                randn[i] = Generator.RANDOM.nextGaussian();
            }
            return randn;
        }

        /**
         * @param size    Number of rows.
         * @param popSize Population size.
         * @return a 2-dimensional matrix of Gaussian random numbers.
         */
        static RealMatrix randn1(int size, int popSize) {
            final double[][] d = new double[size][popSize];
            for (int r = 0; r < size; r++) {
                for (int c = 0; c < popSize; c++) {
                    d[r][c] = Generator.RANDOM.nextGaussian();
                }
            }
            return new Array2DRowRealMatrix(d, false);
        }

        /**
         * {@inheritDoc}
         */
        public int compareTo(DoubleIndex o) {
            return Double.compare(value, o.value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object other) {

            if (this == other) {
                return true;
            }

            if (other instanceof DoubleIndex) {
                return Double.compare(value, ((DoubleIndex) other).value) == 0;
            }

            return false;
        }
    }
}



























































