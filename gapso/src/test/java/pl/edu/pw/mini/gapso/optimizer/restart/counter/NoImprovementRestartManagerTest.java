package pl.edu.pw.mini.gapso.optimizer.restart.counter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pl.edu.pw.mini.gapso.bounds.Bounds;
import pl.edu.pw.mini.gapso.bounds.SimpleBounds;
import pl.edu.pw.mini.gapso.configuration.MoveConfiguration;
import pl.edu.pw.mini.gapso.function.Function;
import pl.edu.pw.mini.gapso.optimizer.Particle;
import pl.edu.pw.mini.gapso.optimizer.SamplingOptimizer;
import pl.edu.pw.mini.gapso.optimizer.Swarm;
import pl.edu.pw.mini.gapso.optimizer.move.Move;
import pl.edu.pw.mini.gapso.optimizer.restart.counter.NoImprovementRestartManager.Configuration;

import java.util.Arrays;
import java.util.List;

public class NoImprovementRestartManagerTest {

    public static final int EVALUATIONS_PER_DIMENSION_LIMIT = 10;
    public static final int DIM = 2;
    private Function f;
    private NoImprovementRestartManager manager;
    private Configuration conf;
    private Move move;

    @Test
    public void shouldBeRestarted() {
        for (int k = 0; k < 2; ++k) {
            manager.reset();
            Swarm swarm = new Swarm();
            new Particle(new double[]{0, 0}, f, swarm);
            new Particle(new double[]{0, 1}, f, swarm);
            for (int j = 0; j < 2; ++j) {
                for (int i = 0; i < EVALUATIONS_PER_DIMENSION_LIMIT; ++i) {
                    Assert.assertFalse(k + " " + j + " " + i, manager.shouldBeRestarted(swarm.getParticles()));
                }
                Assert.assertTrue(manager.shouldBeRestarted(swarm.getParticles()));
                swarm.getParticles().get(0).move(move);
            }
            Assert.assertTrue(manager.shouldBeRestarted(swarm.getParticles()));
        }
    }

    @Before
    public void init() {
        move = new Move(new MoveConfiguration("", 1, 1, false, null)) {
            @Override
            public double[] getNext(Particle currentParticle, List<Particle> particleList) {
                double[] x = new double[currentParticle.getBest().getX().length];
                Arrays.fill(x, -1);
                return x;
            }

            @Override
            public void registerObjectsWithOptimizer(SamplingOptimizer samplingOptimizer) {

            }

            @Override
            public void resetState(int particleCount) {

            }

            @Override
            public void registerPersonalImprovement(double deltaY) {

            }

            @Override
            public void newIteration() {

            }
        };
        conf = new Configuration(EVALUATIONS_PER_DIMENSION_LIMIT);
        manager = new NoImprovementRestartManager(conf);
        f = new Function() {
            @Override
            protected double computeValue(double[] x) {
                if (x[0] == -1)
                    return -1;
                return 0;
            }

            @Override
            public boolean isTargetReached() {
                return false;
            }

            @Override
            public int getDimension() {
                return DIM;
            }

            @Override
            public Bounds getBounds() {
                return new SimpleBounds(
                        new double[]{-1, -1},
                        new double[]{1, 1}
                );
            }
        };
    }
}