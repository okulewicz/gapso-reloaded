package pl.edu.pw.mini.gapso.optimizer;

import pl.edu.pw.mini.gapso.function.Function;
import pl.edu.pw.mini.gapso.generator.Generator;
import pl.edu.pw.mini.gapso.generator.initializer.Initializer;
import pl.edu.pw.mini.gapso.optimization.move.Move;
import pl.edu.pw.mini.gapso.optimizer.restart.RestartObserver;
import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.sample.SingleSample;
import pl.edu.pw.mini.gapso.sample.UpdatableSample;

import java.util.ArrayList;
import java.util.List;

public class GAPSOOptimizer extends Optimizer {
    private final int _particlesCount;
    private final int _evaluationsBudget;
    private final Move[] _availableMoves;
    private final Initializer _initializer;
    private final RestartObserver _restartObserver;

    public GAPSOOptimizer(int particlesCount, int evaluationsBudget, Move[] availableMoves, Initializer initializer, RestartObserver restartObserver) {
        _particlesCount = particlesCount;
        _evaluationsBudget = evaluationsBudget;
        _availableMoves = availableMoves;
        _initializer = initializer;
        _restartObserver = restartObserver;
    }

    @Override
    public Sample optimize(Function function) {
        UpdatableSample totalGlobalBest = new UpdatableSample(
                new SingleSample(
                        new double[function.getDimension()],
                        Double.POSITIVE_INFINITY)
        );
        while (function.getEvaluationsCount() < _evaluationsBudget) {
            UpdatableSample globalBest = new UpdatableSample(
                    new SingleSample(
                            new double[function.getDimension()],
                            Double.POSITIVE_INFINITY)
            );
            List<Particle> particles = new ArrayList<>();
            for (int i = 0; i < _particlesCount; ++i) {
                double[] initialLocation = _initializer.getNextSample(function.getBounds());
                particles.add(new Particle(initialLocation, function, globalBest));
            }
            while (function.getEvaluationsCount() < _evaluationsBudget) {
                for (Particle particle : particles) {
                    particle.move(_availableMoves[Generator.RANDOM.nextInt(_availableMoves.length)], particles);
                }
                if (_restartObserver.shouldBeRestarted(particles)) {
                    break;
                }
            }
            if (totalGlobalBest.getY() > globalBest.getY()) {
                totalGlobalBest.updateSample(globalBest);
            }
        }
        return totalGlobalBest;
    }

    @Override
    public long getPerformedEvaluations(Function function) {
        return 0;
    }
}
