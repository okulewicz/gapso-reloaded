package pso.coco;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Properties;

public class PropertiesBBOBExperimentConfigurator implements BBOBExperimentConfigurator {
    private String bbobFunctionStr;
    private String dimensionsStr;
    private String experimentName;
    private boolean functionMappingExperiment;

    public PropertiesBBOBExperimentConfigurator() {
        final Properties properties = new Properties();
        try {
            try {
                InputStream inputStream = Files.newInputStream(Paths.get("bbob.properties"), StandardOpenOption.READ);
                properties.load(inputStream);
                inputStream.close();
            } catch (IOException e1) {
                InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("bbob.default.properties");
                properties.load(inputStream);
                inputStream.close();
            }
            dimensionsStr = properties.getProperty("bbob.dimensions");
            bbobFunctionStr = properties.getProperty("bbob.function");
            functionMappingExperiment = Boolean.parseBoolean(properties.getProperty("bbob.map.function"));

            experimentName = properties.getProperty("experiment.name");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getExperimentName() {
        return experimentName;
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
                .mapToInt(item -> Integer.parseInt(item))
                .toArray()
                ;
    }

}
