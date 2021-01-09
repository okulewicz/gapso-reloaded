package pl.edu.pw.mini.gapso.configuration;

import org.junit.Assert;
import org.junit.Test;

public class ConfigurationTest {
    String JSONString = "{\n" +
            "  \"seed\": 1,\n" +
            "  \"moves\": [\n" +
            "    {\n" +
            "      \"name\": \"DE/rand/1/bin\",\n" +
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
        Assert.assertNotNull(configuration.getMoves());
        Assert.assertEquals(1, configuration.getMoves().length);
        MoveConfiguration[] moveConfiguration = configuration.getMoves();
        Assert.assertEquals("DE/rand/1/bin",
                moveConfiguration[0].getName());
        Assert.assertTrue(moveConfiguration[0].isAdaptable());
        Assert.assertEquals(1000,
                moveConfiguration[0].getInitialWeight(), 0.0);
        Assert.assertEquals(1,
                moveConfiguration[0].getMinimalAmount());
        Assert.assertEquals(0,
                moveConfiguration[0].getMinimalRatio(), 0.0);
        Assert.assertEquals(0.9,
                moveConfiguration[0].getParameters().get("crossProb"), 0.0);
        Assert.assertEquals(0.5,
                moveConfiguration[0].getParameters().get("scale"), 0.0);

    }
}