package pl.edu.pw.mini.gapso.optimizer.restart.composite;

import com.google.gson.JsonElement;
import pl.edu.pw.mini.gapso.optimizer.Particle;
import pl.edu.pw.mini.gapso.optimizer.restart.RestartManager;

import java.util.List;

public class AndRestartManager extends CompositeLogicRestartManager {
    public static final String NAME = "And";

    public AndRestartManager(List<RestartManager> restartManagers) {
        super(restartManagers);
    }

    public AndRestartManager(JsonElement configuration) {
        super(configuration);
    }

    //TODO: needs to be tested
    @Override
    public boolean shouldBeRestarted(List<Particle> particleList) {
        return restartManagers.stream().allMatch(rm -> rm.shouldBeRestarted(particleList));
    }
}
