package pl.edu.pw.mini.gapso.optimizer;

import org.junit.Assert;
import org.junit.Test;
import pl.edu.pw.mini.gapso.function.ConvexSquareFunction;
import pl.edu.pw.mini.gapso.function.FunctionWhiteBox;
import pl.edu.pw.mini.gapso.sample.UpdatableSample;

import java.util.ArrayList;
import java.util.List;

public class ParticleTest {

    @Test
    public void getGlobalBest() {
        FunctionWhiteBox function = new ConvexSquareFunction();
        UpdatableSample globalBest = UpdatableSample.generateInitialSample(function.getDimension());
        double[] optimum = function.getOptimumLocation();
        double optimumValue = function.getValue(optimum);
        double[] otherPoint = new double[]{0.0, 1.0};
        double otherPointValue = function.getValue(otherPoint);
        List<Particle> particleList = new ArrayList<>();
        Particle.IndexContainer indexContainer = new Particle.IndexContainer();
        Particle p1 = new Particle(otherPoint, function, globalBest, indexContainer, particleList);
        Assert.assertEquals(otherPointValue, globalBest.getY(), 0.0);
        Assert.assertArrayEquals(otherPoint, globalBest.getX(), 0.0);

        Particle p2 = new Particle(optimum, function, globalBest, indexContainer, particleList);
        Assert.assertEquals(optimumValue, globalBest.getY(), 0.0);
        Assert.assertArrayEquals(optimum, globalBest.getX(), 0.0);
    }
}