package pl.edu.pw.mini.gapso.initializer;

import pl.edu.pw.mini.gapso.bounds.Bounds;

public abstract class Initializer {
    public abstract double[] getNextSample(Bounds bounds);

    public abstract boolean canSample();
}
