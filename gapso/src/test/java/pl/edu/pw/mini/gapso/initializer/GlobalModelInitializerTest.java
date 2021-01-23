package pl.edu.pw.mini.gapso.initializer;

import org.junit.Assert;
import org.junit.Test;
import pl.edu.pw.mini.gapso.function.ChaoticFunction;
import pl.edu.pw.mini.gapso.function.RastriginFunction;
import pl.edu.pw.mini.gapso.optimizer.RandomOptimizer;

public class GlobalModelInitializerTest {

    @Test
    public void assessSamplingAbility() {
        GlobalModelInitializer modelInitializer = new GlobalModelInitializer();
        RastriginFunction rastriginFunction = new RastriginFunction(10);
        RandomOptimizer randomOptimizer = new RandomOptimizer();
        Assert.assertFalse(modelInitializer.canSample());
        modelInitializer.registerObjectsWithOptimizer(randomOptimizer);
        randomOptimizer.optimize(rastriginFunction);
        Assert.assertFalse(modelInitializer.canSample());
        modelInitializer.resetInitializer(false);
        Assert.assertTrue(modelInitializer.canSample());

        modelInitializer.resetInitializer(true);
        modelInitializer.registerObjectsWithOptimizer(randomOptimizer);
        Assert.assertFalse(modelInitializer.canSample());
        ChaoticFunction chaoticFunction = new ChaoticFunction(10);
        randomOptimizer.optimize(chaoticFunction);
        Assert.assertFalse(modelInitializer.canSample());
        modelInitializer.resetInitializer(false);
        Assert.assertFalse(modelInitializer.canSample());
    }
}