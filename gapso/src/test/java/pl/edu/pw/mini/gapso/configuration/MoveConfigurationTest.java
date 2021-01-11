package pl.edu.pw.mini.gapso.configuration;

import org.junit.Assert;
import org.junit.Test;

public class MoveConfigurationTest {

    @Test
    public void getMove() {
        MoveConfiguration noSuchMove = new MoveConfiguration(
                "NoSuchMove",
                0.0,
                1,
                false
        );
        try {
            noSuchMove.getMove();
            Assert.fail("Non existent move generated");
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }
}