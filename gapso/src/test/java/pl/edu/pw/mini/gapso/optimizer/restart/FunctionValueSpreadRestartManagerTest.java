package pl.edu.pw.mini.gapso.optimizer.restart;

import org.junit.Test;
import pl.edu.pw.mini.gapso.function.Function;
import pl.edu.pw.mini.gapso.function.SingleStepFunction;
import pl.edu.pw.mini.gapso.sample.UpdatableSample;

public class FunctionValueSpreadRestartManagerTest {

    @Test
    public void shouldBeRestarted() {
        Function function = new SingleStepFunction();

        UpdatableSample globalBest = UpdatableSample.generateInitialSample(function.getDimension());

        boolean[] restarts = new boolean[]{
                false,
                true,
                true,
                true,
                false
        };

        RestartManager observer = new FunctionValueSpreadRestartManager(RestartScheme.BORDERLINE_CASE_THRESHOLD);
        RestartScheme.ValidateRestartManagerAgainstRestartsScheme(function, globalBest, RestartScheme.samples, restarts, observer);
    }
}