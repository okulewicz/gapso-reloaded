package pl.edu.pw.mini.gapso.optimizer.restart;

import pl.edu.pw.mini.gapso.optimizer.Particle;

import java.util.List;
import java.util.OptionalDouble;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;

public class MaxPopulationDistanceRestart extends RestartObserver {
    private final double _threshold;

    public MaxPopulationDistanceRestart(double threshold) {
        _threshold = threshold;
    }

    @Override
    public boolean shouldBeRestarted(List<Particle> particleList) {
        if (particleList.isEmpty())
            return true;
        int dim = particleList.get(0).getBest().getX().length;
        for (int i = 0; i < dim; ++i) {
            int finalI = i;
            Supplier<DoubleStream> valuesInDimSupplier = () -> particleList.stream().mapToDouble(p -> p.getBest().getX()[finalI]);
            OptionalDouble minOpt = valuesInDimSupplier.get().min();
            OptionalDouble maxOpt = valuesInDimSupplier.get().max();
            if (minOpt.isPresent() && maxOpt.isPresent()) {
                if (maxOpt.getAsDouble() - minOpt.getAsDouble() < _threshold) {
                    return true;
                }
            }
        }
        return false;
    }
}
