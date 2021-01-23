package pl.edu.pw.mini.gapso.initializer;

import pl.edu.pw.mini.gapso.bounds.Bounds;
import pl.edu.pw.mini.gapso.bounds.SimpleBounds;
import pl.edu.pw.mini.gapso.model.FullSquareModel;
import pl.edu.pw.mini.gapso.model.Model;
import pl.edu.pw.mini.gapso.model.SimpleSquareModel;
import pl.edu.pw.mini.gapso.optimizer.SamplingOptimizer;
import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.sample.sampler.AllSamplesSampler;

import java.util.ArrayList;
import java.util.List;

public class GlobalModelInitializer extends Initializer {
    public static final String NAME = "GlobalModel";
    public static final int SAMPLE_COUNT_MUL_FACTOR = 10;
    public static final double DESIRED_MODEL_QUALITY = 0.95;
    private ArrayList<Model> modelSequence;
    private AllSamplesSampler sampler;
    private boolean canSample;

    public GlobalModelInitializer() {
        resetInitializer(true);
    }

    @Override
    public double[] getNextSample(Bounds bounds) {
        assert sampler.getSamplesCount() > 0;
        Sample sample = sampler.getSamples(1).get(0);
        final int dimension = sample.getX().length;
        double[] returnSample = null;
        for (Model model : modelSequence) {
            final int minSamplesCount = model.getMinSamplesCount(dimension);
            if (sampler.getSamplesCount() >= SAMPLE_COUNT_MUL_FACTOR * minSamplesCount) {
                List<Sample> samples = sampler.getSamples(minSamplesCount);
                returnSample = model.getOptimumLocation(samples, bounds);
                if (model.getRSquared() < DESIRED_MODEL_QUALITY) {
                    returnSample = null;
                    continue;
                }
                if (returnSample == null) {
                    continue;
                }
                break;
            }
        }
        if (returnSample == null) {
            canSample = false;
            RandomInitializer ri = new RandomInitializer();
            returnSample = ri.getNextSample(bounds);
        }
        return returnSample;
    }

    @Override
    public boolean canSample() {
        return canSample;
    }

    protected boolean assessSamplingAbility() {
        if (sampler.getSamplesCount() == 0) {
            return false;
        }
        Sample sample = sampler.getSamples(1).get(0);
        final int dimension = sample.getX().length;
        Model selectedModel = null;
        for (int i = 0; i < modelSequence.size(); ++i) {
            Model model = modelSequence.get(i);
            final int desiredSamplesCount = SAMPLE_COUNT_MUL_FACTOR * model.getMinSamplesCount(dimension);
            if (sampler.getSamplesCount() >= desiredSamplesCount) {
                List<Sample> samples = sampler.getSamples(desiredSamplesCount);
                final Bounds boundsFromSamples = SimpleBounds.createBoundsFromSamples(samples);
                double[] optimumEstimation = model.getOptimumLocation(samples, boundsFromSamples);
                if (boundsFromSamples.striclyContain(optimumEstimation)) {
                    if (model.getRSquared() > DESIRED_MODEL_QUALITY) {
                        return true;
                    }
                }
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
            if (modelSequence != null) {
                modelSequence.clear();
            }
            modelSequence = new ArrayList<>();
            modelSequence.add(new SimpleSquareModel());
            modelSequence.add(new FullSquareModel());
        }
        canSample = assessSamplingAbility();
    }
}
