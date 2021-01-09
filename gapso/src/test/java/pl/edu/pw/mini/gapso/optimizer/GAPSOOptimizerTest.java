package pl.edu.pw.mini.gapso.optimizer;

import org.junit.Assert;
import org.junit.Test;
import pl.edu.pw.mini.gapso.function.ConvexSquareFunction;
import pl.edu.pw.mini.gapso.function.FunctionWhiteBox;
import pl.edu.pw.mini.gapso.generator.initializer.RandomIntializer;
import pl.edu.pw.mini.gapso.optimization.move.DEBest1Bin;
import pl.edu.pw.mini.gapso.optimization.move.Move;
import pl.edu.pw.mini.gapso.optimizer.restart.MaxPopulationDistanceRestart;
import pl.edu.pw.mini.gapso.sample.Sample;

public class GAPSOOptimizerTest {

    @Test
    public void optimize() {
        GAPSOOptimizer optimizer = new GAPSOOptimizer(10, 1000, new
                Move[]{new DEBest1Bin(1.2, 1.0)}, new RandomIntializer(),
                new MaxPopulationDistanceRestart(1e-8));
        FunctionWhiteBox function = new ConvexSquareFunction();
        Sample optimumEstimation = optimizer.optimize(function);
        Assert.assertNotNull(optimumEstimation);
        Assert.assertArrayEquals(function.getOptimumLocation(), optimumEstimation.getX(), 1e-4);
    }
}