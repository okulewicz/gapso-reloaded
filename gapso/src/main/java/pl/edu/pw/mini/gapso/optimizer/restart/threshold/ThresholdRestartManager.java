package pl.edu.pw.mini.gapso.optimizer.restart.threshold;

import com.google.gson.JsonElement;
import pl.edu.pw.mini.gapso.optimizer.restart.RestartManager;
import pl.edu.pw.mini.gapso.utils.Util;

public abstract class ThresholdRestartManager extends RestartManager {
    protected final double _threshold;

    public ThresholdRestartManager(double threshold) {
        _threshold = threshold;
    }

    public ThresholdRestartManager(JsonElement configuration) {
        this(Util.GSON.fromJson(configuration, SpreadThresholdConfiguration.class).getThreshold());
    }

    public static class SpreadThresholdConfiguration {
        private double threshold;

        public SpreadThresholdConfiguration(double threshold) {
            this.threshold = threshold;
        }

        public double getThreshold() {
            return threshold;
        }
    }

    @Override
    public void reset() {
        //DO NOTHING ON PURPOSE
    }

}
