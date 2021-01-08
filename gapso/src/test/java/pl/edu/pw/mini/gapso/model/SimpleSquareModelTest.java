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

public class SimpleSquareModelTest {

    @Test
    public void getOptimumLocation() {
        Function squareFunction = new Function() {
            @Override
            public double getValue(double[] x) {
                return 2 * x[0] * x[0] + 3 * x[0] + 4 * x[1] * x[1] - 3 * x[1] + 1;
            }
        };
        List<Sample> sampleList = getSamples(squareFunction);
        Bounds bounds = SimpleBounds.createBoundsFromSamples(sampleList);
        Model model = new SimpleSquareModel();
        double[] optimumLocation = model.getOptimumLocation(sampleList, bounds);
        Assert.assertArrayEquals(new double[]{
                Math.min(Math.max(-0.75, bounds.getLower()[0]), bounds.getUpper()[0]),
                Math.min(Math.max(0.375, bounds.getLower()[1]), bounds.getUpper()[1])
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