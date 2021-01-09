package pl.edu.pw.mini.gapso.optimizer;

import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.sample.UpdatableSample;

public class Particle {
    private double[] velocity;
    private Sample current;
    private Sample best;
    private UpdatableSample globalBest;

    public Sample getBest() {
        return best;
    }
}
