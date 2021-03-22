package pl.edu.pw.mini.gapso.initializer;

import pl.edu.pw.mini.gapso.bounds.Bounds;
import pl.edu.pw.mini.gapso.model.FullSquareModel;
import pl.edu.pw.mini.gapso.model.LinearModel;
import pl.edu.pw.mini.gapso.model.Model;
import pl.edu.pw.mini.gapso.model.SimpleSquareModel;
import pl.edu.pw.mini.gapso.optimizer.SamplingOptimizer;
import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.sample.sampler.AllSamplesSampler;

import java.util.ArrayList;
import java.util.List;

public class ModelInitializer extends Initializer {
    public static final String NAME = "Model";
    public static final double R_SQUARED = 0.99;
    private ArrayList<Model> modelSequence;
    private AllSamplesSampler sampler;

    public ModelInitializer() {
        resetInitializer(true);
    }

    @Override
    public double[] getNextSample(Bounds bounds) {
        assert sampler.getSamplesCount() > 0;
        Sample sample = sampler.getSamples(1).get(0);
        final int dimension = sample.getX().length;
        double[] returnSample = null;
        for (int i = 0; i < modelSequence.size(); ++i) {
            Model model = modelSequence.get(i);
            final int minSamplesCount = model.getMinSamplesCount(dimension);
            if (sampler.getSamplesCount() >= minSamplesCount) {
                List<Sample> samples = sampler.getSamples(sampler.getSamplesCount());
                returnSample = model.getOptimumLocation(samples, bounds);
                if (model.getRSquared() < R_SQUARED) {
                    returnSample = null;
                }
                if (returnSample == null)
                    continue;
                modelSequence.remove(model);
                break;
            }
        }
        if (returnSample == null) {
            RandomInitializer ri = new RandomInitializer();
            returnSample = ri.getNextSample(bounds);
        }
        return returnSample;
    }

    @Override
    public boolean canSample() {
        if (sampler.getSamplesCount() == 0) {
            return false;
        }
        Sample sample = sampler.getSamples(1).get(0);
        final int dimension = sample.getX().length;
        for (Model model : modelSequence) {
            if (sampler.getSamplesCount() >= model.getMinSamplesCount(dimension)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void registerObjectsWithOptimizer(SamplingOptimizer optimizer) {
        optimizer.registerSampler(sampler);
    }

    @Override
    public void resetInitializer(boolean hardReset) {
        if (hardReset) {
            sampler = new AllSamplesSampler();
            modelSequence = new ArrayList<>();
        }
        assert modelSequence != null;
        modelSequence.clear();
        modelSequence.add(new LinearModel());
        modelSequence.add(new SimpleSquareModel());
        modelSequence.add(new FullSquareModel());
        modelSequence.add(new SimpleSquareModel());
        modelSequence.add(new FullSquareModel());
    }
}
