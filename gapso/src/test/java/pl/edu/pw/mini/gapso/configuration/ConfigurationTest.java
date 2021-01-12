package pl.edu.pw.mini.gapso.configuration;

import org.junit.Assert;
import org.junit.Test;
import pl.edu.pw.mini.gapso.initializer.RandomInitializer;
import pl.edu.pw.mini.gapso.initializer.SequenceInitializer;
import pl.edu.pw.mini.gapso.optimizer.move.DEBest1Bin;
import pl.edu.pw.mini.gapso.optimizer.move.LocalBestModel;
import pl.edu.pw.mini.gapso.optimizer.move.Move;
import pl.edu.pw.mini.gapso.optimizer.restart.threshold.SmallestSpreadBelowThresholdRestartManager;

public class ConfigurationTest {
    String JSONStringVer1 = "{\n" +
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

    String JSONStringWithSeqInit = "{\n" +
            "  \"seed\": 1,\n" +
            "  \"particlesCountPerDimension\": 10,\n" +
            "  \"evaluationsBudgetPerDimension\": 100,\n" +
            "  \"initializerDefinition\": {\n" +
            "    \"name\": \"Sequence\",\n" +
            "    \"parameters\": {\n" +
            "      \"initializers\": [\n" +
            "        {\n" +
            "          \"name\": \"Model\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Random\"\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  },\n" +
            "  \"restartManagerDefinition\": {\n" +
            "    \"name\": \"MinSpreadInDimensions\",\n" +
            "    \"parameters\": {\n" +
            "      \"threshold\": 1e-4\n" +
            "    }\n" +
            "  },\n" +
            " \"moveDefinition\": [\n" +
            "    {\n" +
            "      \"name\": \"DE/best/1/bin\",\n" +
            "      \"isAdaptable\": true,\n" +
            "      \"initialWeight\": 1000,\n" +
            "      \"minimalAmount\": 1,\n" +
            "      \"parameters\": {\n" +
            "        \"crossProb\": 1.0,\n" +
            "        \"scale\": 1.2\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"LocalBestModel\",\n" +
            "      \"isAdaptable\": false,\n" +
            "      \"initialWeight\": 0,\n" +
            "      \"minimalAmount\": 1,\n" +
            "      \"parameters\": {}\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    @Test
    public void generateFromJSONStringWithSeqInit() {
        Configuration configuration = Configuration.generateFromJSONString(JSONStringWithSeqInit);
        Assert.assertEquals(1, configuration.getSeed());
        Assert.assertEquals(10, configuration.getParticlesCountPerDimension());
        Assert.assertEquals(100, configuration.getEvaluationsBudgetPerDimension());
        Assert.assertNotNull(configuration.getMoves());
        Assert.assertEquals(2, configuration.getMoves().length);
        Move[] moves = configuration.getMoves();
        Assert.assertEquals(DEBest1Bin.class,
                moves[0].getClass());
        Assert.assertEquals(LocalBestModel.class,
                moves[1].getClass());
        Assert.assertNotNull(configuration.getRestartManager());
        Assert.assertEquals(SmallestSpreadBelowThresholdRestartManager.class,
                configuration.getRestartManager().getClass());
        Assert.assertNotNull(configuration.getInitializer());
        Assert.assertEquals(
                SequenceInitializer.class,
                configuration.getInitializer().getClass());
    }

    @Test
    public void generateFromJSONStringVer1() {
        Configuration configuration = Configuration.generateFromJSONString(JSONStringVer1);
        Assert.assertEquals(1, configuration.getSeed());
        Assert.assertEquals(10, configuration.getParticlesCountPerDimension());
        Assert.assertEquals(1000, configuration.getEvaluationsBudgetPerDimension());
        Assert.assertNotNull(configuration.getMoves());
        Assert.assertEquals(1, configuration.getMoves().length);
        Move[] moves = configuration.getMoves();
        Assert.assertEquals(DEBest1Bin.class,
                moves[0].getClass());
        Assert.assertNotNull(configuration.getRestartManager());
        Assert.assertEquals(SmallestSpreadBelowThresholdRestartManager.class,
                configuration.getRestartManager().getClass());
        Assert.assertNotNull(configuration.getInitializer());
        Assert.assertEquals(
                RandomInitializer.class,
                configuration.getInitializer().getClass());
    }
}