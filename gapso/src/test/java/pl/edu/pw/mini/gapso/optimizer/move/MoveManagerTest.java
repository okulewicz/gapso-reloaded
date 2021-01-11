package pl.edu.pw.mini.gapso.optimizer.move;

import org.junit.Test;
import pl.edu.pw.mini.gapso.configuration.MoveConfiguration;

public class MoveManagerTest {

    @Test
    public void generateMoveSequence() {
        MoveConfiguration moveConfigurationModel = new MoveConfiguration(
                LocalBestModel.NAME,
                0.0,
                1,
                false
        );
        MoveConfiguration moveConfigurationDE = new MoveConfiguration(
                DEBest1Bin.NAME,
                1000.0,
                1,
                true,
                new DEBest1Bin.DEBest1BinConfiguration(1.0, 1.0)
        );

    }

}