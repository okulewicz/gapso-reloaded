package pl.edu.pw.mini.gapso.model;

import org.junit.Assert;
import org.junit.Test;
import pl.edu.pw.mini.gapso.bounds.Bounds;
import pl.edu.pw.mini.gapso.bounds.SimpleBounds;
import pl.edu.pw.mini.gapso.function.Function;
import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.sample.SingleSample;

import java.util.ArrayList;
import java.util.List;

public class LinearModelTest {

    @Test
    public void getOptimumLocationForSlopedFunction() {
        Function slopedLinearFunction = new Function() {
            @Override
            public double getValue(double[] x) {
                return 2 * x[0] - 3 * x[1] + 1;
            }
        };
        List<Sample> sampleList = getSamples(slopedLinearFunction);
        Bounds bounds = SimpleBounds.createBoundsFromSamples(sampleList);
        Model model = new LinearModel();
        double[] optimumLocation = model.getOptimumLocation(sampleList, bounds);
        Assert.assertArrayEquals(new double[]{0.0, 1.0}, optimumLocation, 0.0);
    }

    @Test
    public void getOptimumLocationForPartiallyFlatFunction() {
        Function partiallyFlatLinearFunction = new Function() {
            @Override
            public double getValue(double[] x) {
                return -2 * x[0] + 1;
            }
        };
        List<Sample> sampleList = getSamples(partiallyFlatLinearFunction);
        Bounds bounds = SimpleBounds.createBoundsFromSamples(sampleList);
        Model model = new LinearModel();
        double[] optimumLocation = model.getOptimumLocation(sampleList, bounds);
        Assert.assertArrayEquals(new double[]{1.0, 0.5}, optimumLocation, 0.0);
    }

    private List<Sample> getSamples(Function function) {
        double[] x1 = new double[]{0.0, 0.0};
        double[] x2 = new double[]{0.0, 1.0};
        double[] x3 = new double[]{1.0, 0.0};
        double y1 = function.getValue(x1);
        double y2 = function.getValue(x2);
        double y3 = function.getValue(x3);
        Sample s1 = new SingleSample(x1, y1);
        Sample s2 = new SingleSample(x2, y2);
        Sample s3 = new SingleSample(x3, y3);
        List<Sample> sampleList = new ArrayList<>();
        sampleList.add(s1);
        sampleList.add(s2);
        sampleList.add(s3);
        return sampleList;
    }
}