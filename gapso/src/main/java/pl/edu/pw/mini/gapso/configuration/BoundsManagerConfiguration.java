package pl.edu.pw.mini.gapso.configuration;

import com.google.gson.JsonElement;
import pl.edu.pw.mini.gapso.bounds.BoundsManager;
import pl.edu.pw.mini.gapso.bounds.GlobalModelBoundsManager;
import pl.edu.pw.mini.gapso.bounds.RandomRegionBoundsManager;
import pl.edu.pw.mini.gapso.bounds.ResetAllBoundsManager;

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
        if (getName().equals(RandomRegionBoundsManager.NAME)) {
            return new RandomRegionBoundsManager(this);
        }
        throw new IllegalArgumentException("Unknown initializer " + getName());
    }
}
