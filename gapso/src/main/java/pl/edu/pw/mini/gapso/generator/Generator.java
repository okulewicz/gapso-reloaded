package pl.edu.pw.mini.gapso.generator;

import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import pl.edu.pw.mini.gapso.configuration.Configuration;

public class Generator {
    public static final RandomGenerator RANDOM =
            new JDKRandomGenerator(Configuration.getInstance().getSeed());
}
