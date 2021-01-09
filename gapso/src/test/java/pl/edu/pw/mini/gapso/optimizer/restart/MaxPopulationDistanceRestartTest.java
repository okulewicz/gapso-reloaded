package pl.edu.pw.mini.gapso.optimizer.restart;

import org.junit.Assert;
import org.junit.Test;
import pl.edu.pw.mini.gapso.function.ConvexSquareFunction;
import pl.edu.pw.mini.gapso.function.FunctionWhiteBox;
import pl.edu.pw.mini.gapso.optimizer.Particle;
import pl.edu.pw.mini.gapso.sample.SingleSample;
import pl.edu.pw.mini.gapso.sample.UpdatableSample;

import java.util.ArrayList;
import java.util.List;

public class MaxPopulationDistanceRestartTest {

    public static final double BORDERLINE_CASE_THRESHOLD = 1e-8;

    @Test
    public void shouldBeRestarted() {
        FunctionWhiteBox function = new ConvexSquareFunction();
        UpdatableSample globalBest = new UpdatableSample(
                new SingleSample(new double[2], Double.POSITIVE_INFINITY)
        );

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

        RestartObserver observer = new MaxPopulationDistanceRestart(BORDERLINE_CASE_THRESHOLD);

        List<Particle> particles = new ArrayList<>();
        Assert.assertTrue(observer.shouldBeRestarted(particles));
        for (int i = 0; i < samples.length; ++i) {
            Particle particle = new Particle(
                    samples[i],
                    function,
                    globalBest
            );
            particles.add(particle);
            Assert.assertEquals(restarts[i], observer.shouldBeRestarted(particles));
        }
    }
}