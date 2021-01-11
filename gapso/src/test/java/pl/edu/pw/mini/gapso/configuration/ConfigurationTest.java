package pl.edu.pw.mini.gapso.configuration;

import org.junit.Assert;
import org.junit.Test;
import pl.edu.pw.mini.gapso.generator.initializer.RandomInitializer;
import pl.edu.pw.mini.gapso.optimization.move.DEBest1Bin;
import pl.edu.pw.mini.gapso.optimization.move.Move;
import pl.edu.pw.mini.gapso.optimizer.restart.MinSpreadInDimensionsRestartManager;

public class ConfigurationTest {
    String JSONString = "{\n" +
            "  \"seed\": 1,\n" +
            "  \"particlesCountPerDimension\": 10,\n" +
            "  \"evaluationsBudgetPerDimension\": 1000,\n" +
            "  \"initializerDefinition\":\n" +
            "    {\n" +
            "      \"name\": \"Random\"\n" +
            "    }\n" +
            "  ,\n" +
            "  \"restartManagerDefinition\":\n" +
            "    {\n" +
            "      \"name\": \"MinSpreadInDimensions\",\n" +
            "      \"parameters\": {\n" +
            "        \"threshold\": 1e-4\n" +
            "      }\n" +
            "    }\n" +
            "  ,\n" +
            "  \"moveDefinition\": [\n" +
            "    {\n" +
            "      \"name\": \"DE/best/1/bin\",\n" +
            "      \"isAdaptable\": true,\n" +
            "      \"initialWeight\": 1000,\n" +
            "      \"minimalAmount\": 1,\n" +
            "      \"parameters\": {\n" +
            "        \"crossProb\": 0.9,\n" +
            "        \"scale\": 0.5\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    @Test
    public void generateFromJSONString() {
        Configuration configuration = Configuration.generateFromJSONString(JSONString);
        Assert.assertEquals(1, configuration.getSeed());
        Assert.assertEquals(10, configuration.getParticlesCountPerDimension());
        Assert.assertEquals(1000, configuration.getEvaluationsBudgetPerDimension());
        Assert.assertNotNull(configuration.getMoves());
        Assert.assertEquals(1, configuration.getMoves().length);
        Move[] moves = configuration.getMoves();
        Assert.assertEquals(DEBest1Bin.class,
                moves[0].getClass());
        Assert.assertNotNull(configuration.getInitializer());
        Assert.assertEquals(MinSpreadInDimensionsRestartManager.class,
                configuration.getRestartManager().getClass());
        Assert.assertNotNull(configuration.getInitializer());
        Assert.assertEquals(
                RandomInitializer.class,
                configuration.getInitializer().getClass());
    }
}