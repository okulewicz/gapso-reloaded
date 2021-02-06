package pl.edu.pw.mini.gapso.optimizer;

import pl.edu.pw.mini.gapso.bounds.Bounds;
import pl.edu.pw.mini.gapso.function.Function;
import pl.edu.pw.mini.gapso.optimizer.move.Move;
import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.sample.SingleSample;

import java.util.Arrays;
import java.util.List;

public class Particle {
    private static final int MAX_COUNTER = 10;
    private final List<Particle> _particles;
    private final Swarm _swarm;
    private final Function _function;
    private final int index;
    private Sample current;
    private Sample best;

    public Particle(double[] initialLocation, Function function, Swarm swarm) {
        _function = function;
        _particles = swarm.getParticles();
        _swarm = swarm;
        Sample sample = initializeLocation(initialLocation, function);
        current = sample;
        best = sample;
        index = _particles.size();
        _particles.add(this);
        tryUpdateGlobalBest();
    }

    public int getIndex() {
        return index;
    }

    public Sample getBest() {
        return best;
    }

    public int getGlobalBestIndex() {
        return _swarm.getGlobalBestIdx();
    }

    public Function getFunction() {
        return _function;
    }

    private void tryUpdateGlobalBest() {
        if (best.getY() < _swarm.getGlobalBest().getY()) {
            _swarm.setGlobalBest(best);
            _swarm.setGlobalBestIdx(index);
        }
    }

    private Sample initializeLocation(double[] initialLocation, Function function) {
        double value = function.getValue(initialLocation);
        return new SingleSample(initialLocation, value);
    }

    public ParticleMoveResults move(Move availableMove) {
        Sample returned = getSampleWithinFunctionBounds(availableMove);
        if (returned == null) {
            return null;
        }
        current = returned;
        ParticleMoveResults pmr = new ParticleMoveResults(
                _swarm.getGlobalBest().getY() - current.getY(),
                best.getY() - current.getY(),
                best
        );
        tryUpdatePersonalBest();
        return pmr;
    }

    private void tryUpdatePersonalBest() {
        if (current.getY() < best.getY()) {
            best = current;
            tryUpdateGlobalBest();
        }
    }

    private Sample getSampleWithinFunctionBounds(Move availableMove) {
        double[] sample = availableMove.getNext(this, _particles);
        if (sample == null) {
            return null;
        }
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
