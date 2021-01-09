package pl.edu.pw.mini.gapso.configuration;

import java.util.Map;

public class MoveConfiguration {
    private String name;
    private double initialWeight;
    private double minimalRatio;
    private int minimalAmount;
    private boolean isAdaptable;
    private Map<String, Double> parameters;

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

    public Map<String, Double> getParameters() {
        return parameters;
    }

}
