package pl.edu.pw.mini.gapso.configuration;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import pl.edu.pw.mini.gapso.optimizer.move.*;

public class MoveConfiguration {

    private int minimalAmount;
    private boolean isAdaptable;

    private String name;
    private double initialWeight;
    private JsonElement parameters;

    public MoveConfiguration(String name, double initialWeight, int minimalAmount, boolean isAdaptable) {
        this(name, initialWeight, minimalAmount, isAdaptable, new Object());
    }

    public MoveConfiguration(String name, double initialWeight, int minimalAmount, boolean isAdaptable, Object parameters) {
        this.name = name;
        this.initialWeight = initialWeight;
        this.minimalAmount = minimalAmount;
        this.isAdaptable = isAdaptable;
        Gson gson = new Gson();
        this.parameters = gson.toJsonTree(parameters);
    }

    private double minimalRatio;

    public String getName() {
        return name;
    }

    public double getInitialWeight() {
        return initialWeight;
    }

    public double getMinimalRatio() {
        return minimalRatio;
    }

    public int getMinimalAmount() {
        return minimalAmount;
    }

    public boolean isAdaptable() {
        return isAdaptable;
    }

    public JsonElement getParameters() {
        return parameters;
    }

    public Move getMove() {
        if (getName().equals(DEBest1Bin.NAME)) {
            return new DEBest1Bin(this);
        }
        if (getName().equals(SHADE.NAME)) {
            return new SHADE(this);
        }
        if (getName().equals(CMAESApache.NAME)) {
            return new CMAESApache(this);
        }
        if (getName().equals(LocalBestModel.NAME)) {
            return new LocalBestModel(this);
        }
        if (getName().equals(GlobalModel.NAME)) {
            return new GlobalModel(this);
        }
        if (getName().equals(NearestSamplesModel.NAME)) {
            return new NearestSamplesModel(this);
        }
        throw new IllegalArgumentException("Unknown move " + getName());
    }
}
