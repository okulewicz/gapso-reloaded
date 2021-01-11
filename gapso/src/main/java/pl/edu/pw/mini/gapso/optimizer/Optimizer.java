package pl.edu.pw.mini.gapso.optimizer;

import pl.edu.pw.mini.gapso.function.Function;
import pl.edu.pw.mini.gapso.sample.Sample;

public abstract class Optimizer {
    public abstract Sample optimize(Function function);
}
