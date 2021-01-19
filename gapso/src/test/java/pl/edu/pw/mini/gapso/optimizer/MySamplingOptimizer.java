package pl.edu.pw.mini.gapso.optimizer;

import pl.edu.pw.mini.gapso.function.Function;
import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.sample.Sampler;

import java.util.ArrayList;
import java.util.List;

public class MySamplingOptimizer extends SamplingOptimizer {
    public List<Sampler> samplerList = new ArrayList<>();

    public Function wrapFunction(Function function) {
        return createSamplingWrapper(function, samplerList);
    }

    @Override
    public Sample optimize(Function function) {
        return null;
    }

    @Override
    public void registerSampler(Sampler sampler) {
        samplerList.add(sampler);
    }

}
