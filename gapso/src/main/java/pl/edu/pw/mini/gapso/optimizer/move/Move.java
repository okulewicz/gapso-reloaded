package pl.edu.pw.mini.gapso.optimizer.move;

import pl.edu.pw.mini.gapso.configuration.MoveConfiguration;
import pl.edu.pw.mini.gapso.optimizer.Particle;
import pl.edu.pw.mini.gapso.optimizer.SamplingOptimizer;

import java.util.List;

public abstract class Move {
    private int minNumber;
    private double weight;
    private boolean isAdaptable;

    public Move(MoveConfiguration configuration) {
        this.minNumber = configuration.getMinimalAmount();
        this.weight = configuration.getInitialWeight();
        this.isAdaptable = configuration.isAdaptable();
    }

    public abstract double[] getNext(Particle currentParticle, List<Particle> particleList);

    public int getMinNumber() {
        return minNumber;
    }

    public double getWeight() {
        return weight;
    }

    public boolean isAdaptable() {
        return isAdaptable;
    }

    public abstract void registerObjectsWithOptimizer(SamplingOptimizer samplingOptimizer);

    public abstract void resetState();
}
