package pl.edu.pw.mini.gapso.bounds;

import pl.edu.pw.mini.gapso.configuration.BoundsManagerConfiguration;
import pl.edu.pw.mini.gapso.optimizer.SamplingOptimizer;
import pl.edu.pw.mini.gapso.sample.Sample;

public class ResetAllBoundsManager extends BoundsManager {
    public static final String NAME = "ResetAll";

    public ResetAllBoundsManager(BoundsManagerConfiguration boundsManagerConfiguration) {
        super(boundsManagerConfiguration);
    }

    @Override
    public void resetManager() {
        //DO NOTHING ON PURPOSE
    }

    @Override
    public void registerObjectsWithOptimizer(SamplingOptimizer optimizer) {
        //DO NOTHING ON PURPOSE
    }

    @Override
    public Bounds getBounds() {
        return initialBounds;
    }

    @Override
    public void registerOptimumLocation(Sample sample) {
        //DO NOTHING ON PURPOSE
    }
}
