package pl.edu.pw.mini.gapso.initializer;

import pl.edu.pw.mini.gapso.bounds.Bounds;
import pl.edu.pw.mini.gapso.optimizer.GAPSOOptimizer;

public abstract class Initializer {
    public abstract double[] getNextSample(Bounds bounds);

    public abstract boolean canSample();

    public abstract void registerObjectsWithOptimizer(GAPSOOptimizer optimizer);

    public abstract void resetInitializer();
}
