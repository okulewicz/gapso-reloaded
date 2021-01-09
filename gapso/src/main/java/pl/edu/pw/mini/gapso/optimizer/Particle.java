package pl.edu.pw.mini.gapso.optimizer;

import pl.edu.pw.mini.gapso.bounds.Bounds;
import pl.edu.pw.mini.gapso.function.Function;
import pl.edu.pw.mini.gapso.optimization.move.Move;
import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.sample.SingleSample;
import pl.edu.pw.mini.gapso.sample.UpdatableSample;

import java.util.List;

public class Particle {
    private final Function _function;
    private Sample current;
    private Sample best;
    private UpdatableSample globalBest;

    public Particle(double[] initialLocation, Function function, UpdatableSample bestHolder) {
        _function = function;
        Sample sample = initializeLocation(initialLocation, function);
        current = sample;
        best = sample;
        globalBest = bestHolder;
        tryUpdateGlobalBest();
    }

    public Sample getBest() {
        return best;
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

    public void move(Move availableMove, List<Particle> particleList) {
        current = getSampleWithinFunctionBounds(availableMove, particleList);
        tryUpdatePersonalBest();
    }

    private void tryUpdatePersonalBest() {
        if (current.getY() < best.getY()) {
            best = current;
            tryUpdateGlobalBest();
        }
    }

    private Sample getSampleWithinFunctionBounds(Move availableMove, List<Particle> particleList) {
        double[] sample = availableMove.getNext(this, particleList);
        Bounds bounds = _function.getBounds();
        while (!bounds.contain(sample)) {
            for (int i = 0; i < sample.length; ++i) {
                sample[i] = (current.getX()[i] + sample[i]) / 2.0;
            }
        }
        double y = _function.getValue(sample);
        return new SingleSample(sample, y);
    }
}
