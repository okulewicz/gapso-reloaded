package pl.edu.pw.mini.gapso.optimizer;

import pl.edu.pw.mini.gapso.bounds.Bounds;
import pl.edu.pw.mini.gapso.function.Function;
import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.sample.Sampler;
import pl.edu.pw.mini.gapso.sample.SingleSample;

import java.util.List;

public abstract class SamplingOptimizer extends Optimizer {
    public abstract void registerSampler(Sampler sampler);

    protected Function createSamplingWrapper(Function function, List<Sampler> samplers) {
        return new FunctionSamplingWrapper(function, samplers);
    }

    private static class FunctionSamplingWrapper extends Function {
        private final Function _function;
        private final List<Sampler> _samplers;

        public FunctionSamplingWrapper(Function function, List<Sampler> samplers) {
            _samplers = samplers;
            _function = function;
        }

        @Override
        protected double computeValue(double[] x) {
            double y = _function.getValue(x);
            Sample s = new SingleSample(x, y);
            for (Sampler sampler : _samplers) {
                sampler.tryStoreSample(s);
            }
            return y;
        }

        @Override
        public boolean isTargetReached() {
            return _function.isTargetReached();
        }

        @Override
        public int getDimension() {
            return _function.getDimension();
        }

        @Override
        public Bounds getBounds() {
            return _function.getBounds();
        }
    }
}
