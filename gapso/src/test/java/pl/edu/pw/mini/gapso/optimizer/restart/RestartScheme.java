package pl.edu.pw.mini.gapso.optimizer.restart;

import org.junit.Assert;
import pl.edu.pw.mini.gapso.function.Function;
import pl.edu.pw.mini.gapso.optimizer.Particle;
import pl.edu.pw.mini.gapso.optimizer.Swarm;

public class RestartScheme {
    public static final double BORDERLINE_CASE_THRESHOLD = 1e-8;

    public static double[][] samples = new double[][]{
            {0.0, 0.0},
            {BORDERLINE_CASE_THRESHOLD / 10, BORDERLINE_CASE_THRESHOLD / 10},
            {BORDERLINE_CASE_THRESHOLD, BORDERLINE_CASE_THRESHOLD / 10},
            {BORDERLINE_CASE_THRESHOLD, BORDERLINE_CASE_THRESHOLD},
            {BORDERLINE_CASE_THRESHOLD * 10, 4.0}
    };

    public static void ValidateRestartManagerAgainstRestartsScheme(Function function, double[][] samples, boolean[] restarts, RestartManager observer) {
        Swarm swarm = new Swarm();
        Assert.assertTrue(observer.shouldBeRestarted(swarm.getParticles()));
        for (
                int i = 0;
                i < samples.length; ++i) {
            new Particle(
                    samples[i],
                    function,
                    swarm
            );
            final boolean actual = observer.shouldBeRestarted(swarm.getParticles());
            Assert.assertEquals(restarts[i], actual);
        }
    }

}
