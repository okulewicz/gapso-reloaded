package pl.edu.pw.mini.gapso.generator.initializer;

import pl.edu.pw.mini.gapso.bounds.Bounds;

public abstract class Initializer {
    public abstract double[] getNextSample(Bounds bounds);

    protected abstract boolean canSample();
}
