package pl.edu.pw.mini.gapso.optimizer.restart;

import com.google.gson.JsonElement;
import pl.edu.pw.mini.gapso.optimizer.Particle;

import java.util.DoubleSummaryStatistics;
import java.util.List;

public class FunctionValueSpreadRestartManager extends ThresholdRestartManager {
    public final static String NAME = "FunctionValues";

    public FunctionValueSpreadRestartManager(double threshold) {
        super(threshold);
    }

    public FunctionValueSpreadRestartManager(JsonElement configuration) {
        super(configuration);
    }

    @Override
    public boolean shouldBeRestarted(List<Particle> particleList) {
        if (particleList.isEmpty())
            return true;
        if (particleList.size() < 2)
            return false;
        DoubleSummaryStatistics statistics =
                particleList
                        .stream()
                        .mapToDouble(p -> p.getBest().getY())
                        .summaryStatistics();
        double minOpt = statistics.getMin();
        double maxOpt = statistics.getMax();
        return maxOpt - minOpt < _threshold;
    }
}
