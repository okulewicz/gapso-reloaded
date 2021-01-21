package pl.edu.pw.mini.gapso.optimizer;

import pl.edu.pw.mini.gapso.bounds.Bounds;
import pl.edu.pw.mini.gapso.configuration.Configuration;
import pl.edu.pw.mini.gapso.function.Function;
import pl.edu.pw.mini.gapso.initializer.BoundsManager;
import pl.edu.pw.mini.gapso.initializer.Initializer;
import pl.edu.pw.mini.gapso.optimizer.move.Move;
import pl.edu.pw.mini.gapso.optimizer.move.MoveManager;
import pl.edu.pw.mini.gapso.optimizer.restart.RestartManager;
import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.sample.UpdatableSample;
import pl.edu.pw.mini.gapso.sample.sampler.Sampler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GAPSOOptimizer extends SamplingOptimizer {
    private final List<Sampler> samplers = new ArrayList<>();
    private final List<Sampler> successSamplers = new ArrayList<>();

    private final int _particlesCountPerDimension;
    private final int _evaluationsBudgetPerDimension;
    private final Move[] _availableMoves;
    private final Initializer _initializer;
    private final RestartManager _restartManager;
    private final BoundsManager _boundsManager;
    private UpdatableSample totalGlobalBest;
    private MoveManager moveManager;
    private Bounds bounds;

    @Override
    public void registerSampler(Sampler sampler) {
        samplers.add(sampler);
    }

    @Override
    public void registerSuccessSampler(Sampler sampler) {
        successSamplers.add(sampler);
    }

    public GAPSOOptimizer(int particlesCount, int evaluationsBudget, Move[] availableMoves, Initializer initializer, RestartManager restartManager, BoundsManager boundsManager) {
        _particlesCountPerDimension = particlesCount;
        _evaluationsBudgetPerDimension = evaluationsBudget;
        _availableMoves = availableMoves;
        _initializer = initializer;
        _restartManager = restartManager;
        _boundsManager = boundsManager;
    }

    public GAPSOOptimizer() {
        this(Configuration.getInstance().getParticlesCountPerDimension(),
                Configuration.getInstance().getEvaluationsBudgetPerDimension(),
                Configuration.getInstance().getMoves(),
                Configuration.getInstance().getInitializer(),
                Configuration.getInstance().getRestartManager(),
                Configuration.getInstance().getBoundsManager());
    }

    @Override
    public Sample optimize(Function function) {
        totalGlobalBest = UpdatableSample.generateInitialSample(function.getDimension());
        _boundsManager.setInitialBounds(function.getBounds());
        final int particleCount = _particlesCountPerDimension * function.getDimension();
        resetAndConfigureBeforeOptimization(particleCount);
        bounds = function.getBounds();
        while (isEnoughOptimizationBudgetLeftAndNeedsOptimization(function)) {
            Function functionWrapper = createSamplingWrapper(function, samplers, bounds);
            UpdatableSample globalBest = UpdatableSample.generateInitialSample(functionWrapper.getDimension());
            Particle.IndexContainer indexContainer = new Particle.IndexContainer();
            List<Particle> particles = new ArrayList<>();
            for (int i = 0; i < particleCount; ++i) {
                assert _initializer.canSample();
                double[] initialLocation = _initializer.getNextSample(functionWrapper.getBounds());
                new Particle(initialLocation, functionWrapper, globalBest, indexContainer, particles);
            }
            while (isEnoughOptimizationBudgetLeftAndNeedsOptimization(function)) {
                List<Move> moves = moveManager.generateMoveSequence(particles.size());
                Iterator<Move> movesIterator = moves.iterator();
                for (Particle particle : particles) {
                    Move selectedMove = movesIterator.next();
                    double globalBestValue = globalBest.getY();
                    double personalBestValue = particle.getBest().getY();
                    particle.move(selectedMove);
                    final Sample newPersonalBest = particle.getBest();
                    double newPersonalBestValue = newPersonalBest.getY();
                    if (newPersonalBestValue < personalBestValue) {
                        successSamplers.forEach(s -> s.tryStoreSample(newPersonalBest));
                    }
                    moveManager.registerPersonalImprovementByMove(selectedMove, personalBestValue - newPersonalBestValue);
                    moveManager.registerGlobalImprovementByMove(selectedMove, globalBestValue - newPersonalBestValue);
                }
                if (_restartManager.shouldBeRestarted(particles)) {
                    _boundsManager.registerOptimumLocation(globalBest);
                    tryUpdateTotalGlobalBest(totalGlobalBest, globalBest);
                    resetAfterOptimizationRestart();
                    break;
                }
            }
            tryUpdateTotalGlobalBest(totalGlobalBest, globalBest);
        }
        return totalGlobalBest;
    }

    private void tryUpdateTotalGlobalBest(UpdatableSample totalGlobalBest, UpdatableSample globalBest) {
        if (totalGlobalBest.getY() > globalBest.getY()) {
            totalGlobalBest.updateSample(globalBest);
        }
    }

    private void resetAndConfigureBeforeOptimization(int particleCount) {
        //TODO: this needs to be tested somehow
        samplers.clear();
        successSamplers.clear();
        _initializer.resetInitializer(true);
        _initializer.registerObjectsWithOptimizer(this);
        _boundsManager.resetManager();
        _boundsManager.registerObjectsWithOptimizer(this);
        for (Move move : _availableMoves) {
            move.resetState(particleCount);
            move.registerObjectsWithOptimizer(this);
        }
        bounds = _boundsManager.getBounds();
        moveManager = new MoveManager(_availableMoves);
    }

    private void resetAfterOptimizationRestart() {
        //TODO: this needs to be tested somehow
        samplers.clear();
        successSamplers.clear();
        _initializer.resetInitializer(true);
        _initializer.registerObjectsWithOptimizer(this);
        bounds = _boundsManager.getBounds();
        _restartManager.reset();
    }

    private boolean isEnoughOptimizationBudgetLeftAndNeedsOptimization(Function function) {
        return function.getEvaluationsCount() < _evaluationsBudgetPerDimension * function.getDimension()
                && !function.isTargetReached();
    }
}
