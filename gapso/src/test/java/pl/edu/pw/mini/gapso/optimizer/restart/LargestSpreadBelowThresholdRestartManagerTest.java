package pl.edu.pw.mini.gapso.optimizer.restart;

import org.junit.Test;
import pl.edu.pw.mini.gapso.function.ConvexSquareFunction;
import pl.edu.pw.mini.gapso.function.FunctionWhiteBox;
import pl.edu.pw.mini.gapso.optimizer.restart.threshold.LargestSpreadBelowThresholdRestartManager;

public class LargestSpreadBelowThresholdRestartManagerTest {

    @Test
    public void shouldBeRestarted() {

        FunctionWhiteBox function = new ConvexSquareFunction();

        boolean[] restarts = new boolean[]{
                false,
                true,
                true,
                false,
                false,
                false
        };

        RestartManager observer = new LargestSpreadBelowThresholdRestartManager(RestartScheme.BORDERLINE_CASE_THRESHOLD);
        RestartScheme.ValidateRestartManagerAgainstRestartsScheme(function, RestartScheme.samples, restarts, observer);
    }

}