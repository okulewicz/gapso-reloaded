package pl.edu.pw.mini.gapso.sample;

import org.junit.Assert;
import org.junit.Test;
import pl.edu.pw.mini.gapso.function.Function;
import pl.edu.pw.mini.gapso.function.RastriginFunction;

import java.util.ArrayList;
import java.util.List;

public class OutliersDetectionTest {

    public static final int TRIES = 10;

    /*
From this sample 7 - 9 samples should be selected
bbob_f003_i01_d02,  1,  4, 95,-2.30084313, 2.70881784,DataPoint
bbob_f003_i01_d02,  1,  4, 95,-2.27343419, 2.93562809,DataPoint
bbob_f003_i01_d02,  1,  4, 95,-2.40330721, 2.94947199,DataPoint
bbob_f003_i01_d02,  1,  4, 95,-1.98531486, 2.35901598,DataPoint
bbob_f003_i01_d02,  1,  4, 95,-2.74117079, 2.28027840,DataPoint
bbob_f003_i01_d02,  1,  4, 95,-2.52283674, 2.33001549,DataPoint
bbob_f003_i01_d02,  1,  4, 95,-2.42052192, 2.29508464,DataPoint
bbob_f003_i01_d02,  1,  4, 95,-2.44421259, 2.27361385,DataPoint
bbob_f003_i01_d02,  1,  4, 95,-2.40817622, 2.32146468,DataPoint
bbob_f003_i01_d02,  1,  4, 95,-2.31197445, 2.28253265,DataPoint
bbob_f003_i01_d02,  1,  4, 95,-2.35750177, 2.30095716,DataPoint
bbob_f003_i01_d02,  1,  4, 95,-2.24174585, 2.33512048,DataPoint
bbob_f003_i01_d02,  1,  4, 95,-1.16585358, 2.70881784,DataPoint
bbob_f003_i01_d02,  1,  4, 95,-1.45508010, 2.45674420,DataPoint
bbob_f003_i01_d02,  1,  4, 95,-2.36981864, 1.74817235,DataPoint
bbob_f003_i01_d02,  1,  4, 95,-2.31923290, 1.88347933,DataPoint
bbob_f003_i01_d02,  1,  4, 95,-2.17129671, 1.55606380,DataPoint
     */

    @Test
    public void getLargestCluster() {
        int dimension = 1;
        Function function = new RastriginFunction(dimension);
        List<Sample> samples = prepareSamples(function);
        List<Sample> outliers = new ArrayList<>();
        {
            OptimalClusters optimalClusters = new OptimalClusters(samples, dimension);
            Assert.assertEquals(samples.size(), optimalClusters.getLargestCluster().size());
        }
        {
            Sample outlingSample = addOutlingSample(function, samples, 1.0);
            outliers.add(outlingSample);
            OptimalClusters optimalClustersWithOutlier = new OptimalClusters(samples, dimension);
            List<Sample> largestCluster = optimalClustersWithOutlier.getLargestCluster();
            Assert.assertTrue(samples.containsAll(largestCluster));
            Assert.assertFalse(largestCluster.contains(outlingSample));
            Assert.assertEquals(samples.size() - 1, largestCluster.size());
        }
        {
            Sample outlyingSample = addOutlingSample(function, samples, 0.9);
            outliers.add(outlyingSample);
            OptimalClusters optimalClustersWithOutliers = new OptimalClusters(samples, dimension);
            List<Sample> largestCluster = optimalClustersWithOutliers.getLargestCluster();
            Assert.assertTrue(samples.containsAll(largestCluster));
            Assert.assertFalse(largestCluster.contains(outlyingSample));
            Assert.assertEquals(samples.size() - 2, largestCluster.size());
        }
    }

    private Sample addOutlingSample(Function function, List<Sample> samples, double location) {
        double[] xVector = new double[]{location};
        double y = function.getValue(xVector);
        Sample s = new SingleSample(xVector, y);
        samples.add(0, s);
        return s;
    }

    private List<Sample> prepareSamples(Function function) {
        List<Sample> samples = new ArrayList<>();
        for (double x = -0.2; x <= 0.2; x += 0.1) {
            double[] xVector = new double[]{x};
            double y = function.getValue(xVector);
            Sample s = new SingleSample(xVector, y);
            samples.add(s);
        }
        return samples;
    }

}
