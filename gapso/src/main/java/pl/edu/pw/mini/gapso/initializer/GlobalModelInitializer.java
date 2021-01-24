package pl.edu.pw.mini.gapso.initializer;

import pl.edu.pw.mini.gapso.bounds.Bounds;
import pl.edu.pw.mini.gapso.bounds.SimpleBounds;
import pl.edu.pw.mini.gapso.model.FullSquareModel;
import pl.edu.pw.mini.gapso.model.Model;
import pl.edu.pw.mini.gapso.model.SimpleSquareModel;
import pl.edu.pw.mini.gapso.optimizer.SamplingOptimizer;
import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.sample.SingleSample;
import pl.edu.pw.mini.gapso.sample.sampler.AllSamplesSampler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GlobalModelInitializer extends Initializer {
    public static final String NAME = "GlobalModel";
    public static final int SAMPLE_COUNT_MUL_FACTOR = 30;
    public static final double DESIRED_MODEL_QUALITY = 0.8;
    public static final int DESIRED_GOOD_SAMPLES = 20;
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

        Bounds estimatedBounds = sampler.getBounds();

        for (int tr = 0; tr < 3 * DESIRED_GOOD_SAMPLES; ++tr) {
            for (Model model : modelSequence) {
                final int desiredSamplesCount = SAMPLE_COUNT_MUL_FACTOR * model.getMinSamplesCount(dimension);
                if (sampler.getSamplesCount() >= desiredSamplesCount) {
                    List<Sample> samples = sampler.getSamples(desiredSamplesCount);
                    samples.sort(Comparator.comparingDouble(Sample::getY));
                    Collections.reverse(samples);
                    samples = samples.stream().limit(desiredSamplesCount / 2).collect(Collectors.toList());
                    final Bounds boundsFromSamples = SimpleBounds.createBoundsFromSamples(samples);
                    double[] optimumEstimation = model.getOptimumLocation(samples, boundsFromSamples);
                    if (boundsFromSamples.strictlyContain(optimumEstimation)) {
                        if (model.getRSquared() > DESIRED_MODEL_QUALITY) {
                            resultSamples.add(new SingleSample(optimumEstimation, Double.POSITIVE_INFINITY));
                            if (resultSamples.size() >= DESIRED_GOOD_SAMPLES) {
                                boundsToGenerate = SimpleBounds.createBoundsFromSamples(resultSamples);
                                return true;
                            }
                            break;
                        }
                    }
                }
            }
        }
        if (boundsToGenerate != null) {
            double[] tempLower = new double[dimension];
            double[] tempUpper = new double[dimension];
            for (int d = 0; d < dimension; ++d) {
                tempLower[d] = (estimatedBounds.getLower()[d] + boundsToGenerate.getLower()[d]) / 2.0;
                tempUpper[d] = (estimatedBounds.getUpper()[d] + boundsToGenerate.getUpper()[d]) / 2.0;
            }
            boundsToGenerate = new SimpleBounds(tempLower, tempUpper);
            return true;
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
