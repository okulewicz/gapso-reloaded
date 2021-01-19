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
import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.utils.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ModelMove extends Move {
    protected final Map<Model, Integer> modelSequenceWithFreq;
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
        resetState();
    }

    @Override
    public double[] getNext(Particle currentParticle, List<Particle> particleList) {
        List<Sample> samples = getSamples(currentParticle, particleList);
        Bounds bounds = SimpleBounds.createBoundsFromSamples(samples);
        final int dimension = currentParticle.getFunction().getDimension();
        double[] returnSample = null;
        for (Model model : modelSequenceWithFreq.keySet()) {
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

        public ModelSequenceParameters(List<ModelParameters> models) {
            this.models = models;
        }

        public List<ModelParameters> getModels() {
            return models;
        }
    }

    protected abstract List<Sample> getSamples(Particle currentParticle, List<Particle> particleList);
}
