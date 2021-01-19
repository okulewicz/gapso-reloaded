package pl.edu.pw.mini.gapso.optimizer.move;

import org.junit.Assert;
import org.junit.Test;
import pl.edu.pw.mini.gapso.configuration.MoveConfiguration;
import pl.edu.pw.mini.gapso.model.SimpleSquareModel;
import pl.edu.pw.mini.gapso.optimizer.GAPSOOptimizerTest;
import pl.edu.pw.mini.gapso.optimizer.move.ModelMove.ModelParameters;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MoveManagerTest {

    @Test
    public void generateMoveSequence() {
        final int expectedLBM = 2;
        List<ModelParameters> parameters = new ArrayList<>();
        parameters.add(new ModelParameters(
                SimpleSquareModel.NAME,
                1
        ));
        MoveConfiguration moveConfigurationModel = new MoveConfiguration(
                LocalBestModel.NAME,
                0.0,
                expectedLBM,
                false,
                new ModelMove.ModelSequenceParameters(
                        parameters
                )
        );
        final int expectedMinDE = 1;
        MoveConfiguration moveConfigurationDE = new MoveConfiguration(
                DEBest1Bin.NAME,
                1000.0,
                expectedMinDE,
                true,
                new DEBest1Bin.DEBest1BinConfiguration(1.0, 1.0)
        );
        final Move deMove = moveConfigurationDE.getMove();
        final Move lbmMove = moveConfigurationModel.getMove();
        MoveManager moveManager = new MoveManager(new Move[]{
                deMove,
                lbmMove
        });

        try {
            moveManager.generateMoveSequence(expectedLBM);
            Assert.fail("Impossible sequence to generate");
        } catch (IllegalArgumentException ex) {
            ex.getMessage();
        }
        testSequence(expectedLBM, moveManager, 3);
        testSequence(expectedLBM, moveManager, 10);

        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < 100; ++i) {
            List<Move> moveSequence = moveManager.generateMoveSequence(3);
            indexes.add(moveSequence.indexOf(deMove));
        }
        Set<Integer> uniqueIndexes = new HashSet<>(indexes);
        Assert.assertTrue("Should be more than one index position in multiple tries", uniqueIndexes.size() > 1);

        GAPSOOptimizerTest.optimizeWithMoves(new Move[]{deMove, lbmMove});
    }

    private void testSequence(int expectedLBM, MoveManager moveManager, int size) {
        List<Move> moveSequence = moveManager.generateMoveSequence(size);
        long countDE = moveSequence.stream().filter(move -> move instanceof DEBest1Bin).count();
        long countLBM = moveSequence.stream().filter(move -> move instanceof LocalBestModel).count();
        Assert.assertEquals(size - expectedLBM, countDE);
        Assert.assertEquals(expectedLBM, countLBM);
    }

}