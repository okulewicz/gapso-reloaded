package pl.edu.pw.mini.gapso.utils;

import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import pl.edu.pw.mini.gapso.configuration.Configuration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Generator {
    public static final RandomGenerator RANDOM =
            new JDKRandomGenerator(Configuration.getInstance().getSeed());

    public static List<Integer> getUniqueIntegerSequence(int minValueIncl, int maxValueExcl, int size) {
        final int maxElements = maxValueExcl - minValueIncl;
        List<Integer> integers = new ArrayList<>();
        if (size > maxElements) {
            throw new IllegalArgumentException("Not enough elements (" +
                    maxElements + ") for sequence size (" + size + ")");
        }
        if (size > maxElements / 2) {
            for (int i = minValueIncl; i < maxValueExcl; ++i) {
                integers.add(i);
            }
            while (integers.size() > size) {
                integers.remove(RANDOM.nextInt(integers.size()));
            }
        } else {
            Set<Integer> uniqueIntegers = new HashSet<>();
            while (uniqueIntegers.size() < size) {
                int rand = minValueIncl + RANDOM.nextInt(maxValueExcl - minValueIncl);
                uniqueIntegers.add(rand);
            }
            integers.addAll(uniqueIntegers);
        }
        return integers;

    }
}
