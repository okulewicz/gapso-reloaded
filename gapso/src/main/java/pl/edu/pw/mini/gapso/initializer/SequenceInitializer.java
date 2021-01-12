package pl.edu.pw.mini.gapso.initializer;

import pl.edu.pw.mini.gapso.bounds.Bounds;
import pl.edu.pw.mini.gapso.configuration.InitializerConfiguration;
import pl.edu.pw.mini.gapso.optimizer.SamplingOptimizer;
import pl.edu.pw.mini.gapso.utils.Util;

import java.util.List;
import java.util.stream.Collectors;

public class SequenceInitializer extends Initializer {
    public static final String NAME = "Sequence";

    public SequenceInitializer(InitializerConfiguration configuration) {
        this(Util.GSON
                .fromJson(
                        configuration.getParameters(),
                        Configuration.class)
                .getInitializers());
    }

    private final List<Initializer> _initializers;

    @Override
    public boolean canSample() {
        return _initializers.stream().anyMatch(Initializer::canSample);
    }

    @Override
    public void registerObjectsWithOptimizer(SamplingOptimizer optimizer) {
        for (Initializer initializer : _initializers) {
            initializer.registerObjectsWithOptimizer(optimizer);
        }
    }

    @Override
    public void resetInitializer(boolean hardReset) {
        for (Initializer initializer : _initializers) {
            initializer.resetInitializer(hardReset);
        }
    }

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

    public static class Configuration {
        private List<InitializerConfiguration> initializers;

        public Configuration(List<InitializerConfiguration> initializers) {
            this.initializers = initializers;
        }

        public List<Initializer> getInitializers() {
            return initializers.stream().map(InitializerConfiguration::getInitializer).collect(Collectors.toList());
        }
    }
}
