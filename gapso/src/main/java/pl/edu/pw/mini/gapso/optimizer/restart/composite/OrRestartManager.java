package pl.edu.pw.mini.gapso.optimizer.restart.composite;

import com.google.gson.JsonElement;
import pl.edu.pw.mini.gapso.optimizer.Particle;
import pl.edu.pw.mini.gapso.optimizer.restart.RestartManager;

import java.util.List;

public class OrRestartManager extends CompositeLogicRestartManager {
    public static final String NAME = "Or";

    public OrRestartManager(List<RestartManager> restartManagers) {
        super(restartManagers);
    }

    public OrRestartManager(JsonElement configuration) {
        super(configuration);
    }

    //TODO: needs to be tested
    @Override
    public boolean shouldBeRestarted(List<Particle> particleList) {
        return restartManagers.stream().anyMatch(rm -> rm.shouldBeRestarted(particleList));
    }
}
