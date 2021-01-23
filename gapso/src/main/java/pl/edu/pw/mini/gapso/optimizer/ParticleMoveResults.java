package pl.edu.pw.mini.gapso.optimizer;

import pl.edu.pw.mini.gapso.sample.Sample;

public class ParticleMoveResults {
    public double globalImprovement;
    public double personalImprovement;
    public Sample previousBest;

    public ParticleMoveResults(double globalImprovement, double personalImprovement, Sample previousBest) {
        this.globalImprovement = globalImprovement;
        this.personalImprovement = personalImprovement;
        this.previousBest = previousBest;
    }

    public double getGlobalImprovement() {
        return globalImprovement;
    }

    public double getPersonalImprovement() {
        return personalImprovement;
    }

    public Sample getPreviousBest() {
        return previousBest;
    }
}
