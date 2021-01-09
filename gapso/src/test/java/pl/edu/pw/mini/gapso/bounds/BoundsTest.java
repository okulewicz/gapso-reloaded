package pl.edu.pw.mini.gapso.bounds;

import org.junit.Assert;
import org.junit.Test;

public class BoundsTest {

    @Test
    public void isWithin() {
        Bounds bounds = new Bounds() {
            @Override
            public double[] getLower() {
                return new double[]{-1.0, -1.0};
            }

            @Override
            public double[] getUpper() {
                return new double[]{1.0, 1.0};
            }
        };

        for (double x = -1.0; x <= 1.0; x += 1.0) {
            for (double y = -1.0; y <= 1.0; y += 1.0) {
                Assert.assertTrue(bounds.contain(new double[]{x, y}));
            }
        }
    }
}