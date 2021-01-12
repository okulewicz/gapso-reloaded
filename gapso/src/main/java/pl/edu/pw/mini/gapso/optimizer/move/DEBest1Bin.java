package pl.edu.pw.mini.gapso.optimizer.move;

import com.google.gson.Gson;
import pl.edu.pw.mini.gapso.configuration.MoveConfiguration;
import pl.edu.pw.mini.gapso.optimizer.Particle;
import pl.edu.pw.mini.gapso.utils.Generator;

import java.util.List;

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

    public DEBest1Bin(MoveConfiguration moveConfiguration) {
        super(moveConfiguration);
        Gson gson = new Gson();
        DEBest1BinConfiguration deConf = gson.fromJson(
                moveConfiguration.getParameters(),
                DEBest1BinConfiguration.class);
        _scale = deConf.getScale();
        _crossProb = deConf.getCrossProb();
    }

    @Override
    public double[] getNext(Particle currentParticle, List<Particle> particleList) {
        final int particlesCount = particleList.size();
        if (particlesCount < 4) {
            throw new IllegalArgumentException("Not enough particles for " + NAME);
        }
        int bestIndex = currentParticle.getGlobalBestIndex();
        int currentIndex = currentParticle.getIndex();
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
        private double scale;
        private double crossProb;

        public DEBest1BinConfiguration(double scale, double crossProb) {
            this.scale = scale;
            this.crossProb = crossProb;
        }

        public double getScale() {
            return scale;
        }

        public double getCrossProb() {
            return crossProb;
        }
    }
}
