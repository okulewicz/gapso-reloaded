package pl.edu.pw.mini.gapso.optimizer.restart.threshold;

import com.google.gson.JsonElement;
import pl.edu.pw.mini.gapso.optimizer.Particle;

import java.util.DoubleSummaryStatistics;
import java.util.List;

public class SmallestSpreadBelowThresholdRestartManager extends ThresholdRestartManager {
    public final static String NAME = "MinSpreadInDimensions";

    public SmallestSpreadBelowThresholdRestartManager(double threshold) {
        super(threshold);
    }

    public SmallestSpreadBelowThresholdRestartManager(JsonElement configuration) {
        super(configuration);
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
}
