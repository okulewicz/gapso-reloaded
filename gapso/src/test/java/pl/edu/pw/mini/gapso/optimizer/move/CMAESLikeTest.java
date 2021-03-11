package pl.edu.pw.mini.gapso.optimizer.move;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.junit.Assert;
import org.junit.Test;

public class CMAESLikeTest {

    public static final double eps = 1e-4;

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
}