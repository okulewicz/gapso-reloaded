package pl.edu.pw.mini.gapso.optimizer.restart;

import pl.edu.pw.mini.gapso.optimizer.Particle;

import java.util.List;

public abstract class RestartObserver {
    public abstract boolean shouldBeRestarted(List<Particle> particleList);
}
