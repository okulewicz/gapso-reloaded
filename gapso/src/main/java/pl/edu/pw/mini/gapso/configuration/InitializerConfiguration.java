package pl.edu.pw.mini.gapso.configuration;

import com.google.gson.JsonElement;

public class InitializerConfiguration {
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
}
