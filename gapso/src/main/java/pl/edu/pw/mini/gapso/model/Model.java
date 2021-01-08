package pl.edu.pw.mini.gapso.model;

import pl.edu.pw.mini.gapso.bounds.Bounds;
import pl.edu.pw.mini.gapso.sample.Sample;

import java.util.List;

public abstract class Model {
    public abstract double[] getOptimumLocation(List<Sample> samples, Bounds bounds);

    protected abstract int getMinSamplesCount(int dim);
}
