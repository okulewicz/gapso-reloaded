package pl.edu.pw.mini.gapso.optimizer.move;

import pl.edu.pw.mini.gapso.configuration.MoveConfiguration;
import pl.edu.pw.mini.gapso.model.FullSquareModel;
import pl.edu.pw.mini.gapso.model.SimpleSquareModel;
import pl.edu.pw.mini.gapso.optimizer.Particle;
import pl.edu.pw.mini.gapso.optimizer.SamplingOptimizer;
import pl.edu.pw.mini.gapso.sample.AllSamplesSampler;
import pl.edu.pw.mini.gapso.sample.Sample;

import java.util.List;

public class GlobalModel extends ModelMove {
    public static final String NAME = "GlobalModel";
    AllSamplesSampler _sampler;

    public GlobalModel(MoveConfiguration configuration) {
        super(configuration);
        modelSequence.add(new FullSquareModel());
        modelSequence.add(new SimpleSquareModel());
    }

    @Override
    protected List<Sample> getSamples(Particle currentParticle, List<Particle> particleList) {
        final int dim = currentParticle.getFunction().getDimension();
        int samplesCount = Math.max(50 * dim * dim, _sampler.getSamplesCount());
        return _sampler.getSamples(samplesCount);
    }

    @Override
    public void registerObjectsWithOptimizer(SamplingOptimizer samplingOptimizer) {
        samplingOptimizer.registerSampler(_sampler);
    }

    @Override
    public void resetState() {
        _sampler = new AllSamplesSampler();
    }
}
