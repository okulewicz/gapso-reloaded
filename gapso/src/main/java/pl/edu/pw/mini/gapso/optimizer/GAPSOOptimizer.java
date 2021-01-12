package pl.edu.pw.mini.gapso.optimizer;

import pl.edu.pw.mini.gapso.configuration.Configuration;
import pl.edu.pw.mini.gapso.function.Function;
import pl.edu.pw.mini.gapso.initializer.Initializer;
import pl.edu.pw.mini.gapso.optimizer.move.Move;
import pl.edu.pw.mini.gapso.optimizer.move.MoveManager;
import pl.edu.pw.mini.gapso.optimizer.restart.RestartManager;
import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.sample.Sampler;
import pl.edu.pw.mini.gapso.sample.UpdatableSample;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GAPSOOptimizer extends SamplingOptimizer {
    private final List<Sampler> samplers = new ArrayList<>();

    private final int _particlesCountPerDimension;
    private final int _evaluationsBudgetPerDimension;
    private final Move[] _availableMoves;
    private final Initializer _initializer;
    private final RestartManager _restartManager;

    @Override
    public void registerSampler(Sampler sampler) {
        samplers.add(sampler);
    }

    public GAPSOOptimizer(int particlesCount, int evaluationsBudget, Move[] availableMoves, Initializer initializer, RestartManager restartManager) {
        _particlesCountPerDimension = particlesCount;
        _evaluationsBudgetPerDimension = evaluationsBudget;
        _availableMoves = availableMoves;
        _initializer = initializer;
        _restartManager = restartManager;
    }

    public GAPSOOptimizer() {
        this(Configuration.getInstance().getParticlesCountPerDimension(),
                Configuration.getInstance().getEvaluationsBudgetPerDimension(),
                Configuration.getInstance().getMoves(),
                Configuration.getInstance().getInitializer(),
                Configuration.getInstance().getRestartManager());
    }

    @Override
    public Sample optimize(Function function) {
        resetAndConfigureBeforeOptimization();
        Function functionWrapper = createSamplingWrapper(function, samplers);
        UpdatableSample totalGlobalBest = UpdatableSample.generateInitialSample(functionWrapper.getDimension());
        MoveManager moveManager = new MoveManager(_availableMoves);
        while (isEnoughOptimizationBudgetLeftAndNeedsOptimization(functionWrapper)) {
            UpdatableSample globalBest = UpdatableSample.generateInitialSample(functionWrapper.getDimension());
            Particle.IndexContainer indexContainer = new Particle.IndexContainer();
            List<Particle> particles = new ArrayList<>();
            for (int i = 0; i < _particlesCountPerDimension * functionWrapper.getDimension(); ++i) {
                assert _initializer.canSample();
                double[] initialLocation = _initializer.getNextSample(functionWrapper.getBounds());
                new Particle(initialLocation, functionWrapper, globalBest, indexContainer, particles);
            }
            while (isEnoughOptimizationBudgetLeftAndNeedsOptimization(functionWrapper)) {
                List<Move> moves = moveManager.generateMoveSequence(particles.size());
                Iterator<Move> movesIterator = moves.iterator();
                for (Particle particle : particles) {
                    Move selectedMove = movesIterator.next();
                    particle.move(selectedMove);
                }
                if (_restartManager.shouldBeRestarted(particles)) {
                    resetAfterOptimizationRestart();
                    break;
                }
            }
            if (totalGlobalBest.getY() > globalBest.getY()) {
                totalGlobalBest.updateSample(globalBest);
            }
        }
        return totalGlobalBest;
    }

    private void resetAndConfigureBeforeOptimization() {
        samplers.clear();
        _initializer.resetInitializer(true);
        _initializer.registerObjectsWithOptimizer(this);
    }

    private void resetAfterOptimizationRestart() {
        samplers.clear();
        _initializer.resetInitializer(false);
        _initializer.registerObjectsWithOptimizer(this);
    }

    private boolean isEnoughOptimizationBudgetLeftAndNeedsOptimization(Function function) {
        return function.getEvaluationsCount() < _evaluationsBudgetPerDimension * function.getDimension()
                && !function.isTargetReached();
    }
}
