package pl.edu.pw.mini.gapso.optimizer.move;

import pl.edu.pw.mini.gapso.configuration.MoveConfiguration;
import pl.edu.pw.mini.gapso.optimizer.Particle;
import pl.edu.pw.mini.gapso.optimizer.SamplingOptimizer;
import pl.edu.pw.mini.gapso.sample.Sample;

import java.util.List;
import java.util.stream.Collectors;

public class LocalBestModel extends ModelMove {
    public static final String NAME = "LocalBestModel";

    public LocalBestModel(MoveConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected List<Sample> getSamples(Particle currentParticle, List<Particle> particleList) {
        return particleList.stream().map(Particle::getBest).collect(Collectors.toList());
    }

    @Override
    public void registerObjectsWithOptimizer(SamplingOptimizer samplingOptimizer) {
        //DO NOTHING ON PURPOSE
    }

    @Override
    public void resetState() {
        //DO NOTHING ON PURPOSE
    }
}
