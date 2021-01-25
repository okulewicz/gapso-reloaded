package pl.edu.pw.mini.gapso.optimizer.restart;

import org.junit.Test;
import pl.edu.pw.mini.gapso.function.ConvexSquareFunction;
import pl.edu.pw.mini.gapso.function.FunctionWhiteBox;
import pl.edu.pw.mini.gapso.optimizer.restart.threshold.SmallestSpreadBelowThresholdRestartManager;

public class SmallestSpreadBelowThresholdRestartManagerTest {

    @Test
    public void shouldBeRestarted() {
        FunctionWhiteBox function = new ConvexSquareFunction();

        boolean[] restarts = new boolean[]{
                false,
                false,
                false,
                false,
                false,
                false
        };

        RestartManager observer = new SmallestSpreadBelowThresholdRestartManager(RestartScheme.BORDERLINE_CASE_THRESHOLD);
        RestartScheme.ValidateRestartManagerAgainstRestartsScheme(function, RestartScheme.samples, restarts, observer);
    }
}