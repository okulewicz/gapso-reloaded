package pl.edu.pw.mini.gapso.optimizer;

import pl.edu.pw.mini.gapso.configuration.Configuration;
import pl.edu.pw.mini.gapso.function.Function;
import pl.edu.pw.mini.gapso.generator.initializer.Initializer;
import pl.edu.pw.mini.gapso.optimizer.move.Move;
import pl.edu.pw.mini.gapso.optimizer.move.MoveManager;
import pl.edu.pw.mini.gapso.optimizer.restart.RestartManager;
import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.sample.UpdatableSample;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GAPSOOptimizer extends Optimizer {
    private final int _particlesCountPerDimension;
    private final int _evaluationsBudgetPerDimension;
    private final Move[] _availableMoves;
    private final Initializer _initializer;
    private final RestartManager _restartManager;

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
        UpdatableSample totalGlobalBest = UpdatableSample.generateInitialSample(function.getDimension());
        MoveManager moveManager = new MoveManager(_availableMoves);
        while (isEnoughOptimizationBudgetLeftAndNeedsOptimzation(function)) {
            UpdatableSample globalBest = UpdatableSample.generateInitialSample(function.getDimension());
            Particle.IndexContainer indexContainer = new Particle.IndexContainer();
            List<Particle> particles = new ArrayList<>();
            for (int i = 0; i < _particlesCountPerDimension * function.getDimension(); ++i) {
                double[] initialLocation = _initializer.getNextSample(function.getBounds());
                new Particle(initialLocation, function, globalBest, indexContainer, particles);
            }
            while (isEnoughOptimizationBudgetLeftAndNeedsOptimzation(function)) {
                List<Move> moves = moveManager.generateMoveSequence(particles.size());
                Iterator<Move> movesIterator = moves.iterator();
                for (Particle particle : particles) {
                    Move selectedMove = movesIterator.next();
                    particle.move(selectedMove);
                }
                if (_restartManager.shouldBeRestarted(particles)) {
                    break;
                }
            }
            if (totalGlobalBest.getY() > globalBest.getY()) {
                totalGlobalBest.updateSample(globalBest);
            }
        }
        return totalGlobalBest;
    }

    private boolean isEnoughOptimizationBudgetLeftAndNeedsOptimzation(Function function) {
        return function.getEvaluationsCount() < _evaluationsBudgetPerDimension * function.getDimension()
                && !function.isTargetReached();
    }
}
