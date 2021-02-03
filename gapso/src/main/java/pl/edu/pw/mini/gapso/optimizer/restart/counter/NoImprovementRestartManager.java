package pl.edu.pw.mini.gapso.optimizer.restart.counter;

import com.google.gson.JsonElement;
import pl.edu.pw.mini.gapso.optimizer.Particle;
import pl.edu.pw.mini.gapso.optimizer.restart.RestartManager;
import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.utils.Util;

import java.util.List;

public class NoImprovementRestartManager extends RestartManager {
    public static final String NAME = "NoImprovement";
    final int evaluationsPerDimensionLimit;
    double bestValue = Double.POSITIVE_INFINITY;
    int tests = 0;

    public NoImprovementRestartManager(JsonElement parameters) {
        Configuration conf = Util.GSON.fromJson(parameters, Configuration.class);
        this.evaluationsPerDimensionLimit = conf.getEvaluationsPerDimensionLimit();
    }

    @Override
    public boolean shouldBeRestarted(List<Particle> particleList) {
        final Sample best = particleList.get(particleList.get(0).getGlobalBestIndex()).getBest();
        final double globalBestValue = best.getY();
        if (globalBestValue < bestValue) {
            bestValue = globalBestValue;
            tests = 0;
        } else {
            tests += particleList.size();
        }
        return evaluationsPerDimensionLimit * best.getX().length < tests;
    }

    public static class Configuration {
        int evaluationsPerDimensionLimit;

        public Configuration(int evaluationsPerDimensionLimit) {
            this.evaluationsPerDimensionLimit = evaluationsPerDimensionLimit;
        }

        public int getEvaluationsPerDimensionLimit() {
            return evaluationsPerDimensionLimit;
        }


    }

    @Override
    public void reset() {
        tests = 0;
        bestValue = Double.POSITIVE_INFINITY;
    }

}
