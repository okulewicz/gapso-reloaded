package pl.edu.pw.mini.gapso.optimizer.move;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.junit.Assert;
import org.junit.Test;

public class CMAESApacheTest {

    @Test
    public void verifyMatrixInverstion() {
        RealMatrix xmean = MatrixUtils.createColumnRealMatrix(new double[]{
                -1.9912179345333625,
                2.0184205031009594
        });
        RealMatrix BD = MatrixUtils.createRealMatrix(new double[][]{
                {1.0288879405862101, 0.04204759154743019},
                {0.07312045085172937, -0.5916574551977396}
        });
        RealMatrix arzAccumulator = MatrixUtils.createColumnRealMatrix(new double[]{
                0.755161663154593,
                -1.5917988890618884
        });
        double sigma = 1.741096960892957;
        RealMatrix arxAccumulator = MatrixUtils.createRealMatrix(2, 1);
        arxAccumulator.setColumnMatrix(0, xmean.add(BD.multiply(arzAccumulator.getColumnMatrix(0))
                .scalarMultiply(sigma))); // m + sig * Normal(0,C)
        final double[] expectedArx = {-0.7549600135908514, 3.754324539870181};
        Assert.assertArrayEquals(
                expectedArx,
                arxAccumulator.getColumn(0),
                1e-4
        );

        RealMatrix invertedBD = MatrixUtils.inverse(BD);
        RealMatrix inputX = MatrixUtils.createColumnRealMatrix(expectedArx);
        RealMatrix resultZ = inputX.subtract(xmean).scalarMultiply(1 / sigma).preMultiply(invertedBD);
        Assert.assertArrayEquals(arzAccumulator.getColumn(0), resultZ.getColumn(0), 1e-4);
    }
}