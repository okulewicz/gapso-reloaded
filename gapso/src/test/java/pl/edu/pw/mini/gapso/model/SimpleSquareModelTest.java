package pl.edu.pw.mini.gapso.model;

import org.junit.Assert;
import org.junit.Test;
import pl.edu.pw.mini.gapso.bounds.Bounds;
import pl.edu.pw.mini.gapso.bounds.SimpleBounds;
import pl.edu.pw.mini.gapso.function.*;
import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.sample.SingleSample;
import pl.edu.pw.mini.gapso.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class SimpleSquareModelTest {

    @Test
    public void getConvexOptimumLocation() {
        FunctionWhiteBox convexSquareFunction = new ConvexSeparableSquareFunction();
        double[] expectedOptimumLocation = convexSquareFunction.getOptimumLocation();
        List<Sample> sampleList = getSamples(convexSquareFunction);
        Bounds bounds = SimpleBounds.createBoundsFromSamples(sampleList);
        Model model = new SimpleSquareModel();
        double[] optimumLocation = model.getOptimumLocation(sampleList, bounds);
        Assert.assertArrayEquals(new double[]{
                Utils.getBoundedValue(bounds, 0, expectedOptimumLocation),
                Utils.getBoundedValue(bounds, 1, expectedOptimumLocation)
        }, optimumLocation, 1e-2);
    }

    @Test
    public void getConcaveOptimumLocation() {
        Function concaveSquareFunction = new ConcaveSeparableSquareFunction();
        List<Sample> sampleList = getSamples(concaveSquareFunction);
        Bounds bounds = SimpleBounds.createBoundsFromSamples(sampleList);
        Model model = new SimpleSquareModel();
        double[] optimumLocation = model.getOptimumLocation(sampleList, bounds);
        Assert.assertArrayEquals(new double[]{
                bounds.getUpper()[0],
                bounds.getUpper()[1]
        }, optimumLocation, 1e-2);
    }

    @Test
    public void getLinearOptimumLocation() {
        Function linear = new SlopedLinearFunction();
        List<Sample> sampleList = getSamples(linear);
        Bounds bounds = SimpleBounds.createBoundsFromSamples(sampleList);
        Model model = new SimpleSquareModel();
        double[] optimumLocation = model.getOptimumLocation(sampleList, bounds);
        Assert.assertArrayEquals(new double[]{
                bounds.getLower()[0],
                bounds.getUpper()[1]
        }, optimumLocation, 1e-2);
    }

    @Test
    public void getMinSamplesCount() {
        Model model = new SimpleSquareModel();
        Assert.assertEquals(5, model.getMinSamplesCount(2));
    }

    private List<Sample> getSamples(Function function) {
        double[] x1 = new double[]{0.0, 0.0};
        double[] x2 = new double[]{0.0, 1.0};
        double[] x3 = new double[]{1.0, 0.0};
        double[] x4 = new double[]{2.0, 0.0};
        double[] x5 = new double[]{0.0, 2.0};
        double y1 = function.getValue(x1);
        double y2 = function.getValue(x2);
        double y3 = function.getValue(x3);
        double y4 = function.getValue(x4);
        double y5 = function.getValue(x5);
        Sample s1 = new SingleSample(x1, y1);
        Sample s2 = new SingleSample(x2, y2);
        Sample s3 = new SingleSample(x3, y3);
        Sample s4 = new SingleSample(x4, y4);
        Sample s5 = new SingleSample(x5, y5);
        List<Sample> sampleList = new ArrayList<>();
        sampleList.add(s1);
        sampleList.add(s2);
        sampleList.add(s3);
        sampleList.add(s4);
        sampleList.add(s5);
        return sampleList;
    }

}