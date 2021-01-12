package pl.edu.pw.mini.gapso.generator.initializer;

import pl.edu.pw.mini.gapso.bounds.Bounds;

import java.util.List;

public class SequenceInitializer extends Initializer {
    private final List<Initializer> _initializers;

    public SequenceInitializer(List<Initializer> initializers) {
        _initializers = initializers;
    }

    @Override
    public double[] getNextSample(Bounds bounds) {
        for (Initializer initializer : _initializers) {
            if (initializer.canSample()) {
                return initializer.getNextSample(bounds);
            }
        }
        throw new ArrayIndexOutOfBoundsException("No initializers are available for sampling");
    }

    @Override
    protected boolean canSample() {
        return _initializers.stream().anyMatch(Initializer::canSample);
    }
}
