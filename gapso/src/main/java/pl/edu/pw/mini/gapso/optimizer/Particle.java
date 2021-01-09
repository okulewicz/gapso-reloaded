package pl.edu.pw.mini.gapso.optimizer;

import pl.edu.pw.mini.gapso.function.Function;
import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.sample.SingleSample;
import pl.edu.pw.mini.gapso.sample.UpdatableSample;

public class Particle {
    private Sample current;
    private Sample best;
    private UpdatableSample globalBest;

    public Particle(double[] initialLocation, Function function, UpdatableSample bestHolder) {
        Sample sample = initializeLocation(initialLocation, function);
        current = sample;
        best = sample;
        globalBest = bestHolder;
        tryUpdateGlobalBest();
    }

    public Sample getBest() {
        return best;
    }

    public Sample getGlobalBest() {
        return globalBest;
    }

    private void tryUpdateGlobalBest() {
        if (best.getY() < globalBest.getY()) {
            globalBest.updateSample(best);
        }
    }

    private Sample initializeLocation(double[] initialLocation, Function function) {
        double value = function.getValue(initialLocation);
        return new SingleSample(initialLocation, value);
    }
}
