package pl.edu.pw.mini.gapso.optimizer.move;

import pl.edu.pw.mini.gapso.configuration.MoveConfiguration;
import pl.edu.pw.mini.gapso.optimizer.Particle;
import pl.edu.pw.mini.gapso.optimizer.SamplingOptimizer;
import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.sample.sampler.AllSamplesSampler;

import java.util.List;

public class GlobalModel extends ModelMove {
    public static final String NAME = "GlobalModel";
    public static final int SAMPLES_DIM_SQ_FACTOR = 50;
    AllSamplesSampler _sampler;

    public GlobalModel(MoveConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected List<Sample> getSamples(Particle currentParticle, List<Particle> particleList) {
        final int dim = currentParticle.getFunction().getDimension();
        int samplesCount = Math.min(SAMPLES_DIM_SQ_FACTOR * dim * dim, _sampler.getSamplesCount());
        return _sampler.getSamples(samplesCount);
    }

    @Override
    public void registerObjectsWithOptimizer(SamplingOptimizer samplingOptimizer) {
        samplingOptimizer.registerSampler(_sampler);
    }

    @Override
    public void resetState(int particleCount) {
        _sampler = new AllSamplesSampler();
        resetWeight();
    }

    @Override
    public void registerPersonalImprovement(double deltaY) {
        //DO NOTHING ON PURPOSE
    }

    @Override
    public void newIteration() {
        //DO NOTHING ON PURPOSE
    }

    @Override
    public void registerSamplingResult(double y) {
        //DO NOTHING ON PURPOSE
    }
}
