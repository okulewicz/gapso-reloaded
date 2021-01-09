package pl.edu.pw.mini.gapso.optimization.move;

import org.junit.Assert;
import org.junit.Test;

public class DEBest1BinTest {

    @Test
    public void getDESample() {
        double[] current = new double[]{3.0, 5.0};
        double[] best = new double[]{1.0, -1.0};
        double[] diff1 = new double[]{2.0, 0.0};
        double[] diff2 = new double[]{1.0, -3.0};
        double[] sample;

        DEBest1Bin move = new DEBest1Bin();
        for (double scale = 0.0; scale <= 1.0; scale += 0.1) {
            sample = move.getDESample(current, best, diff1, diff2, scale, 1.0);
            for (int dimIdx = 0; dimIdx < sample.length; ++dimIdx) {
                Assert.assertEquals(best[dimIdx] + scale * (diff1[dimIdx] - diff2[dimIdx])
                        , sample[dimIdx], 0.0);
            }
        }

        for (double scale = 0.0; scale <= 1.0; scale += 0.1) {
            sample = move.getDESample(current, best, diff1, diff2, scale, 0.0);
            int bestParts = 0;
            int currentParts = 0;
            for (int dimIdx = 0; dimIdx < sample.length; ++dimIdx) {
                if (sample[dimIdx] == best[dimIdx] + scale * (diff1[dimIdx] - diff2[dimIdx])) {
                    bestParts++;
                } else if (sample[dimIdx] == current[dimIdx]) {
                    currentParts++;
                }
            }
            Assert.assertEquals(1, bestParts);
            Assert.assertEquals(sample.length - 1, currentParts);
        }
    }
}