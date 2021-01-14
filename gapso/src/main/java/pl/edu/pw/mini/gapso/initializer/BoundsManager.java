package pl.edu.pw.mini.gapso.initializer;

import pl.edu.pw.mini.gapso.bounds.Bounds;
import pl.edu.pw.mini.gapso.bounds.SimpleBounds;
import pl.edu.pw.mini.gapso.bounds.SplittableBounds;
import pl.edu.pw.mini.gapso.model.FullSquareModel;
import pl.edu.pw.mini.gapso.model.Model;
import pl.edu.pw.mini.gapso.optimizer.SamplingOptimizer;
import pl.edu.pw.mini.gapso.sample.AllSamplesSampler;
import pl.edu.pw.mini.gapso.sample.Sample;

import java.util.Arrays;
import java.util.List;

public class BoundsManager {
    public static final double SPREAD_FACTOR = 0.1;
    private Bounds initialBounds;
    private SplittableBounds bounds;
    private AllSamplesSampler sampler;

    public void resetManager() {
        bounds = new SplittableBounds(initialBounds);
        sampler = new AllSamplesSampler();
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

        Model functionModel = new FullSquareModel();

        final int minSamplesCount = 5 * functionModel.getMinSamplesCount(initialBounds.getLower().length);
        if (sampler.getSamplesCount() > 2 * minSamplesCount) {
            List<Sample> samplesForModel = sampler.getSamples(minSamplesCount);
            double[] boundsCenter = functionModel.getOptimumLocation(samplesForModel, SimpleBounds.createBoundsFromSamples(samplesForModel));
            double[] lower = Arrays.copyOf(boundsCenter, boundsCenter.length);
            double[] upper = Arrays.copyOf(boundsCenter, boundsCenter.length);
            for (int j = 0; j < 100; ++j) {
                boundsCenter = functionModel.getOptimumLocation(samplesForModel, SimpleBounds.createBoundsFromSamples(samplesForModel));
                for (int i = 0; i < boundsCenter.length; ++i) {
                    lower[i] = Math.min(boundsCenter[i], lower[i]);
                    upper[i] = Math.max(boundsCenter[i], upper[i]);
                }
            }

            if (functionModel.getrSquared() > 0.9) {
                for (int i = 0; i < boundsCenter.length; ++i) {
                    double spread = (initialBounds.getUpper()[i] - initialBounds.getLower()[i]) * SPREAD_FACTOR;
                    lower[i] = Math.max(initialBounds.getLower()[i], lower[i] - spread / 2.0);
                    upper[i] = Math.min(initialBounds.getUpper()[i], upper[i] + spread / 2.0);
                }
                return new SplittableBounds(
                        new SimpleBounds(
                                lower,
                                upper
                        )
                );
            }
        }

        return initialBounds;
    }

    public void registerOptimumLocation(Sample sample) {

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
