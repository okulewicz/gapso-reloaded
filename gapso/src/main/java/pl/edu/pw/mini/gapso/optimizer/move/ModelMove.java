package pl.edu.pw.mini.gapso.optimizer.move;

import pl.edu.pw.mini.gapso.bounds.Bounds;
import pl.edu.pw.mini.gapso.bounds.SimpleBounds;
import pl.edu.pw.mini.gapso.configuration.MoveConfiguration;
import pl.edu.pw.mini.gapso.initializer.RandomInitializer;
import pl.edu.pw.mini.gapso.model.FullSquareModel;
import pl.edu.pw.mini.gapso.model.LinearModel;
import pl.edu.pw.mini.gapso.model.Model;
import pl.edu.pw.mini.gapso.model.SimpleSquareModel;
import pl.edu.pw.mini.gapso.optimizer.Particle;
import pl.edu.pw.mini.gapso.sample.OptimalClusters;
import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.utils.Util;

import java.util.*;
import java.util.stream.Collectors;

public abstract class ModelMove extends Move {
    protected final Map<Model, Integer> modelSequenceWithFreq;
    private SamplesClusteringType clusteringType;
    private int currentUseCounter;

    public ModelMove(MoveConfiguration configuration) {
        super(configuration);
        modelSequenceWithFreq = new HashMap<>();
        ModelMove.ModelSequenceParameters parameters =
                Util.GSON.fromJson(configuration.getParameters(), ModelMove.ModelSequenceParameters.class);
        for (ModelMove.ModelParameters modelParameters : parameters.getModels()) {
            Model model = null;
            int frequency = modelParameters.getModelUseFrequency();
            if (modelParameters.getModelType().equals(FullSquareModel.NAME)) {
                model = new FullSquareModel();
            }
            if (modelParameters.getModelType().equals(SimpleSquareModel.NAME)) {
                model = new SimpleSquareModel();
            }
            if (modelParameters.getModelType().equals(LinearModel.NAME)) {
                model = new LinearModel();
            }
            assert model != null;
            modelSequenceWithFreq.put(model, frequency);
        }
        currentUseCounter = 1;
        clusteringType = parameters.getClusteringType();
        resetState();
    }

    @Override
    public double[] getNext(Particle currentParticle, List<Particle> particleList) {
        final int dimension = currentParticle.getFunction().getDimension();
        List<Sample> samples = getSamples(currentParticle, particleList);
        if (clusteringType == SamplesClusteringType.LARGEST) {
            OptimalClusters clusters = new OptimalClusters(samples, dimension);
            final List<Sample> largestCluster = clusters.getLargestCluster();
            if (!largestCluster.isEmpty()) {
                samples = largestCluster;
            }
        } else if (clusteringType == SamplesClusteringType.BEST) {
            OptimalClusters clusters = new OptimalClusters(samples, dimension);
            final int minSamplesInBestCluster = modelSequenceWithFreq.keySet().stream().mapToInt(m -> m.getMinSamplesCount(dimension)).min().orElse(Integer.MAX_VALUE);
            final List<Sample> bestCluster = clusters.getBestCluster(minSamplesInBestCluster);
            if (!bestCluster.isEmpty()) {
                samples = bestCluster;
            }
        }
        Bounds bounds = SimpleBounds.createBoundsFromSamples(samples);
        double[] returnSample = null;
        final List<Model> models = modelSequenceWithFreq.keySet().stream().sorted(
                Comparator.comparingInt(m -> m.getMinSamplesCount(dimension)))
                .collect(Collectors.toList());
        Collections.reverse(models);
        for (Model model : models) {
            final int minSamplesCount = model.getMinSamplesCount(dimension);
            final Integer frequency = modelSequenceWithFreq.get(model);
            if (samples.size() >= minSamplesCount && currentUseCounter % frequency == 0) {
                returnSample = model.getOptimumLocation(samples, bounds);
                break;
            }
        }
        if (returnSample == null) {
            RandomInitializer ri = new RandomInitializer();
            returnSample = ri.getNextSample(bounds);
        }
        currentUseCounter++;
        return returnSample;
    }

    public static class ModelParameters {
        private String modelType;
        private int modelUseFrequency;

        public ModelParameters(String modelType, int modelUseFrequency) {
            this.modelType = modelType;
            this.modelUseFrequency = modelUseFrequency;
        }

        public String getModelType() {
            return modelType;
        }

        public int getModelUseFrequency() {
            return modelUseFrequency;
        }
    }

    public static class ModelSequenceParameters {
        private List<ModelParameters> models;
        private SamplesClusteringType clusteringType;

        public ModelSequenceParameters(List<ModelParameters> models, SamplesClusteringType clusteringType) {
            this.models = models;
            this.clusteringType = clusteringType;
        }

        public List<ModelParameters> getModels() {
            return models;
        }

        public SamplesClusteringType getClusteringType() {
            return clusteringType;
        }

    }

    protected abstract List<Sample> getSamples(Particle currentParticle, List<Particle> particleList);
}
