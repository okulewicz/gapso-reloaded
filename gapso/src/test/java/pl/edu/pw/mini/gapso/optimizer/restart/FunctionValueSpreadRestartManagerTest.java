package pl.edu.pw.mini.gapso.optimizer.restart;

import org.junit.Test;
import pl.edu.pw.mini.gapso.function.Function;
import pl.edu.pw.mini.gapso.function.SingleStepFunction;
import pl.edu.pw.mini.gapso.optimizer.restart.threshold.FunctionValueSpreadRestartManager;

public class FunctionValueSpreadRestartManagerTest {

    @Test
    public void shouldBeRestarted() {
        Function function = new SingleStepFunction();

        boolean[] restarts = new boolean[]{
                false,
                true,
                true,
                true,
                false
        };

        RestartManager observer = new FunctionValueSpreadRestartManager(RestartScheme.BORDERLINE_CASE_THRESHOLD);
        RestartScheme.ValidateRestartManagerAgainstRestartsScheme(function, RestartScheme.samples, restarts, observer);
    }
}