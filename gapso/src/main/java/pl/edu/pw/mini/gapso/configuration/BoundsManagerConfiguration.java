package pl.edu.pw.mini.gapso.configuration;

import com.google.gson.JsonElement;
import pl.edu.pw.mini.gapso.initializer.BoundsManager;
import pl.edu.pw.mini.gapso.initializer.GlobalModelBoundsManager;
import pl.edu.pw.mini.gapso.optimizer.ResetAllBoundsManager;

public class BoundsManagerConfiguration {
    String name;
    JsonElement parameters;

    public BoundsManagerConfiguration(String name, JsonElement parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public JsonElement getParameters() {
        return parameters;
    }

    public BoundsManager getBoundsManager() {
        if (getName().equals(GlobalModelBoundsManager.NAME)) {
            return new GlobalModelBoundsManager(this);
        }
        if (getName().equals(ResetAllBoundsManager.NAME)) {
            return new ResetAllBoundsManager(this);
        }
        throw new IllegalArgumentException("Unknown initializer " + getName());
    }
}
