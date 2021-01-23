package pl.edu.pw.mini.gapso.optimizer.move;

import org.junit.Assert;
import org.junit.Test;
import pl.edu.pw.mini.gapso.configuration.MoveConfiguration;
import pl.edu.pw.mini.gapso.function.ConvexSquareFunction;
import pl.edu.pw.mini.gapso.function.Function;
import pl.edu.pw.mini.gapso.function.FunctionWhiteBox;
import pl.edu.pw.mini.gapso.initializer.RandomInitializer;
import pl.edu.pw.mini.gapso.model.FullSquareModel;
import pl.edu.pw.mini.gapso.optimizer.MySamplingOptimizer;
import pl.edu.pw.mini.gapso.optimizer.Particle;
import pl.edu.pw.mini.gapso.optimizer.Swarm;

import java.util.ArrayList;
import java.util.List;

public class GlobalModelTest {

    @Test
    public void getNext() {

        List<ModelMove.ModelParameters> parameters = new ArrayList<>();
        parameters.add(new ModelMove.ModelParameters(
                FullSquareModel.NAME,
                2
        ));
        MoveConfiguration moveConfigurationModel = new MoveConfiguration(
                LocalBestModel.NAME,
                0.0,
                1,
                false,
                new ModelMove.ModelSequenceParameters(
                        parameters,
                        SamplesClusteringType.NONE
                )
        );
        MySamplingOptimizer samplingOptimizer = new MySamplingOptimizer();
        GlobalModel globalModel = new GlobalModel(moveConfigurationModel);
        FunctionWhiteBox function = new ConvexSquareFunction();
        RandomInitializer generator = new RandomInitializer();
        globalModel.registerObjectsWithOptimizer(samplingOptimizer);
        Function wrappedFunction = samplingOptimizer.wrapFunction(function);
        for (int i = 0; i < 100; ++i) {
            Assert.assertEquals(i, samplingOptimizer.samplerList.get(0).getSamplesCount());
            double[] sample = generator.getNextSample(wrappedFunction.getBounds());
            wrappedFunction.getValue(sample);
        }
        Swarm swarm = new Swarm();
        double[] sample = generator.getNextSample(wrappedFunction.getBounds());
        Particle p = new Particle(sample, wrappedFunction, swarm);
        double[] modelResult = globalModel.getNext(p, swarm.getParticles());
        Assert.assertNotEquals(function.getOptimumLocation()[0], modelResult[0], 1e-8);
        Assert.assertNotEquals(function.getOptimumLocation()[1], modelResult[1], 1e-8);
        modelResult = globalModel.getNext(p, swarm.getParticles());
        Assert.assertArrayEquals(function.getOptimumLocation(), modelResult, 1e-8);
        modelResult = globalModel.getNext(p, swarm.getParticles());
        Assert.assertNotEquals(function.getOptimumLocation()[0], modelResult[0], 1e-8);
        Assert.assertNotEquals(function.getOptimumLocation()[1], modelResult[1], 1e-8);
        modelResult = globalModel.getNext(p, swarm.getParticles());
        Assert.assertArrayEquals(function.getOptimumLocation(), modelResult, 1e-8);
    }
}