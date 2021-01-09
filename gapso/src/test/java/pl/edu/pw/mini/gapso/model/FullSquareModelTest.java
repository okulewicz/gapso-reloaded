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

public class FullSquareModelTest {

    @Test
    public void getConvexOptimumLocation() {
        Function convexSquareFunction = new Function() {
            @Override
            public double getValue(double[] x) {
                return
                        +1 * x[0] * x[0]
                                + 2 * x[0] * x[1]
                                + 5 * x[1] * x[1]
                                - 2 * x[0]
                                - 4 * x[1]
                                + 1;
            }
        };
        double optValue = convexSquareFunction.getValue(new double[]{
                0.75,
                0.25
        });
        for (double deltax = -0.01; deltax <= 0.01; deltax += 0.02)
            for (double deltay = -0.01; deltay <= 0.01; deltay += 0.02) {
                double suboptValue = convexSquareFunction.getValue(new double[]{
                        0.75 + deltax,
                        0.25 + deltay
                });
                Assert.assertTrue(suboptValue > optValue);
            }
        List<Sample> sampleList = getSamples(convexSquareFunction);
        Bounds bounds = SimpleBounds.createBoundsFromSamples(sampleList);
        Model model = new FullSquareModel();
        double[] optimumLocation = model.getOptimumLocation(sampleList, bounds);
        Assert.assertArrayEquals(new double[]{
                0.25,
                0.75
        }, optimumLocation, 1e-2);
    }

    private List<Sample> getSamples(Function function) {
        double[] x1 = new double[]{0.0, 0.0};
        double[] x2 = new double[]{0.0, 1.0};
        double[] x3 = new double[]{1.0, 0.0};
        double[] x4 = new double[]{2.0, 0.0};
        double[] x5 = new double[]{0.0, 2.0};
        double[] x6 = new double[]{1.0, 1.0};
        double y1 = function.getValue(x1);
        double y2 = function.getValue(x2);
        double y3 = function.getValue(x3);
        double y4 = function.getValue(x4);
        double y5 = function.getValue(x5);
        double y6 = function.getValue(x6);
        Sample s1 = new SingleSample(x1, y1);
        Sample s2 = new SingleSample(x2, y2);
        Sample s3 = new SingleSample(x3, y3);
        Sample s4 = new SingleSample(x4, y4);
        Sample s5 = new SingleSample(x5, y5);
        Sample s6 = new SingleSample(x6, y6);
        List<Sample> sampleList = new ArrayList<>();
        sampleList.add(s1);
        sampleList.add(s2);
        sampleList.add(s3);
        sampleList.add(s4);
        sampleList.add(s5);
        sampleList.add(s6);
        return sampleList;
    }

}