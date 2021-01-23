package pl.edu.pw.mini.gapso.optimizer;

import org.junit.Assert;
import org.junit.Test;
import pl.edu.pw.mini.gapso.function.ConvexSquareFunction;
import pl.edu.pw.mini.gapso.function.FunctionWhiteBox;

public class ParticleTest {

    @Test
    public void getGlobalBest() {
        FunctionWhiteBox function = new ConvexSquareFunction();
        double[] optimum = function.getOptimumLocation();
        double optimumValue = function.getValue(optimum);
        double[] otherPoint = new double[]{0.0, 1.0};
        double otherPointValue = function.getValue(otherPoint);
        Swarm swarm = new Swarm();
        Particle p1 = new Particle(otherPoint, function, swarm);
        Assert.assertEquals(otherPointValue, swarm.getGlobalBest().getY(), 0.0);
        Assert.assertArrayEquals(otherPoint, swarm.getGlobalBest().getX(), 0.0);

        Particle p2 = new Particle(optimum, function, swarm);
        Assert.assertEquals(optimumValue, swarm.getGlobalBest().getY(), 0.0);
        Assert.assertArrayEquals(optimum, swarm.getGlobalBest().getX(), 0.0);
    }
}