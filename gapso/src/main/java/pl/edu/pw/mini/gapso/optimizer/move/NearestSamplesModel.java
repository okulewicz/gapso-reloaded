package pl.edu.pw.mini.gapso.optimizer.move;

import pl.edu.pw.mini.gapso.configuration.MoveConfiguration;
import pl.edu.pw.mini.gapso.optimizer.Particle;
import pl.edu.pw.mini.gapso.optimizer.SamplingOptimizer;
import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.sample.sampler.TreeSampler;

import java.util.List;

public class NearestSamplesModel extends ModelMove {
    public static final String NAME = "NearestSamples";
    private static final int FACTOR = 5;
    private TreeSampler treeSampler;

    public NearestSamplesModel(MoveConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected List<Sample> getSamples(Particle currentParticle, List<Particle> particleList) {
        final int dimension = currentParticle.getFunction().getDimension();
        final int maxSamplesCount = modelSequenceWithFreq
                .keySet()
                .stream()
                .mapToInt(m -> m.getMinSamplesCount(dimension)).max().orElse(Integer.MAX_VALUE);
        return treeSampler.getKNearestSamples(currentParticle.getBest().getX(), FACTOR * maxSamplesCount);
    }

    @Override
    public void registerObjectsWithOptimizer(SamplingOptimizer samplingOptimizer) {
        samplingOptimizer.registerSampler(treeSampler);
    }

    @Override
    public void resetState(int particleCount) {
        if (treeSampler != null) {
            treeSampler.clear();
        }
        treeSampler = new TreeSampler();
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
}
