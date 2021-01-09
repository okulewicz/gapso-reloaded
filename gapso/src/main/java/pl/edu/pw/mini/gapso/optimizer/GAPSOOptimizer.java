package pl.edu.pw.mini.gapso.optimizer;

import pl.edu.pw.mini.gapso.function.Function;
import pl.edu.pw.mini.gapso.generator.initializer.Initializer;
import pl.edu.pw.mini.gapso.optimization.move.Move;
import pl.edu.pw.mini.gapso.optimizer.restart.RestartObserver;
import pl.edu.pw.mini.gapso.sample.Sample;

public class GAPSOOptimizer extends Optimizer {
    public GAPSOOptimizer(int particlesCount, int evaluationsBudget, Move[] availableMoves, Initializer initializer, RestartObserver restartObserver) {

    }

    @Override
    public Sample optimize(Function function) {
        return null;
    }

    @Override
    public long getPerformedEvaluations(Function function) {
        return 0;
    }
}
