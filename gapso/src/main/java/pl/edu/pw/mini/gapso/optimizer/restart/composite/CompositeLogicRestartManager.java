package pl.edu.pw.mini.gapso.optimizer.restart.composite;

import com.google.gson.JsonElement;
import pl.edu.pw.mini.gapso.configuration.RestartConfiguration;
import pl.edu.pw.mini.gapso.optimizer.restart.RestartManager;
import pl.edu.pw.mini.gapso.utils.Util;

import java.util.List;
import java.util.stream.Collectors;

public abstract class CompositeLogicRestartManager extends RestartManager {
    protected List<RestartManager> restartManagers;

    public CompositeLogicRestartManager(List<RestartManager> restartManagers) {
        this.restartManagers = restartManagers;
    }

    public CompositeLogicRestartManager(JsonElement configuration) {
        this(
                Util.GSON.fromJson(configuration, CompositeLogicRestartManagerConfiguration.class)
                        .getRestartManagerDefinitions());
    }

    private static class CompositeLogicRestartManagerConfiguration {
        private List<RestartConfiguration> restartManagerDefinitions;

        private CompositeLogicRestartManagerConfiguration(List<RestartConfiguration> restartManagerDefinitions) {
            this.restartManagerDefinitions = restartManagerDefinitions;
        }

        public List<RestartManager> getRestartManagerDefinitions() {
            return restartManagerDefinitions.stream().map(RestartConfiguration::getManager).collect(Collectors.toList());
        }
    }

}
