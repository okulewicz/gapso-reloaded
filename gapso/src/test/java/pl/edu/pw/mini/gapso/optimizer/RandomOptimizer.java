package pl.edu.pw.mini.gapso.optimizer;

import pl.edu.pw.mini.gapso.function.Function;
import pl.edu.pw.mini.gapso.initializer.Initializer;
import pl.edu.pw.mini.gapso.initializer.RandomInitializer;
import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.sample.SingleSample;
import pl.edu.pw.mini.gapso.sample.UpdatableSample;
import pl.edu.pw.mini.gapso.sample.sampler.Sampler;

import java.util.ArrayList;
import java.util.List;

public class RandomOptimizer extends SamplingOptimizer {
    List<Sampler> samplerList = new ArrayList<>();

    @Override
    public void registerSampler(Sampler sampler) {
        samplerList.add(sampler);
    }

    @Override
    public void registerSuccessSampler(Sampler archive) {

    }

    @Override
    public Sample optimize(Function function) {
        function = createSamplingWrapper(function, samplerList);
        Initializer randomInitialize = new RandomInitializer();
        double[] someSampleLocation = randomInitialize.getNextSample(function.getBounds());
        Sample someSample = new SingleSample(someSampleLocation, function.getValue(someSampleLocation));
        UpdatableSample bestSample = new UpdatableSample(someSample);
        for (int i = 0; i < 120 * function.getDimension() * function.getDimension(); ++i) {
            someSampleLocation = randomInitialize.getNextSample(function.getBounds());
            someSample = new SingleSample(someSampleLocation, function.getValue(someSampleLocation));
            if (bestSample.getY() > someSample.getY()) {
                bestSample.updateSample(someSample);
            }
        }
        return bestSample;
    }
}
