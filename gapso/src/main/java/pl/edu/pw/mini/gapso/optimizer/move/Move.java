package pl.edu.pw.mini.gapso.optimizer.move;

import pl.edu.pw.mini.gapso.configuration.MoveConfiguration;
import pl.edu.pw.mini.gapso.optimizer.Particle;
import pl.edu.pw.mini.gapso.optimizer.SamplingOptimizer;

import java.util.List;

public abstract class Move {
    private final double initialWeight;
    private final double minimalRatio;
    private final int minNumber;
    private double weight;
    private final boolean isAdaptable;

    public Move(MoveConfiguration configuration) {
        this.minNumber = configuration.getMinimalAmount();
        this.minimalRatio = configuration.getMinimalRatio();
        this.initialWeight = this.weight = configuration.getInitialWeight();
        this.isAdaptable = configuration.isAdaptable();
    }

    public abstract double[] getNext(Particle currentParticle, List<Particle> particleList) throws IllegalStateException;

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

    public abstract void resetState(int particleCount);

    public abstract void registerPersonalImprovement(double deltaY);

    public abstract void newIteration();

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void resetWeight() {
        this.weight = this.initialWeight;
    }

    public double getMinimalRatio() {
        return minimalRatio;
    }

    public abstract void registerSamplingResult(double y);
}
