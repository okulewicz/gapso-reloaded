package pl.edu.pw.mini.gapso.optimizer.restart;

import org.junit.Assert;
import org.junit.Test;
import pl.edu.pw.mini.gapso.function.ConvexSquareFunction;
import pl.edu.pw.mini.gapso.function.FunctionWhiteBox;
import pl.edu.pw.mini.gapso.optimizer.Particle;
import pl.edu.pw.mini.gapso.sample.UpdatableSample;

import java.util.ArrayList;
import java.util.List;

public class MinSpreadInDimensionsRestartManagerTest {

    public static final double BORDERLINE_CASE_THRESHOLD = 1e-8;

    @Test
    public void shouldBeRestarted() {
        FunctionWhiteBox function = new ConvexSquareFunction();
        UpdatableSample globalBest = UpdatableSample.generateInitialSample(function.getDimension());

        double[][] samples = new double[][]{
                {0.0, 1.0},
                {BORDERLINE_CASE_THRESHOLD / 10, 2.0},
                {BORDERLINE_CASE_THRESHOLD, 3.0},
                {BORDERLINE_CASE_THRESHOLD * 10, 4.0}
        };
        boolean[] restarts = new boolean[]{
                false,
                true,
                false,
                false
        };

        RestartManager observer = new MinSpreadInDimensionsRestartManager(BORDERLINE_CASE_THRESHOLD);

        Particle.IndexContainer globalBestIndexContainer = new Particle.IndexContainer();
        List<Particle> particles = new ArrayList<>();
        Assert.assertTrue(observer.shouldBeRestarted(particles));
        for (int i = 0; i < samples.length; ++i) {
            new Particle(
                    samples[i],
                    function,
                    globalBest,
                    globalBestIndexContainer,
                    particles
            );
            Assert.assertEquals(restarts[i], observer.shouldBeRestarted(particles));
        }
    }
}