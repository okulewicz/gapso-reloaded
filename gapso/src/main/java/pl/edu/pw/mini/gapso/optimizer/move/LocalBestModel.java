package pl.edu.pw.mini.gapso.optimizer.move;

import pl.edu.pw.mini.gapso.bounds.Bounds;
import pl.edu.pw.mini.gapso.bounds.SimpleBounds;
import pl.edu.pw.mini.gapso.configuration.MoveConfiguration;
import pl.edu.pw.mini.gapso.initializer.RandomInitializer;
import pl.edu.pw.mini.gapso.model.FullSquareModel;
import pl.edu.pw.mini.gapso.model.LinearModel;
import pl.edu.pw.mini.gapso.model.Model;
import pl.edu.pw.mini.gapso.model.SimpleSquareModel;
import pl.edu.pw.mini.gapso.optimizer.Particle;
import pl.edu.pw.mini.gapso.sample.Sample;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LocalBestModel extends Move {
    public static final String NAME = "LocalBestModel";

    public LocalBestModel(MoveConfiguration configuration) {
        super(configuration);
    }

    @Override
    public double[] getNext(Particle currentParticle, List<Particle> particleList) {
        List<Sample> samples = particleList.stream().map(Particle::getBest).collect(Collectors.toList());
        Bounds bounds = SimpleBounds.createBoundsFromSamples(samples);
        final int dimension = currentParticle.getFunction().getDimension();
        List<Model> modelSequence = new ArrayList<>();
        modelSequence.add(new FullSquareModel());
        modelSequence.add(new SimpleSquareModel());
        modelSequence.add(new LinearModel());
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
}
