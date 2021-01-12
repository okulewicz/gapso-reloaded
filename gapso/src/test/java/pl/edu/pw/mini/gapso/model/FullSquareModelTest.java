package pl.edu.pw.mini.gapso.model;

import org.junit.Assert;
import org.junit.Test;
import pl.edu.pw.mini.gapso.bounds.Bounds;
import pl.edu.pw.mini.gapso.bounds.SimpleBounds;
import pl.edu.pw.mini.gapso.function.ConvexSquareFunction;
import pl.edu.pw.mini.gapso.function.Function;
import pl.edu.pw.mini.gapso.function.FunctionWhiteBox;
import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.sample.SingleSample;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FullSquareModelTest {

    @Test
    public void getConvexOptimumLocation() {
        FunctionWhiteBox convexSquareFunction = new ConvexSquareFunction();
        final double[] expectedOptimumLocation = convexSquareFunction.getOptimumLocation();
        double optValue = convexSquareFunction.getValue(expectedOptimumLocation);
        final double delta = 0.01;
        for (double deltax = -delta; deltax <= delta; deltax += 2 * delta)
            for (double deltay = -delta; deltay <= delta; deltay += 2 * delta) {
                double suboptValue = convexSquareFunction.getValue(new double[]{
                        expectedOptimumLocation[0] + deltax,
                        expectedOptimumLocation[1] + deltay
                });
                Assert.assertTrue(suboptValue > optValue);
            }
        List<Sample> sampleList = getSamples(convexSquareFunction);
        Bounds bounds = SimpleBounds.createBoundsFromSamples(sampleList);
        Model model = new FullSquareModel();
        double[] optimumLocation = model.getOptimumLocation(sampleList, bounds);
        Assert.assertArrayEquals(expectedOptimumLocation, optimumLocation, 1e-2);
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

    @Test
    public void ModelOptimumOutsideFunctionBounds() {
        Sample[] samples = new Sample[]{
                new SingleSample(
                        new double[]{0.8246643391410124, -0.7269922749851094},
                        109.19935697815373
                ),
                new SingleSample(
                        new double[]{4.144236131707409, 0.24866348310267128},
                        114.5258712437668
                ),
                new SingleSample(
                        new double[]{2.7460606544754924, -4.788280862812627},
                        268.8474200039809
                ),
                new SingleSample(
                        new double[]{-5.0, 5.0},
                        2052.797737485445
                ),
                new SingleSample(
                        new double[]{-1.3281330993538196, 2.900498624453631},
                        501.6320477069903
                ),
                new SingleSample(
                        new double[]{2.3670425847561303, 1.4217172229576647},
                        178.12010529952312
                )
        };
        List<Sample> sampleList = Arrays.stream(samples).collect(Collectors.toList());
        Bounds largebounds = new SimpleBounds(
                new double[]{-6.0, -6.0},
                new double[]{6.0, 6.0}
        );
        Bounds bounds = new SimpleBounds(
                new double[]{-5.0, -5.0},
                new double[]{5.0, 5.0}
        );
        Model model = new FullSquareModel();
        double[] optimumLocation = model.getOptimumLocation(sampleList, largebounds);
        Assert.assertArrayEquals(new double[]{-3.249336123377295, 5.876107354430642}, optimumLocation, 0.0);

        optimumLocation = model.getOptimumLocation(sampleList, bounds);
        Assert.assertArrayEquals(new double[]{-3.249336123377295, 5.0}, optimumLocation, 0.0);
    }

}