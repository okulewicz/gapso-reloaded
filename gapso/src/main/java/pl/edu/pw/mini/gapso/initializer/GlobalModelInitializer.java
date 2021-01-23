package pl.edu.pw.mini.gapso.initializer;

import pl.edu.pw.mini.gapso.bounds.Bounds;
import pl.edu.pw.mini.gapso.bounds.SimpleBounds;
import pl.edu.pw.mini.gapso.model.FullSquareModel;
import pl.edu.pw.mini.gapso.model.Model;
import pl.edu.pw.mini.gapso.optimizer.SamplingOptimizer;
import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.sample.SingleSample;
import pl.edu.pw.mini.gapso.sample.sampler.AllSamplesSampler;

import java.util.ArrayList;
import java.util.List;

public class GlobalModelInitializer extends Initializer {
    public static final String NAME = "GlobalModel";
    public static final int SAMPLE_COUNT_MUL_FACTOR = 20;
    public static final double DESIRED_MODEL_QUALITY = 0.98;
    public static final int DESIRED_GOOD_SAMPLES = 30;
    private ArrayList<Model> modelSequence;
    private AllSamplesSampler sampler;
    private boolean canSample;
    private Bounds boundsToGenerate;

    public GlobalModelInitializer() {
        resetInitializer(true);
    }

    @Override
    public double[] getNextSample(Bounds bounds) {
        assert canSample;
        assert boundsToGenerate != null;
        RandomInitializer ri = new RandomInitializer();
        assert bounds.contain(boundsToGenerate.getLower());
        assert bounds.contain(boundsToGenerate.getUpper());
        return ri.getNextSample(boundsToGenerate);
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
        List<Sample> resultSamples = new ArrayList<>();
        for (Model model : modelSequence) {
            final int desiredSamplesCount = SAMPLE_COUNT_MUL_FACTOR * model.getMinSamplesCount(dimension);
            if (sampler.getSamplesCount() >= desiredSamplesCount) {
                for (int tr = 0; tr < 2 * DESIRED_GOOD_SAMPLES; ++tr) {
                    List<Sample> samples = sampler.getSamples(desiredSamplesCount);
                    final Bounds boundsFromSamples = SimpleBounds.createBoundsFromSamples(samples);
                    double[] optimumEstimation = model.getOptimumLocation(samples, boundsFromSamples);
                    if (boundsFromSamples.strictlyContain(optimumEstimation)) {
                        if (model.getRSquared() > DESIRED_MODEL_QUALITY) {
                            resultSamples.add(new SingleSample(optimumEstimation, Double.POSITIVE_INFINITY));
                            if (resultSamples.size() >= DESIRED_GOOD_SAMPLES) {
                                boundsToGenerate = SimpleBounds.createBoundsFromSamples(resultSamples);
                                return true;
                            }
                        }
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
            modelSequence.add(new FullSquareModel());
        }
        canSample = assessSamplingAbility();
    }
}
