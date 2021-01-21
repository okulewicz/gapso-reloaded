package pl.edu.pw.mini.gapso.optimizer.move;

import com.google.gson.Gson;
import org.apache.commons.math3.distribution.CauchyDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import pl.edu.pw.mini.gapso.configuration.MoveConfiguration;
import pl.edu.pw.mini.gapso.optimizer.Particle;
import pl.edu.pw.mini.gapso.optimizer.SamplingOptimizer;
import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.sample.sampler.LimitedCapacitySampler;
import pl.edu.pw.mini.gapso.utils.Generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class SHADE extends Move {
    public static final String NAME = "SHADE";
    private final double _scale;
    private final double _crossProb;
    private final int _slots;
    private final double _pBestRatio;
    private final double _archiveSizeFactor;
    private double[] _scales;
    private double[] _crossProbs;
    private LimitedCapacitySampler _archive;
    private int activeSlot;
    private double lastScale;
    private double lastCrossProb;
    private List<Double> deltas;
    private List<Double> successfulCrossProb;
    private List<Double> successfulScales;

    public SHADE(MoveConfiguration moveConfiguration) {
        super(moveConfiguration);
        Gson gson = new Gson();
        SHADEConfiguration deConf = gson.fromJson(
                moveConfiguration.getParameters(),
                SHADEConfiguration.class);
        _pBestRatio = deConf.getpBestRatio();
        _slots = deConf.getSlots();
        _scale = deConf.getScale();
        _crossProb = deConf.getCrossProb();
        _archiveSizeFactor = deConf.getArchiveSizeFactor();
    }

    public static double[] getDESample(double[] current, double[] best, double[] diffVector, double[] diffOrArchiveVector, double scale, double crossProb) {
        final int dim = current.length;
        double[] tryX = new double[dim];
        int alwaysSwitchIdx = Generator.RANDOM.nextInt(dim);
        for (int dimIdx = 0; dimIdx < dim; ++dimIdx) {
            tryX[dimIdx] = current[dimIdx]
                    + scale * (best[dimIdx] - current[dimIdx])
                    + scale * (diffVector[dimIdx] - diffOrArchiveVector[dimIdx]);
            if (alwaysSwitchIdx != dimIdx) {
                double testIfSwitch = Generator.RANDOM.nextDouble();
                if (testIfSwitch > crossProb) {
                    tryX[dimIdx] = current[dimIdx];
                }
            }
        }
        return tryX;
    }

    public double computeMeanWL(List<Double> deltas, List<Double> values) {
        double deltaSum = deltas.stream().mapToDouble(item -> item).sum();
        double numeratorSum = 0.0;
        double denominatorSum = 0.0;
        for (int i = 0; i < deltas.size(); ++i) {
            double w = deltas.get(i) / deltaSum;
            final double valueI = values.get(i);
            final double weightedValue = w * valueI;
            numeratorSum += weightedValue * valueI;
            denominatorSum += weightedValue;
        }
        return numeratorSum / denominatorSum;
    }

    @Override
    public double[] getNext(Particle currentParticle, List<Particle> particleList) {
        final int particlesCount = particleList.size();
        if (particlesCount < 4) {
            throw new IllegalArgumentException("Not enough particles for " + NAME);
        }
        int pBestIndex = getPBestParticleIndex(particleList);
        int currentIndex = currentParticle.getIndex();
        int randomIndex1 = currentIndex;
        while (randomIndex1 == currentIndex || randomIndex1 == pBestIndex) {
            randomIndex1 = Generator.RANDOM.nextInt(particlesCount);
        }
        int randomIndex2 = randomIndex1;
        while (randomIndex2 == randomIndex1 || randomIndex2 == currentIndex || randomIndex2 == pBestIndex) {
            randomIndex2 = Generator.RANDOM.nextInt(particlesCount + _archive.getSamplesCount());
        }
        lastScale = generateScale();
        lastCrossProb = generateCrossProb();
        final Sample currentSample = particleList.get(currentIndex).getBest();
        final Sample pBestSample = particleList.get(pBestIndex).getBest();
        final Sample randomSample = particleList.get(randomIndex1).getBest();
        final Sample randomOrArchiveSample = getRandomOrArchiveSample(particleList, _archive.getSamples(), randomIndex2);
        return getDESample(
                currentSample.getX(),
                pBestSample.getX(),
                randomSample.getX(),
                randomOrArchiveSample.getX(),
                lastScale,
                lastCrossProb);
    }

    protected Sample getRandomOrArchiveSample(List<Particle> particleList, List<Sample> archiveSamples, int randomIndex2) {
        if (randomIndex2 < particleList.size()) {
            return particleList.get(randomIndex2).getBest();
        } else {
            randomIndex2 -= particleList.size();
            return archiveSamples.get(randomIndex2);
        }
    }

    private int getPBestParticleIndex(List<Particle> particleList) {
        final int worstPBest = Math.max(1, (int) (particleList.size() * _pBestRatio));
        final int[] pBests = particleList
                .stream()
                .sorted(Comparator.comparingDouble(p -> p.getBest().getY()))
                .limit(worstPBest)
                .mapToInt(Particle::getIndex)
                .toArray();
        final int selectedPBestIdx = Generator.RANDOM.nextInt(pBests.length);
        return pBests[selectedPBestIdx];
    }

    private double generateCrossProb() {
        NormalDistribution cauchyDistribution = new NormalDistribution(Generator.RANDOM,
                Math.max(0, _crossProbs[activeSlot]), 0.1);
        return Math.max(0.25, cauchyDistribution.sample());
    }

    private double generateScale() {
        CauchyDistribution cauchyDistribution = new CauchyDistribution(Generator.RANDOM, _scales[activeSlot], 0.1);
        return Math.max(0, Math.min(cauchyDistribution.sample(), 0.9));
    }

    @Override
    public void registerObjectsWithOptimizer(SamplingOptimizer samplingOptimizer) {
        samplingOptimizer.registerSuccessSampler(_archive);
    }

    @Override
    public void resetState(int particleCount) {
        deltas = new ArrayList<>();
        successfulCrossProb = new ArrayList<>();
        successfulScales = new ArrayList<>();
        _scales = new double[_slots];
        Arrays.fill(_scales, _scale);
        _crossProbs = new double[_slots];
        Arrays.fill(_crossProbs, _crossProb);
        _archive = new LimitedCapacitySampler((int) (_archiveSizeFactor * particleCount));
        activeSlot = 0;
        resetWeight();
    }

    @Override
    public void registerPersonalImprovement(double deltaY) {
        deltas.add(deltaY);
        successfulCrossProb.add(lastCrossProb);
        successfulScales.add(lastScale);
    }

    @Override
    public void newIteration() {
        if (!deltas.isEmpty()) {
            _crossProbs[activeSlot] = computeMeanWL(deltas, successfulCrossProb);
            _scales[activeSlot] = computeMeanWL(deltas, successfulScales);
            deltas.clear();
            successfulScales.clear();
            successfulCrossProb.clear();
            activeSlot++;
            if (activeSlot > _slots - 1) {
                activeSlot = 0;
            }
        }
    }

    public static class SHADEConfiguration {
        private final double pBestRatio;
        private double scale;
        private double crossProb;
        private int slots;
        private double archiveSizeFactor;

        public SHADEConfiguration(double scale, double crossProb, double pBestRatio,
                                  int slots, double archiveSizeFactor) {
            this.scale = scale;
            this.crossProb = crossProb;
            this.pBestRatio = pBestRatio;
            this.slots = slots;
            this.archiveSizeFactor = archiveSizeFactor;
        }

        public double getScale() {
            return scale;
        }

        public double getCrossProb() {
            return crossProb;
        }

        public int getSlots() {
            return slots;
        }

        public double getArchiveSizeFactor() {
            return archiveSizeFactor;
        }

        public double getpBestRatio() {
            return pBestRatio;
        }
    }
}
