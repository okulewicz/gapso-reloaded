package pl.edu.pw.mini.gapso.configuration;

import com.google.gson.JsonElement;
import pl.edu.pw.mini.gapso.optimizer.restart.RestartManager;
import pl.edu.pw.mini.gapso.optimizer.restart.composite.AndRestartManager;
import pl.edu.pw.mini.gapso.optimizer.restart.composite.OrRestartManager;
import pl.edu.pw.mini.gapso.optimizer.restart.counter.NoImprovementRestartManager;
import pl.edu.pw.mini.gapso.optimizer.restart.threshold.CurrentFunctionValueSpreadRestartManager;
import pl.edu.pw.mini.gapso.optimizer.restart.threshold.FunctionValueSpreadRestartManager;
import pl.edu.pw.mini.gapso.optimizer.restart.threshold.LargestSpreadBelowThresholdRestartManager;
import pl.edu.pw.mini.gapso.optimizer.restart.threshold.SmallestSpreadBelowThresholdRestartManager;

public class RestartConfiguration {
    @SuppressWarnings("unused")
    private String name;
    @SuppressWarnings("unused")
    private JsonElement parameters;

    public String getName() {
        return name;
    }

    public JsonElement getParameters() {
        return parameters;
    }

    public RestartManager getManager() {
        if (getName().equals(SmallestSpreadBelowThresholdRestartManager.NAME)) {
            return new SmallestSpreadBelowThresholdRestartManager(getParameters());
        }
        if (getName().equals(LargestSpreadBelowThresholdRestartManager.NAME)) {
            return new LargestSpreadBelowThresholdRestartManager(getParameters());
        }
        if (getName().equals(FunctionValueSpreadRestartManager.NAME)) {
            return new FunctionValueSpreadRestartManager(getParameters());
        }
        if (getName().equals(AndRestartManager.NAME)) {
            return new AndRestartManager(getParameters());
        }
        if (getName().equals(OrRestartManager.NAME)) {
            return new OrRestartManager(getParameters());
        }
        if (getName().equals(NoImprovementRestartManager.NAME)) {
            return new NoImprovementRestartManager(getParameters());
        }
        if (getName().equals(CurrentFunctionValueSpreadRestartManager.NAME)) {
            return new CurrentFunctionValueSpreadRestartManager(getParameters());
        }
        throw new IllegalArgumentException("Restart manager " + getName() + " is not known");

    }
}
