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

    public static final double eps = 1e-4;
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
    public static final double CS = 0.4462;
    public static final double MUEFF = 2.0286;
    public static final double[] WEIGHTS = new double[]{0.6370, 0.2846, 0.0784};
    public static final double CMU = 0.0579;
    public static final int DIMENSION = 2;
    public static final int MU = 3;

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
        Assert.assertEquals(1.598706564883426, invertedsqrtC.getEntry(0, 0), eps);
        Assert.assertEquals(-0.222744145974292, invertedsqrtC.getEntry(0, 1), eps);
        Assert.assertEquals(-0.222744145974292, invertedsqrtC.getEntry(1, 0), eps);
        Assert.assertEquals(1.746431161979163, invertedsqrtC.getEntry(1, 1), eps);
    }

    @Test
    public void computeArtMp() {
        double sigma = 1.022732253709786;
        List<Sample> samples = Arrays.asList(new Sample[]{
                new SingleSample(new double[]{0.356030369603726, -1.461074127538027}, 79.583239253897560),
                new SingleSample(new double[]{0.596487058591412, -0.724192700829926}, 79.785269869538453),
                new SingleSample(new double[]{-0.485502333563371, -1.051100002728961}, 80.036262825168222),
                new SingleSample(new double[]{-0.637245821647295, -0.805953208187199}, 80.395275035957141),
                new SingleSample(new double[]{0.884924504496003, -0.202585206254164}, 80.790107261787725),
                new SingleSample(new double[]{-1.201107832327802, -2.874105294920569}, 84.542985460866348)
        });
        final RealMatrix artMp = CMAESLike.computeArtMp(MU, DIMENSION,
                sigma, XOLD.getColumn(0), samples);

        Assert.assertEquals(0.459604579294877, artMp.getEntry(0, 0), eps);
        Assert.assertEquals(0.694716641240191, artMp.getEntry(1, 0), eps);
        Assert.assertEquals(-0.363223389721042, artMp.getEntry(2, 0), eps);
        Assert.assertEquals(-0.220828051684211, artMp.getEntry(0, 1), eps);
        Assert.assertEquals(0.499674723147804, artMp.getEntry(1, 1), eps);
        Assert.assertEquals(0.180033584703959, artMp.getEntry(2, 1), eps);
    }

    @Test
    public void computePS() {
        double sigma = 1.022732253709786;
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

        RealMatrix normalizedMeanDiff = CMAESLike.computeNormalizedDiff(sigma, XOLD, XMEAN);
        RealMatrix resultPs = CMAESLike.computePS(CS, MUEFF, invsqrtC, ps, normalizedMeanDiff);
        Assert.assertEquals(0.768314451230276, resultPs.getEntry(0, 0), eps);
        Assert.assertEquals(0.238735133838149, resultPs.getEntry(1, 0), eps);
    }
}