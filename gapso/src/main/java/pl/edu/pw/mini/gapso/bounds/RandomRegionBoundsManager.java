package pl.edu.pw.mini.gapso.bounds;

import pl.edu.pw.mini.gapso.configuration.BoundsManagerConfiguration;
import pl.edu.pw.mini.gapso.optimizer.SamplingOptimizer;
import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.utils.Generator;

import java.util.ArrayList;
import java.util.List;

public class RandomRegionBoundsManager extends BoundsManager {
    public static final double NON_REGISTER_OPTIMUM_THRESHOLD = 1e-2;
    public static final String NAME = "RandomRegion";
    private final List<Sample> optimaEstimations;
    private List<SplittableBounds> boundsList;
    private SplittableBounds currentBounds;

    public RandomRegionBoundsManager(BoundsManagerConfiguration configuration) {
        super(configuration);
        optimaEstimations = new ArrayList<>();
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

    }

    @Override
    public Bounds getBounds() {
        currentBounds = boundsList.get(Generator.RANDOM.nextInt(boundsList.size()));
        return currentBounds;
    }

    @Override
    public void registerOptimumLocation(Sample sample) {
        for (Sample gatheredSamples : optimaEstimations) {
            if (sample.getDistance(gatheredSamples) < NON_REGISTER_OPTIMUM_THRESHOLD) {
                return;
            }
        }
        optimaEstimations.add(sample);
        assert currentBounds.contain(sample.getX());
        boundsList.addAll(currentBounds.Split(sample.getX()));
    }
}
