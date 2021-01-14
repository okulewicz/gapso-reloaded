package pl.edu.pw.mini.gapso.initializer;

import pl.edu.pw.mini.gapso.bounds.Bounds;
import pl.edu.pw.mini.gapso.bounds.SimpleBounds;
import pl.edu.pw.mini.gapso.bounds.SplittableBounds;
import pl.edu.pw.mini.gapso.model.FullSquareModel;
import pl.edu.pw.mini.gapso.model.Model;
import pl.edu.pw.mini.gapso.model.SimpleSquareModel;
import pl.edu.pw.mini.gapso.optimizer.SamplingOptimizer;
import pl.edu.pw.mini.gapso.sample.AllSamplesSampler;
import pl.edu.pw.mini.gapso.sample.Sample;

import java.util.ArrayList;
import java.util.List;

public class BoundsManager {
    public static final double INITIAL_SPREAD_FACTOR = 0.01;
    public static final int SAFETY_MULTIPLIER = 20;
    private Bounds initialBounds;
    private SplittableBounds bounds;
    private AllSamplesSampler sampler;
    private boolean modelUtilized;
    private double spreadFactor;

    public void resetManager() {
        bounds = new SplittableBounds(initialBounds);
        sampler = new AllSamplesSampler();
        spreadFactor = INITIAL_SPREAD_FACTOR;
    }

    public void registerObjectsWithOptimizer(SamplingOptimizer optimizer) {
        assert initialBounds != null;
        optimizer.registerSampler(sampler);
    }

    public void setInitialBounds(Bounds initialBounds) {
        this.initialBounds = initialBounds;
        resetManager();
    }

    public Bounds getBounds() {
        assert initialBounds != null;

        final int dimension = initialBounds.getLower().length;
        final int samplesForFullModel = 2 * SAFETY_MULTIPLIER * new FullSquareModel().getMinSamplesCount(dimension);
        final int samplesForSimpleModel = 2 * SAFETY_MULTIPLIER * new SimpleSquareModel().getMinSamplesCount(dimension);
        final boolean fullModelSatisfied = samplesForFullModel < sampler.getSamplesCount();
        final boolean simpleModelSatisfied = samplesForSimpleModel < sampler.getSamplesCount();
        Model functionModel;
        if (fullModelSatisfied)
            functionModel = new FullSquareModel();
        else if (simpleModelSatisfied) {
            functionModel = new SimpleSquareModel();
        } else {
            modelUtilized = false;
            return initialBounds;
        }
        final int samplesForModeling = SAFETY_MULTIPLIER * functionModel.getMinSamplesCount(dimension);
        if (sampler.getSamplesCount() > 2 * samplesForModeling) {
            List<double[]> estimations = new ArrayList<>();
            for (int j = 0; j < 10; ++j) {
                List<Sample> samplesForModel = sampler.getSamples(samplesForModeling);
                double[] boundsCenter = functionModel.getOptimumLocation(samplesForModel, SimpleBounds.createBoundsFromSamples(samplesForModel));
                if (functionModel.getrSquared() > 0.95) {
                    estimations.add(boundsCenter);
                }
            }
            if (estimations.size() < 10) {
                modelUtilized = false;
                return initialBounds;
            }
            double[] lower = new double[dimension];
            double[] upper = new double[dimension];
            for (int i = 0; i < dimension; ++i) {
                int finalI = i;
                double mean = estimations.stream().mapToDouble(e -> e[finalI]).average().getAsDouble();
                double sd = estimations.stream().mapToDouble(e -> Math.abs(e[finalI] - mean)).average().getAsDouble();
                lower[i] = mean - 2 * sd;
                upper[i] = mean + 2 * sd;
            }

            createSpreadForLowerAndUpperBounds(lower, upper);
            modelUtilized = true;
            return new SplittableBounds(
                    new SimpleBounds(
                            lower,
                            upper
                    )
            );
        }
        modelUtilized = false;
        return initialBounds;
    }

    private void createSpreadForLowerAndUpperBounds(double[] lower, double[] upper) {
        for (int i = 0; i < lower.length; ++i) {
            double spread = (initialBounds.getUpper()[i] - initialBounds.getLower()[i]) * spreadFactor;
            lower[i] = Math.max(initialBounds.getLower()[i], lower[i] - spread / 2.0);
            upper[i] = Math.min(initialBounds.getUpper()[i], upper[i] + spread / 2.0);
        }
        spreadFactor *= 2;
    }

    public void registerOptimumLocation(Sample sample) {

    }

    public boolean isModelUtilized() {
        return modelUtilized;
    }



    /*
new SplittableBounds(function.getBounds());
    int samplesCount = ((AllSamplesSampler)samplers.get(0)).getSamplesCount();
    samples = (((AllSamplesSampler)samplers.get(0)).getSamples(function.getDimension() * 100));
    SimpleSquareModel fsm = new SimpleSquareModel();
    double[] opt = fsm.getOptimumLocation(samples, function.getBounds());
                    System.err.println(samples.size() + " " + fsm.getrSquared());
                    if (fsm.getrSquared() > 0.9) {
        double[] lower = Arrays.copyOf(opt, opt.length);
        double[] upper = Arrays.copyOf(opt, opt.length);
        for (int i = 0; i < opt.length; ++i) {
            lower[i] = Math.max(opt[i] - 0.5, function.getBounds().getLower()[i]);
            upper[i] = Math.min(opt[i] + 0.5, function.getBounds().getUpper()[i]);
        }
        bounds = new SplittableBounds(new SimpleBounds(lower, upper));
    } else {
        bounds = new SplittableBounds(function.getBounds());
    }
                    if (_splitBounds) {
        if (bounds.areBoundsTooThinToSplit()) {
            bounds = new SplittableBounds(function.getBounds());
        } else {
            List<SplittableBounds> newBoundsList = bounds.Split(globalBest.getX());
            bounds = newBoundsList.get(1);
        }
    }
    */

}
