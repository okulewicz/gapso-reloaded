package pl.edu.pw.mini.gapso.optimizer.move;

import pl.edu.pw.mini.gapso.bounds.Bounds;
import pl.edu.pw.mini.gapso.bounds.SimpleBounds;
import pl.edu.pw.mini.gapso.configuration.MoveConfiguration;
import pl.edu.pw.mini.gapso.initializer.RandomInitializer;
import pl.edu.pw.mini.gapso.model.Model;
import pl.edu.pw.mini.gapso.optimizer.Particle;
import pl.edu.pw.mini.gapso.sample.Sample;

import java.util.ArrayList;
import java.util.List;

public abstract class ModelMove extends Move {
    protected final List<Model> modelSequence;

    public ModelMove(MoveConfiguration configuration) {
        super(configuration);
        modelSequence = new ArrayList<>();
    }

    @Override
    public double[] getNext(Particle currentParticle, List<Particle> particleList) {
        List<Sample> samples = getSamples(currentParticle, particleList);
        Bounds bounds = SimpleBounds.createBoundsFromSamples(samples);
        final int dimension = currentParticle.getFunction().getDimension();
        double[] returnSample = null;
        for (Model model : modelSequence) {
            if (samples.size() >= model.getMinSamplesCount(dimension)) {
                returnSample = model.getOptimumLocation(samples, bounds);
                break;
            }
        }
        if (returnSample == null) {
            RandomInitializer ri = new RandomInitializer();
            returnSample = ri.getNextSample(bounds);
        }
        return returnSample;
    }

    protected abstract List<Sample> getSamples(Particle currentParticle, List<Particle> particleList);
}
