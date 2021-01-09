package pl.edu.pw.mini.gapso.optimizer;

import org.junit.Assert;
import org.junit.Test;
import pl.edu.pw.mini.gapso.function.ConvexSquareFunction;
import pl.edu.pw.mini.gapso.function.FunctionWhiteBox;
import pl.edu.pw.mini.gapso.sample.SingleSample;
import pl.edu.pw.mini.gapso.sample.UpdatableSample;

public class ParticleTest {

    @Test
    public void getGlobalBest() {
        UpdatableSample globalBest = new UpdatableSample(
                new SingleSample(new double[2], Double.POSITIVE_INFINITY)
        );
        FunctionWhiteBox function = new ConvexSquareFunction();
        double[] optimum = function.getOptimumLocation();
        double optimumValue = function.getValue(optimum);
        double[] otherPoint = new double[]{0.0, 1.0};
        double otherPointValue = function.getValue(otherPoint);
        Particle p1 = new Particle(otherPoint, function, globalBest);
        Assert.assertEquals(otherPointValue, globalBest.getY(), 0.0);
        Assert.assertArrayEquals(otherPoint, globalBest.getX(), 0.0);

        Particle p2 = new Particle(optimum, function, globalBest);
        Assert.assertEquals(optimumValue, globalBest.getY(), 0.0);
        Assert.assertArrayEquals(optimum, globalBest.getX(), 0.0);
    }
}