package pso.coco;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Properties;

public class PropertiesBBOBExperimentConfigurator implements BBOBExperimentConfigurator {
    public static final String BBOB_PROPERTIES = "bbob.properties";
    public static final String GIT_PROPERTIES = "git.properties";
    public static final String GAPSO_JSON = "gapso.json";
    public static final String EXDATA = "exdata";
    public static final String GIT_COMMIT = "git-commit";
    public static final String EXPERIMENT_NAME = "experiment.name";
    public static final String BBOB_MAP_FUNCTION = "bbob.map.function";
    public static final String BBOB_FUNCTION = "bbob.function";
    public static final String BBOB_DIMENSIONS = "bbob.dimensions";
    public static final String BBOB_DEFAULT_PROPERTIES = "bbob.default.properties";

    private String bbobFunctionStr;
    private String dimensionsStr;
    private String experimentName;
    private boolean functionMappingExperiment;
    private String buildId;

    public PropertiesBBOBExperimentConfigurator() {
        final Properties properties = new Properties();
        try {
            try {
                InputStream inputStream = Files.newInputStream(Paths.get(BBOB_PROPERTIES), StandardOpenOption.READ);
                properties.load(inputStream);
                inputStream.close();
            } catch (IOException e1) {
                InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(BBOB_DEFAULT_PROPERTIES);
                assert inputStream != null;
                properties.load(inputStream);
                inputStream.close();
            }
            dimensionsStr = properties.getProperty(BBOB_DIMENSIONS);
            bbobFunctionStr = properties.getProperty(BBOB_FUNCTION);
            functionMappingExperiment = Boolean.parseBoolean(properties.getProperty(BBOB_MAP_FUNCTION));
            experimentName = properties.getProperty(EXPERIMENT_NAME);
            try {
                InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(GIT_PROPERTIES);
                Properties gitProperties = new Properties();
                assert inputStream != null;
                gitProperties.load(inputStream);
                buildId = gitProperties.getProperty(GIT_COMMIT);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getExperimentName() {
        return experimentName;
    }

    @Override
    public String getBuildId() {
        return buildId;
    }

    @Override
    public boolean isFunctionMappingExperiment() {
        return functionMappingExperiment;
    }

    @Override
    public String[] getFunctionsList() {
        return bbobFunctionStr
                .split(",");

    }

    @Override
    public int[] getDimensionsList() {
        return Arrays.stream(
                dimensionsStr
                        .split(","))
                .mapToInt(Integer::parseInt)
                .toArray()
                ;
    }

}
