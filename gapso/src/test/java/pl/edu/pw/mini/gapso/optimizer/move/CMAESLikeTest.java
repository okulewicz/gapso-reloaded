package pl.edu.pw.mini.gapso.optimizer.move;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.junit.Assert;
import org.junit.Test;
import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.sample.SingleSample;

import java.util.Arrays;
import java.util.List;

public class CMAESLikeTest {

    public static final double EPS = 1e-4;
    public static final RealMatrix XOLD = MatrixUtils.createColumnRealMatrix(
            new double[]{
                    -0.114022057593862,
                    -1.235226156556693
            }
    );
    public static final RealMatrix XMEAN = MatrixUtils.createColumnRealMatrix(
            new double[]{
                    0.358491823316475,
                    -1.219242878279915
            }
    );
    public static final List<Sample> SAMPLES = Arrays.asList(new Sample[]{
            new SingleSample(new double[]{0.356030369603726, -1.461074127538027}, 79.583239253897560),
            new SingleSample(new double[]{0.596487058591412, -0.724192700829926}, 79.785269869538453),
            new SingleSample(new double[]{-0.485502333563371, -1.051100002728961}, 80.036262825168222),
            new SingleSample(new double[]{-0.637245821647295, -0.805953208187199}, 80.395275035957141),
            new SingleSample(new double[]{0.884924504496003, -0.202585206254164}, 80.790107261787725),
            new SingleSample(new double[]{-1.201107832327802, -2.874105294920569}, 84.542985460866348)
    });

    public static final double CS = 0.4462;
    public static final double CMU = 0.0579;
    public static final double C1 = 0.1548;
    public static final double CC = 0.6246;
    public static final double MUEFF = 2.0286;
    public static final double[] WEIGHTS = new double[]{0.6370, 0.2846, 0.0784};
    public static final int DIMENSION = 2;
    public static final int MU = 3;
    public static final double INITIAL_SIGMA = 1.022732253709786;
    public static final RealMatrix NORMALIZED_MEAN_DIFF = CMAESLike.computeNormalizedDiff(INITIAL_SIGMA, XOLD, XMEAN);

    @Test
    public void computeCInvert() {
        RealMatrix B = MatrixUtils.createRealMatrix(
                new double[][]{
                        {0.585342647072830, -0.810786029429327},
                        {-0.810786029429327, -0.585342647072830}
                });
        RealVector D = MatrixUtils.createRealVector(
                new double[]{0.524317832369420, 0.695459807358389
                });
        RealMatrix invertedsqrtC = CMAESLike.computeSQRTCInvert(B, D);
        Assert.assertEquals(1.598706564883426, invertedsqrtC.getEntry(0, 0), EPS);
        Assert.assertEquals(-0.222744145974292, invertedsqrtC.getEntry(0, 1), EPS);
        Assert.assertEquals(-0.222744145974292, invertedsqrtC.getEntry(1, 0), EPS);
        Assert.assertEquals(1.746431161979163, invertedsqrtC.getEntry(1, 1), EPS);
    }

    @Test
    public void computeArtMp() {
        final RealMatrix artMp = CMAESLike.computeArtMp(MU, DIMENSION,
                INITIAL_SIGMA, XOLD, SAMPLES);

        Assert.assertEquals(0.459604579294877, artMp.getEntry(0, 0), EPS);
        Assert.assertEquals(0.694716641240191, artMp.getEntry(1, 0), EPS);
        Assert.assertEquals(-0.363223389721042, artMp.getEntry(2, 0), EPS);
        Assert.assertEquals(-0.220828051684211, artMp.getEntry(0, 1), EPS);
        Assert.assertEquals(0.499674723147804, artMp.getEntry(1, 1), EPS);
        Assert.assertEquals(0.180033584703959, artMp.getEntry(2, 1), EPS);
    }

    @Test
    public void computePS() {
        RealMatrix invsqrtC = MatrixUtils.createRealMatrix(
                new double[][]{
                        {1.555265894630989, -0.184678521878679},
                        {-0.184678521878679, 1.567864796108877}
                }
        );
        RealMatrix ps = MatrixUtils.createRealMatrix(
                new double[][]{
                        {-0.145219813639669},
                        {0.561336332018881}
                }
        );

        RealMatrix resultPs = CMAESLike.computePS(CS, MUEFF, invsqrtC, ps, NORMALIZED_MEAN_DIFF);
        Assert.assertEquals(0.768314451230276, resultPs.getEntry(0, 0), EPS);
        Assert.assertEquals(0.238735133838149, resultPs.getEntry(1, 0), EPS);
    }

    @Test
    public void computeC() {
        double hsig = 1.0;
        RealMatrix pc = MatrixUtils.createColumnRealMatrix(new double[]{
                0.603405624921662,
                0.201682607696034
        });
        RealMatrix C = MatrixUtils.createRealMatrix(new double[][]{
                {0.431131200889151, 0.099773362383646},
                {0.099773362383646, 0.424324590407316}
        });
        RealMatrix resultC = CMAESLike.computeC(SAMPLES, hsig, CC, C1, pc, C, MU, DIMENSION, WEIGHTS, CMU, INITIAL_SIGMA, XOLD);
        Assert.assertEquals(0.412139397126973, resultC.getEntry(0, 0), EPS);
        Assert.assertEquals(0.099072616163092, resultC.getEntry(0, 1), EPS);
        Assert.assertEquals(0.099072616163092, resultC.getEntry(1, 0), EPS);
        Assert.assertEquals(0.346434135864562, resultC.getEntry(1, 1), EPS);


        /* eigenvalues are given in different order and eigenvectors have a different form */
        RealMatrix expectedB = MatrixUtils.createRealMatrix(
                new double[][]{
                        {0.810786029429327, 0.585342647072830},
                        {0.585342647072830, -0.810786029429327}
                });
        RealVector expectedD = MatrixUtils.createRealVector(
                new double[]{0.695459807358389, 0.524317832369420
                });

        CMAESLike.EigenParts parts = CMAESLike.computeEigenDecomposition(resultC);
        Assert.assertEquals(expectedD.getEntry(0), parts.D.getEntry(0), EPS);
        Assert.assertEquals(expectedD.getEntry(1), parts.D.getEntry(1), EPS);

        Assert.assertEquals(expectedB.getEntry(0, 0), parts.B.getEntry(0, 0), EPS);
        Assert.assertEquals(expectedB.getEntry(0, 1), parts.B.getEntry(0, 1), EPS);
        Assert.assertEquals(expectedB.getEntry(1, 0), parts.B.getEntry(1, 0), EPS);
        Assert.assertEquals(expectedB.getEntry(1, 1), parts.B.getEntry(1, 1), EPS);

        RealMatrix invertedsqrtC = CMAESLike.computeSQRTCInvert(parts.B, parts.D);
        Assert.assertEquals(1.598706564883426, invertedsqrtC.getEntry(0, 0), EPS);
        Assert.assertEquals(-0.222744145974292, invertedsqrtC.getEntry(0, 1), EPS);
        Assert.assertEquals(-0.222744145974292, invertedsqrtC.getEntry(1, 0), EPS);
        Assert.assertEquals(1.746431161979163, invertedsqrtC.getEntry(1, 1), EPS);

    }

    @Test
    public void computePc() {
        double hsig = 1.0;
        RealMatrix pc = MatrixUtils.createColumnRealMatrix(new double[]{
                -0.017298888659407,
                0.482232719430822
        });
        RealMatrix C = MatrixUtils.createRealMatrix(new double[][]{
                {0.431131200889151, 0.099773362383646},
                {0.099773362383646, 0.424324590407316}
        });
        RealMatrix resultPc = CMAESLike.computePC(NORMALIZED_MEAN_DIFF, hsig, pc, CC, MUEFF);
        Assert.assertEquals(0.603405624921662, resultPc.getEntry(0, 0), EPS);
        Assert.assertEquals(0.201682607696034, resultPc.getEntry(1, 0), EPS);
    }

    @Test
    public void computeNewDistr() {
        double[] normals = new double[]{-1.8339, 0.5377};
        double[] xmean = new double[]{-1.3058, -1.5179};
        double sigma = 3.2683;
        double[][] barray = new double[][]{
                {0.9171, 0.3988},
                {0.3988, -0.9171}
        };
        double[] darray = new double[]{
                1.1065, 0.8878,
        };
        double[] newx = CMAESLike.scaleAndRotateVector(normals, MatrixUtils.createColumnRealMatrix(xmean),
                MatrixUtils.createRealMatrix(barray), MatrixUtils.createRealVector(darray),
                sigma);
        Assert.assertEquals(-6.7654, newx[0], 1e-3);
        Assert.assertEquals(-5.5931, newx[1], 1e-3);
    }
}