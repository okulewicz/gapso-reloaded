package pl.edu.pw.mini.gapso.optimizer;

import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.sample.SingleSample;

import java.util.ArrayList;
import java.util.List;

public class Swarm {
    private final List<Particle> particles;
    private Sample globalBest;
    private int globalBestIdx;

    public Swarm() {
        globalBestIdx = -1;
        globalBest = new SingleSample(new double[0], Double.POSITIVE_INFINITY);
        particles = new ArrayList<>();
    }

    public List<Particle> getParticles() {
        return particles;
    }

    public Sample getGlobalBest() {
        return globalBest;
    }

    public void setGlobalBest(Sample globalBest) {
        this.globalBest = globalBest;
    }

    public int getGlobalBestIdx() {
        return globalBestIdx;
    }

    public void setGlobalBestIdx(int globalBestIdx) {
        this.globalBestIdx = globalBestIdx;
    }
}
