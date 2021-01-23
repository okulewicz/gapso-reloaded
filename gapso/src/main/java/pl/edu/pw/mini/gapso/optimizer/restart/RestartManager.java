package pl.edu.pw.mini.gapso.optimizer.restart;

import pl.edu.pw.mini.gapso.optimizer.Particle;

import java.util.List;

public abstract class RestartManager {
    public abstract boolean shouldBeRestarted(List<Particle> particleList);

    public abstract void reset();
}
