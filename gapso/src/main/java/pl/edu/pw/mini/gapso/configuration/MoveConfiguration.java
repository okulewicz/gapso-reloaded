package pl.edu.pw.mini.gapso.configuration;

import com.google.gson.JsonElement;

public class MoveConfiguration {
    @SuppressWarnings("unused")
    private String name;
    @SuppressWarnings("unused")
    private double initialWeight;
    @SuppressWarnings("unused")
    private double minimalRatio;
    @SuppressWarnings("unused")
    private int minimalAmount;
    @SuppressWarnings("unused")
    private boolean isAdaptable;
    @SuppressWarnings("unused")
    private JsonElement parameters;

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

}
