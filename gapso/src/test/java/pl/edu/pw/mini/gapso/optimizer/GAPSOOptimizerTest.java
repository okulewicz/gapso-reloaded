package pl.edu.pw.mini.gapso.optimizer;

import org.junit.Assert;
import org.junit.Test;
import pl.edu.pw.mini.gapso.bounds.BoundsManager;
import pl.edu.pw.mini.gapso.bounds.ResetAllBoundsManager;
import pl.edu.pw.mini.gapso.configuration.BoundsManagerConfiguration;
import pl.edu.pw.mini.gapso.configuration.MoveConfiguration;
import pl.edu.pw.mini.gapso.configuration.MoveManagerConfiguration;
import pl.edu.pw.mini.gapso.function.ConvexSquareFunction;
import pl.edu.pw.mini.gapso.function.FunctionWhiteBox;
import pl.edu.pw.mini.gapso.initializer.RandomInitializer;
import pl.edu.pw.mini.gapso.optimizer.move.DEBest1Bin;
import pl.edu.pw.mini.gapso.optimizer.move.MoveManager;
import pl.edu.pw.mini.gapso.optimizer.move.SHADE;
import pl.edu.pw.mini.gapso.optimizer.restart.threshold.SmallestSpreadBelowThresholdRestartManager;
import pl.edu.pw.mini.gapso.sample.Sample;

import java.util.ArrayList;
import java.util.List;

public class GAPSOOptimizerTest {

    public static void optimizeWithMoves(MoveManager moveManager) {
        BoundsManagerConfiguration boundsManagerConfiguration =
                new BoundsManagerConfiguration(ResetAllBoundsManager.NAME, null);
        BoundsManager boundsManager = new ResetAllBoundsManager(boundsManagerConfiguration);
        GAPSOOptimizer optimizer = new GAPSOOptimizer(10, 1.0, Integer.MAX_VALUE, 1000,
                moveManager, new RandomInitializer(),
                new SmallestSpreadBelowThresholdRestartManager(1e-8),
                boundsManager);
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
        List<MoveConfiguration> movesConfiguration = new ArrayList<>();
        movesConfiguration.add(moveConfiguration);
        MoveManagerConfiguration moveManagerConfiguration = new MoveManagerConfiguration(
                false,
                0,
                0,
                false,
                false,
                movesConfiguration
        );
        MoveManager manager = moveManagerConfiguration.getMoveManager();
        optimizeWithMoves(manager);
    }

    @Test
    public void optimizeWithSHADE() {
        SHADE.SHADEConfiguration configuration = new SHADE.SHADEConfiguration(
                0.5, 0.9, 0.11, 6, 2.0);
        MoveConfiguration moveConfiguration =
                new MoveConfiguration(SHADE.NAME, 1000.0, 1, true, configuration);
        List<MoveConfiguration> movesConfiguration = new ArrayList<>();
        movesConfiguration.add(moveConfiguration);
        MoveManagerConfiguration moveManagerConfiguration = new MoveManagerConfiguration(
                false,
                0,
                0,
                false,
                false,
                movesConfiguration
        );
        MoveManager manager = moveManagerConfiguration.getMoveManager();
        optimizeWithMoves(manager);
    }
}