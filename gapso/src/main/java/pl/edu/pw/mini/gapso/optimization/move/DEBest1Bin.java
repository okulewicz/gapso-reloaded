package pl.edu.pw.mini.gapso.optimization.move;

import pl.edu.pw.mini.gapso.generator.Generator;
import pl.edu.pw.mini.gapso.optimizer.Particle;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class DEBest1Bin extends Move {
    public static final String NAME = "DE/best/1/bin";
    private final double _scale;
    private final double _crossProb;

    public static double[] getDESample(double[] current, double[] best, double[] diffVector1, double[] diffVector2, double scale, double crossProb) {
        final int dim = current.length;
        double[] tryX = new double[dim];
        int alwaysSwitchIdx = Generator.RANDOM.nextInt(dim);
        for (int dimIdx = 0; dimIdx < dim; ++dimIdx) {
            tryX[dimIdx] = best[dimIdx] + scale * (diffVector1[dimIdx] - diffVector2[dimIdx]);
            if (alwaysSwitchIdx != dimIdx) {
                double testIfSwitch = Generator.RANDOM.nextDouble();
                if (testIfSwitch > crossProb) {
                    tryX[dimIdx] = current[dimIdx];
                }
            }
        }
        return tryX;
    }

    public DEBest1Bin(double scale, double crossProb) {
        _scale = scale;
        _crossProb = crossProb;
    }

    public DEBest1Bin(DEBest1BinConfiguration configuration) {
        this(configuration.getScale(), configuration.getCrossProb());
    }

    @Override
    public double[] getNext(Particle currentParticle, List<Particle> particleList) {
        final int particlesCount = particleList.size();
        if (particlesCount < 4) {
            throw new IllegalArgumentException("Not enough particles for " + NAME);
        }
        Stream<Particle> bestParticles = particleList.stream().sorted(Comparator.comparing(p -> p.getBest().getY()));
        Particle bestParticle = bestParticles.findFirst().orElseThrow(() -> new IllegalArgumentException("No particles to choose from in " + "DE/rand/1/bin"));
        int bestIndex = particleList.indexOf(bestParticle);
        int currentIndex = particleList.indexOf(currentParticle);
        int randomIndex1 = currentIndex;
        while (randomIndex1 == currentIndex || randomIndex1 == bestIndex) {
            randomIndex1 = Generator.RANDOM.nextInt(particlesCount);
        }
        int randomIndex2 = randomIndex1;
        while (randomIndex2 == randomIndex1 || randomIndex2 == currentIndex || randomIndex2 == bestIndex) {
            randomIndex2 = Generator.RANDOM.nextInt(particlesCount);
        }
        double scale = Generator.RANDOM.nextDouble() * _scale;
        double crossProb = Generator.RANDOM.nextDouble() * _crossProb;
        return getDESample(
                particleList.get(currentIndex).getBest().getX(),
                particleList.get(bestIndex).getBest().getX(),
                particleList.get(randomIndex1).getBest().getX(),
                particleList.get(randomIndex2).getBest().getX(),
                scale,
                crossProb);
    }

    public static class DEBest1BinConfiguration {
        @SuppressWarnings("unused")
        private double scale;
        @SuppressWarnings("unused")
        private double crossProb;

        public double getScale() {
            return scale;
        }

        public double getCrossProb() {
            return crossProb;
        }
    }
}
