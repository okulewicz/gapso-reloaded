package pl.edu.pw.mini.gapso.optimizer.restart;

import pl.edu.pw.mini.gapso.optimizer.Particle;

import java.util.DoubleSummaryStatistics;
import java.util.List;

public class MinSpreadInDimensionsRestartManager extends RestartManager {
    public final static String NAME = "MinSpreadInDimensions";
    private final double _threshold;

    public MinSpreadInDimensionsRestartManager(double threshold) {
        _threshold = threshold;
    }

    public MinSpreadInDimensionsRestartManager(RandomManagerMinSpreadInDimensionsConfiguration configuration) {
        _threshold = configuration.getThreshold();
    }

    @Override
    public boolean shouldBeRestarted(List<Particle> particleList) {
        if (particleList.isEmpty())
            return true;
        if (particleList.size() < 2)
            return false;
        int dim = particleList.get(0).getBest().getX().length;
        for (int i = 0; i < dim; ++i) {
            int finalI = i;
            DoubleSummaryStatistics statistics =
                    particleList
                            .stream()
                            .mapToDouble(p -> p.getBest()
                                    .getX()[finalI])
                            .summaryStatistics();
            double minOpt = statistics.getMin();
            double maxOpt = statistics.getMax();
            if (maxOpt - minOpt < _threshold) {
                return true;
            }
        }
        return false;
    }

    public static class RandomManagerMinSpreadInDimensionsConfiguration {
        @SuppressWarnings("unused")
        private double threshold;

        public double getThreshold() {
            return threshold;
        }
    }
}
