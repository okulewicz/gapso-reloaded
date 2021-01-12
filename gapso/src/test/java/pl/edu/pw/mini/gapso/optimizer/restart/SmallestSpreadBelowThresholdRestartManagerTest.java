package pl.edu.pw.mini.gapso.optimizer.restart;

import org.junit.Test;
import pl.edu.pw.mini.gapso.function.ConvexSquareFunction;
import pl.edu.pw.mini.gapso.function.FunctionWhiteBox;
import pl.edu.pw.mini.gapso.sample.UpdatableSample;

public class SmallestSpreadBelowThresholdRestartManagerTest {

    @Test
    public void shouldBeRestarted() {
        FunctionWhiteBox function = new ConvexSquareFunction();
        UpdatableSample globalBest = UpdatableSample.generateInitialSample(function.getDimension());

        boolean[] restarts = new boolean[]{
                false,
                true,
                true,
                false,
                false
        };

        RestartManager observer = new SmallestSpreadBelowThresholdRestartManager(RestartScheme.BORDERLINE_CASE_THRESHOLD);
        RestartScheme.ValidateRestartManagerAgainstRestartsScheme(function, globalBest, RestartScheme.samples, restarts, observer);
    }
}