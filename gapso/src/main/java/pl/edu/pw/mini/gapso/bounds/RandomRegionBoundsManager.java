package pl.edu.pw.mini.gapso.bounds;

import pl.edu.pw.mini.gapso.configuration.BoundsManagerConfiguration;
import pl.edu.pw.mini.gapso.model.FullSquareModel;
import pl.edu.pw.mini.gapso.optimizer.SamplingOptimizer;
import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.sample.sampler.AllSamplesSampler;
import pl.edu.pw.mini.gapso.utils.Generator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RandomRegionBoundsManager extends BoundsManager {
    public static final double NON_REGISTER_OPTIMUM_THRESHOLD = 1e-2;
    public static final String NAME = "RandomRegion";
    private final List<Sample> optimaEstimations;
    private List<SplittableBounds> boundsList;
    private SplittableBounds currentBounds;
    private AllSamplesSampler sampler;

    public RandomRegionBoundsManager(BoundsManagerConfiguration configuration) {
        super(configuration);
        optimaEstimations = new ArrayList<>();
        sampler = new AllSamplesSampler();
    }

    @Override
    public void resetManager() {
        currentBounds = new SplittableBounds(initialBounds);
        boundsList = new ArrayList<>();
        boundsList.add(currentBounds);
        optimaEstimations.clear();
    }

    @Override
    public void registerObjectsWithOptimizer(SamplingOptimizer optimizer) {
        optimizer.registerSampler(sampler);
    }

    //TODO: Test getting bounds after registering optima
    @Override
    public Bounds getBounds() {
        FullSquareModel fsm = new FullSquareModel();
        final int dimension = currentBounds.getLower().length;
        int desiredSamplesCount = 10 * fsm.getMinSamplesCount(dimension);
        boolean getBest = false;
        if (sampler.getSamplesCount() > desiredSamplesCount) {
            List<Sample> samples = sampler.getSamples(desiredSamplesCount);
            fsm.getOptimumLocation(samples, currentBounds);
            if (Generator.RANDOM.nextDouble() < fsm.getRSquared()) {
                getBest = true;
            }
        }
        if (getBest) {
            optimaEstimations.sort(Comparator.comparingDouble(Sample::getY));
            Sample bestOptimum = optimaEstimations.get(0);
            List<SplittableBounds> overlappingBounds = boundsList.stream().filter(b -> b.contain(bestOptimum.getX())).collect(Collectors.toList());
            currentBounds = overlappingBounds.get(Generator.RANDOM.nextInt(overlappingBounds.size()));
        } else {
            currentBounds = boundsList.get(Generator.RANDOM.nextInt(boundsList.size()));
        }
        return currentBounds;
    }

    @Override
    public void registerOptimumLocation(Sample sample) {
        assert currentBounds.contain(sample.getX());
        boundsList.addAll(currentBounds.Split(sample.getX()));
        for (Sample gatheredSamples : optimaEstimations) {
            if (sample.getDistance(gatheredSamples) < NON_REGISTER_OPTIMUM_THRESHOLD) {
                return;
            }
        }
        optimaEstimations.add(sample);
    }
}
