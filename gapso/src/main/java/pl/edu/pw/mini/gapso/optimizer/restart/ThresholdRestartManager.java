package pl.edu.pw.mini.gapso.optimizer.restart;

public abstract class ThresholdRestartManager extends RestartManager {
    protected final double _threshold;

    public ThresholdRestartManager(double threshold) {
        _threshold = threshold;
    }

    public ThresholdRestartManager(SpreadThresholdConfiguration configuration) {
        this(configuration.getThreshold());
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

}
