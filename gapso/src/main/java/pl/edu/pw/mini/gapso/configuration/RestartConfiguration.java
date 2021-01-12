package pl.edu.pw.mini.gapso.configuration;

import com.google.gson.JsonElement;
import pl.edu.pw.mini.gapso.optimizer.restart.LargestSpreadBelowThresholdRestartManager;
import pl.edu.pw.mini.gapso.optimizer.restart.RestartManager;
import pl.edu.pw.mini.gapso.optimizer.restart.SmallestSpreadBelowThresholdRestartManager;

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
        throw new IllegalArgumentException("Restart manager " + getName() + " is not known");

    }
}
