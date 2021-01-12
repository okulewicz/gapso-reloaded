package pl.edu.pw.mini.gapso.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;

public class GeneratorTest {

    @Test
    public void getUniqueIntegerSequence() {
        for (int i = 0; i <= 10; ++i) {
            test0to9SequenceSizeAndRange(i);
        }
        try {
            test0to9SequenceSizeAndRange(11);
            Assert.fail("It should not be possible to generate sequence that large");
        } catch (IllegalArgumentException ex) {
            ex.getMessage();
        }
    }

    private void test0to9SequenceSizeAndRange(int expectedSequence0to9size) {
        final int minValueIncl = 0;
        final int maxValueExcl = 10;
        List<Integer> integersFrom0To9 = Generator.getUniqueIntegerSequence(minValueIncl, maxValueExcl, expectedSequence0to9size);
        Set<Integer> uniqueContainer = new HashSet<>(integersFrom0To9);
        Assert.assertEquals(expectedSequence0to9size, uniqueContainer.size());
        OptionalInt minVal = integersFrom0To9.stream().mapToInt(s -> s).min();
        OptionalInt maxVal = integersFrom0To9.stream().mapToInt(s -> s).max();
        if (expectedSequence0to9size > 0) {
            assert minVal.isPresent();
            assert maxVal.isPresent();
            Assert.assertTrue(minVal.getAsInt() >= minValueIncl);
            Assert.assertTrue(maxVal.getAsInt() < maxValueExcl);
        } else {
            Assert.assertFalse(minVal.isPresent());
            Assert.assertFalse(maxVal.isPresent());
        }
    }
}