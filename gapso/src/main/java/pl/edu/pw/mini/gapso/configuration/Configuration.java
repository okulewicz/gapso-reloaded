package pl.edu.pw.mini.gapso.configuration;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import pl.edu.pw.mini.gapso.bounds.BoundsManager;
import pl.edu.pw.mini.gapso.initializer.Initializer;
import pl.edu.pw.mini.gapso.optimizer.move.MoveManager;
import pl.edu.pw.mini.gapso.optimizer.restart.RestartManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Configuration {
    private static Configuration configuration;
    private final static Gson gson = new Gson();
    @SuppressWarnings("unused")
    private int seed;
    @SuppressWarnings("unused")
    private int particlesCountPerDimension;
    @SuppressWarnings("unused")
    private int evaluationsBudgetPerDimension;
    @SuppressWarnings("unused")
    private BoundsManagerConfiguration boundsManagerDefinition;

    @SuppressWarnings("unused")
    private RestartConfiguration restartManagerDefinition;
    @SuppressWarnings("unused")
    private InitializerConfiguration initializerDefinition;

    private MoveManagerConfiguration moveManagerDefinition;

    public int getEvaluationsBudgetPerDimension() {
        return evaluationsBudgetPerDimension;
    }


    public static Configuration getInstance() {
        if (configuration == null) {
            try (InputStream customSettingsInputStream =
                         Files.newInputStream(
                                 Paths.get("gapso.json"), StandardOpenOption.READ)) {
                configuration = generateFromJSONString(IOUtils.toString(customSettingsInputStream));
            } catch (IOException e1) {
                try (InputStream defaultSettingsInputStream =
                             Thread.currentThread()
                                     .getContextClassLoader()
                                     .getResourceAsStream("gapso.default.json")) {
                    assert defaultSettingsInputStream != null;
                    configuration = generateFromJSONString(IOUtils.toString(defaultSettingsInputStream));
                } catch (IOException | NullPointerException | AssertionError e2) {
                    e2.printStackTrace(System.err);
                }
            }

        }
        return configuration;
    }

    public static Configuration generateFromJSONString(String JSONString) {
        return gson.fromJson(JSONString, Configuration.class);
    }

    public int getSeed() {
        return seed;
    }

    public int getParticlesCountPerDimension() {
        return particlesCountPerDimension;
    }

    public Initializer getInitializer() {
        return initializerDefinition.getInitializer();
    }

    public RestartManager getRestartManager() {
        return restartManagerDefinition.getManager();
    }


    public BoundsManager getBoundsManager() {
        return boundsManagerDefinition.getBoundsManager();
    }

    public MoveManager getMoveManager() {
        return moveManagerDefinition.getMoveManager();
    }
}
