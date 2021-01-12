package pl.edu.pw.mini.gapso.generator.initializer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pl.edu.pw.mini.gapso.bounds.Bounds;
import pl.edu.pw.mini.gapso.bounds.SimpleBounds;

import java.util.ArrayList;
import java.util.List;

public class SequenceInitializerTest {
    private Bounds bounds;
    private Initializer randomInitializer;

    @Before
    public void setupTestObjects() {
        bounds = new SimpleBounds(
                new double[]{-1.0, -1.0},
                new double[]{2.0, -2.0}
        );
        randomInitializer = new RandomInitializer();
    }

    @Test
    public void getNextSample() {
        Initializer sequenceInitializer = createSequenceInitializerOnRandomInitializer();
        for (int i = 0; i < 1000; ++i) {
            double[] sample = sequenceInitializer.getNextSample(bounds);
            Assert.assertTrue(bounds.contain(sample));
        }
    }

    @Test
    public void canSample() {
        Initializer sequenceInitializer = new SequenceInitializer(new ArrayList<>());
        Assert.assertFalse(sequenceInitializer.canSample());
        sequenceInitializer = createSequenceInitializerOnRandomInitializer();
        Assert.assertTrue(sequenceInitializer.canSample());
    }

    private Initializer createSequenceInitializerOnRandomInitializer() {
        Initializer sequenceInitializer;
        List<Initializer> sequence = new ArrayList<>();
        sequence.add(randomInitializer);
        sequenceInitializer = new SequenceInitializer(sequence);
        return sequenceInitializer;
    }
}