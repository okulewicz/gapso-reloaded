package pl.edu.pw.mini.gapso.bounds;

import pl.edu.pw.mini.gapso.configuration.BoundsManagerConfiguration;
import pl.edu.pw.mini.gapso.optimizer.SamplingOptimizer;
import pl.edu.pw.mini.gapso.sample.Sample;

public abstract class BoundsManager {
    protected Bounds initialBounds;

    public BoundsManager(BoundsManagerConfiguration configuration) {

    }

    public void setInitialBounds(Bounds initialBounds) {
        this.initialBounds = initialBounds;
        resetManager();
    }

    public abstract void resetManager();

    public abstract void registerObjectsWithOptimizer(SamplingOptimizer optimizer);

    public abstract Bounds getBounds();

    public abstract void registerOptimumLocation(Sample sample);
}
