package pl.edu.pw.mini.gapso.optimizer.restart.counter;

import pl.edu.pw.mini.gapso.optimizer.Particle;
import pl.edu.pw.mini.gapso.optimizer.restart.RestartManager;

import java.util.List;

public class NoImprovementRestartManager extends RestartManager {
    public static final String NAME = "NoImprovement";
    final int MAX_TESTS = 10;
    double bestValue = Double.POSITIVE_INFINITY;
    int tests = 0;

    @Override
    public boolean shouldBeRestarted(List<Particle> particleList) {
        final double globalBestValue = particleList.get(particleList.get(0).getGlobalBestIndex()).getBest().getY();
        if (globalBestValue < bestValue) {
            bestValue = globalBestValue;
            tests = 0;
        } else {
            tests++;
        }
        return MAX_TESTS < tests;
    }

    @Override
    public void reset() {
        tests = 0;
        bestValue = Double.POSITIVE_INFINITY;
    }

}
