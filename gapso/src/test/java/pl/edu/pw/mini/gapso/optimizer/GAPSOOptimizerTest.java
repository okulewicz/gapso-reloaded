package pl.edu.pw.mini.gapso.optimizer;

import org.junit.Assert;
import org.junit.Test;
import pl.edu.pw.mini.gapso.configuration.MoveConfiguration;
import pl.edu.pw.mini.gapso.function.ConvexSquareFunction;
import pl.edu.pw.mini.gapso.function.FunctionWhiteBox;
import pl.edu.pw.mini.gapso.initializer.RandomInitializer;
import pl.edu.pw.mini.gapso.optimizer.move.DEBest1Bin;
import pl.edu.pw.mini.gapso.optimizer.move.Move;
import pl.edu.pw.mini.gapso.optimizer.restart.MinSpreadInDimensionsRestartManager;
import pl.edu.pw.mini.gapso.sample.Sample;

public class GAPSOOptimizerTest {

    public static void optimizeWithMoves(Move[] moves) {
        GAPSOOptimizer optimizer = new GAPSOOptimizer(10, 1000,
                moves, new RandomInitializer(),
                new MinSpreadInDimensionsRestartManager(1e-8));
        FunctionWhiteBox function = new ConvexSquareFunction();
        Sample optimumEstimation = optimizer.optimize(function);
        Assert.assertNotNull(optimumEstimation);
        Assert.assertArrayEquals(function.getOptimumLocation(), optimumEstimation.getX(), 1e-4);
        optimizer = new GAPSOOptimizer();
        Assert.assertNotNull(optimizer);
        optimumEstimation = optimizer.optimize(function);
        Assert.assertNotNull(optimumEstimation);
    }

    @Test
    public void optimize() {
        DEBest1Bin.DEBest1BinConfiguration configuration =
                new DEBest1Bin.DEBest1BinConfiguration(1.2, 1.0);
        MoveConfiguration moveConfiguration =
                new MoveConfiguration(DEBest1Bin.NAME, 1000.0, 1, true, configuration);
        optimizeWithMoves(new Move[]{new DEBest1Bin(moveConfiguration)});
    }
}