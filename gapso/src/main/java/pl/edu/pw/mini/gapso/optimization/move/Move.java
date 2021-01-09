package pl.edu.pw.mini.gapso.optimization.move;

import pl.edu.pw.mini.gapso.optimizer.Particle;

import java.util.List;

public abstract class Move {
    public abstract double[] getNext(Particle currentParticle, List<Particle> particleList);
}
