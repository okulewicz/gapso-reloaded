package pl.edu.pw.mini.gapso.optimization.move;

import org.junit.Assert;
import org.junit.Test;
import pl.edu.pw.mini.gapso.function.ConvexSeparableSquareFunction;
import pl.edu.pw.mini.gapso.function.FunctionWhiteBox;
import pl.edu.pw.mini.gapso.optimizer.Particle;
import pl.edu.pw.mini.gapso.optimizer.move.DEBest1Bin;
import pl.edu.pw.mini.gapso.sample.UpdatableSample;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DEBest1BinTest {

    @Test
    public void getDESample() {
        double[] current = new double[]{3.0, 5.0};
        double[] best = new double[]{1.0, -1.0};
        double[] diff1 = new double[]{2.0, 0.0};
        double[] diff2 = new double[]{1.0, -3.0};
        double[] sample;

        for (double scale = 0.0; scale <= 1.0; scale += 0.1) {
            sample = DEBest1Bin.getDESample(current, best, diff1, diff2, scale, 1.0);
            for (int dimIdx = 0; dimIdx < sample.length; ++dimIdx) {
                Assert.assertEquals(best[dimIdx] + scale * (diff1[dimIdx] - diff2[dimIdx])
                        , sample[dimIdx], 0.0);
            }
        }

        for (double scale = 0.0; scale <= 1.0; scale += 0.1) {
            sample = DEBest1Bin.getDESample(current, best, diff1, diff2, scale, 0.0);
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

    @Test
    public void getBestIndexAndSelfIndex() {
        FunctionWhiteBox squareFunction = new ConvexSeparableSquareFunction();
        double[] opt = squareFunction.getOptimumLocation();
        double[] optLeft = Arrays.copyOf(opt, opt.length);
        optLeft[0] -= 1.0;
        double[] optRight = Arrays.copyOf(opt, opt.length);
        optRight[0] += 1.0;
        double[] optTop = Arrays.copyOf(opt, opt.length);
        optTop[1] += 1.0;
        double[] optBottom = Arrays.copyOf(opt, opt.length);
        optBottom[1] -= 1.0;
        UpdatableSample globalBest = UpdatableSample.generateInitialSample(squareFunction.getDimension());

        Particle.IndexContainer indexContainer = new Particle.IndexContainer();
        List<Particle> particles = new ArrayList<>();

        Particle particleTop = new Particle(optTop, squareFunction, globalBest, indexContainer, particles);
        Assert.assertEquals(0, particleTop.getGlobalBestIndex());
        Assert.assertEquals(0, particleTop.getIndex());

        Particle particleBottom = new Particle(optBottom, squareFunction, globalBest, indexContainer, particles);
        Assert.assertEquals(0, particleTop.getGlobalBestIndex());
        Assert.assertEquals(0, particleBottom.getGlobalBestIndex());
        Assert.assertEquals(0, particleTop.getIndex());
        Assert.assertEquals(1, particleBottom.getIndex());

        Particle particleLeft = new Particle(optLeft, squareFunction, globalBest, indexContainer, particles);
        Assert.assertEquals(2, particleTop.getGlobalBestIndex());
        Assert.assertEquals(2, particleBottom.getGlobalBestIndex());
        Assert.assertEquals(2, particleLeft.getGlobalBestIndex());
        Assert.assertEquals(0, particleTop.getIndex());
        Assert.assertEquals(1, particleBottom.getIndex());
        Assert.assertEquals(2, particleLeft.getIndex());

        Particle particleRight = new Particle(optRight, squareFunction, globalBest, indexContainer, particles);
        Assert.assertEquals(2, particleTop.getGlobalBestIndex());
        Assert.assertEquals(2, particleBottom.getGlobalBestIndex());
        Assert.assertEquals(2, particleLeft.getGlobalBestIndex());
        Assert.assertEquals(2, particleRight.getGlobalBestIndex());
        Assert.assertEquals(0, particleTop.getIndex());
        Assert.assertEquals(1, particleBottom.getIndex());
        Assert.assertEquals(2, particleLeft.getIndex());
        Assert.assertEquals(3, particleRight.getIndex());


        Particle particleOpt = new Particle(opt, squareFunction, globalBest, indexContainer, particles);
        Assert.assertEquals(4, particleTop.getGlobalBestIndex());
        Assert.assertEquals(4, particleBottom.getGlobalBestIndex());
        Assert.assertEquals(4, particleLeft.getGlobalBestIndex());
        Assert.assertEquals(4, particleRight.getGlobalBestIndex());
        Assert.assertEquals(4, particleOpt.getGlobalBestIndex());
        Assert.assertEquals(0, particleTop.getIndex());
        Assert.assertEquals(1, particleBottom.getIndex());
        Assert.assertEquals(2, particleLeft.getIndex());
        Assert.assertEquals(3, particleRight.getIndex());
        Assert.assertEquals(4, particleOpt.getIndex());
    }

}