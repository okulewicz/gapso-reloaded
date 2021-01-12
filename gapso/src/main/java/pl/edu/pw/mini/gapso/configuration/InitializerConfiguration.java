package pl.edu.pw.mini.gapso.configuration;

import com.google.gson.JsonElement;
import pl.edu.pw.mini.gapso.initializer.Initializer;
import pl.edu.pw.mini.gapso.initializer.ModelInitializer;
import pl.edu.pw.mini.gapso.initializer.RandomInitializer;
import pl.edu.pw.mini.gapso.initializer.SequenceInitializer;

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

    public Initializer getInitializer() {
        if (getName().equals(RandomInitializer.NAME)) {
            return new RandomInitializer();
        }
        if (getName().equals(ModelInitializer.NAME)) {
            return new ModelInitializer();
        }
        if (getName().equals(SequenceInitializer.NAME)) {
            return new SequenceInitializer(this);
        }
        throw new IllegalArgumentException("Unknown initializer " + getName());
    }
}
