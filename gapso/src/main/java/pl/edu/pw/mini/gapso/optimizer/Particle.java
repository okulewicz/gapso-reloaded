package pl.edu.pw.mini.gapso.optimizer;

import pl.edu.pw.mini.gapso.bounds.Bounds;
import pl.edu.pw.mini.gapso.function.Function;
import pl.edu.pw.mini.gapso.optimizer.move.Move;
import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.sample.SingleSample;
import pl.edu.pw.mini.gapso.sample.UpdatableSample;

import java.util.Arrays;
import java.util.List;

public class Particle {
    private static final int MAX_COUNTER = 10;
    private final List<Particle> _particles;
    private int index;
    private final Function _function;
    private final UpdatableSample globalBest;
    private Sample current;
    private Sample best;
    private IndexContainer globalBestIndexContainer;

    public Particle(double[] initialLocation, Function function, UpdatableSample bestHolder, IndexContainer indexContainer, List<Particle> particles) {
        _function = function;
        _particles = particles;
        Sample sample = initializeLocation(initialLocation, function);
        current = sample;
        best = sample;
        globalBest = bestHolder;
        globalBestIndexContainer = indexContainer;
        index = particles.size();
        particles.add(this);
        tryUpdateGlobalBest();
    }

    public int getIndex() {
        return index;
    }

    public Sample getBest() {
        return best;
    }

    public int getGlobalBestIndex() {
        return globalBestIndexContainer.getIndex();
    }

    public Function getFunction() {
        return _function;
    }

    private void tryUpdateGlobalBest() {
        if (best.getY() < globalBest.getY()) {
            globalBest.updateSample(best);
            globalBestIndexContainer.setIndex(index);
        }
    }

    public static class IndexContainer {
        private int index;

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }

    private Sample initializeLocation(double[] initialLocation, Function function) {
        double value = function.getValue(initialLocation);
        return new SingleSample(initialLocation, value);
    }

    public void move(Move availableMove) {
        current = getSampleWithinFunctionBounds(availableMove);
        tryUpdatePersonalBest();
    }

    private void tryUpdatePersonalBest() {
        if (current.getY() < best.getY()) {
            best = current;
            tryUpdateGlobalBest();
        }
    }

    private Sample getSampleWithinFunctionBounds(Move availableMove) {
        double[] sample = availableMove.getNext(this, _particles);
        Bounds bounds = _function.getBounds();
        int counter = 0;
        while (!bounds.contain(sample)) {
            for (int i = 0; i < sample.length; ++i) {
                sample[i] = (current.getX()[i] + sample[i]) / 2.0;
            }
            if (counter++ > MAX_COUNTER) {
                sample = Arrays.copyOf(current.getX(), current.getX().length);
            }
        }
        double y = _function.getValue(sample);
        return new SingleSample(sample, y);
    }
}
