package pl.edu.pw.mini.gapso.configuration;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Configuration {
    private static Configuration configuration;
    private int seed;
    private MoveConfiguration[] moves;

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
        Gson gson = new Gson();
        return gson.fromJson(JSONString, Configuration.class);
    }

    public int getSeed() {
        return seed;
    }

    public MoveConfiguration[] getMoves() {
        return moves;
    }

}
