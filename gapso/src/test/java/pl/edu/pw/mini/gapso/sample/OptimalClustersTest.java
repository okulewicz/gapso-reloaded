package pl.edu.pw.mini.gapso.sample;

import org.junit.Assert;
import org.junit.Test;
import pl.edu.pw.mini.gapso.function.Function;
import pl.edu.pw.mini.gapso.function.RastriginFunction;
import pl.edu.pw.mini.gapso.sample.cluster.OptimalClusters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OptimalClustersTest {

    protected List<Sample> prepareRealWorldSamples() {
        double[][] vectors = new double[][]
                {
                        new double[]{-2.30084313, 2.70881784},
                        new double[]{-2.27343419, 2.93562809},
                        new double[]{-2.40330721, 2.94947199},
                        new double[]{-1.98531486, 2.35901598},
                        new double[]{-2.74117079, 2.28027840},
                        new double[]{-2.52283674, 2.33001549},
                        new double[]{-2.42052192, 2.29508464},
                        new double[]{-2.44421259, 2.27361385},
                        new double[]{-2.40817622, 2.32146468},
                        new double[]{-2.31197445, 2.28253265},
                        new double[]{-2.35750177, 2.30095716},
                        new double[]{-2.24174585, 2.33512048},
                        new double[]{-1.16585358, 2.70881784},
                        new double[]{-1.45508010, 2.45674420},
                        new double[]{-2.36981864, 1.74817235},
                        new double[]{-2.31923290, 1.88347933},
                        new double[]{-2.17129671, 1.55606380},
                };
        return Arrays
                .stream(vectors)
                .map(v -> new SingleSample(v, 0.0)).collect(Collectors.toList());
    }

    private List<Sample> prepareSamplesFromFunction(Function function) {
        List<Sample> samples = new ArrayList<>();
        for (double x = -0.2; x <= 0.2; x += 0.1) {
            double[] xVector = new double[]{x};
            double y = function.getValue(xVector);
            Sample s = new SingleSample(xVector, y);
            samples.add(s);
        }
        return samples;
    }

    @Test
    public void getLargestClusterFromRealSamplesList() {
        List<Sample> samples = prepareRealWorldSamples();
        OptimalClusters optimalClusters = new OptimalClusters(samples, 2);
        Assert.assertTrue(optimalClusters.getLargestCluster().size() > 6);
        Assert.assertTrue(optimalClusters.getLargestCluster().size() < 10);
    }

    @Test
    public void getLargestCluster() {
        int dimension = 1;
        Function function = new RastriginFunction(dimension);
        List<Sample> samples = prepareSamplesFromFunction(function);
        List<Sample> outliers = new ArrayList<>();
        {
            OptimalClusters optimalClusters = new OptimalClusters(samples, dimension);
            Assert.assertEquals(samples.size(), optimalClusters.getLargestCluster().size());
        }
        {
            Sample outlingSample = addOutlyingSample(function, samples, 1.0);
            outliers.add(outlingSample);
            OptimalClusters optimalClustersWithOutlier = new OptimalClusters(samples, dimension);
            List<Sample> largestCluster = optimalClustersWithOutlier.getLargestCluster();
            Assert.assertTrue(samples.containsAll(largestCluster));
            Assert.assertFalse(largestCluster.contains(outlingSample));
            Assert.assertEquals(samples.size() - 1, largestCluster.size());
        }
        {
            Sample outlyingSample = addOutlyingSample(function, samples, 0.9);
            outliers.add(outlyingSample);
            OptimalClusters optimalClustersWithOutliers = new OptimalClusters(samples, dimension);
            List<Sample> largestCluster = optimalClustersWithOutliers.getLargestCluster();
            Assert.assertTrue(samples.containsAll(largestCluster));
            Assert.assertFalse(largestCluster.contains(outlyingSample));
            Assert.assertEquals(samples.size() - 2, largestCluster.size());
        }
    }

    private Sample addOutlyingSample(Function function, List<Sample> samples, double location) {
        double[] xVector = new double[]{location};
        double y = function.getValue(xVector);
        Sample s = new SingleSample(xVector, y);
        samples.add(0, s);
        return s;
    }

    @Test
    public void getClusters() {
        int dimension = 1;
        Function function = new RastriginFunction(dimension);
        List<Sample> samples = prepareSamplesFromFunction(function);
        {
            OptimalClusters optimalClusters = new OptimalClusters(samples, dimension);
            Assert.assertEquals(1, optimalClusters.getClusters().size());
        }
        {
            addOutlyingSample(function, samples, 1.0);
            OptimalClusters optimalClustersWithOutlier = new OptimalClusters(samples, dimension);
            Assert.assertEquals(2, optimalClustersWithOutlier.getClusters().size());
        }
        {
            addOutlyingSample(function, samples, 0.9);
            OptimalClusters optimalClustersWithOutlier = new OptimalClusters(samples, dimension);
            final List<List<Sample>> clusters = optimalClustersWithOutlier.getClusters();
            Assert.assertEquals(2, clusters.size());
        }


        List<Sample> realsamples = prepareRealWorldSamples();
        OptimalClusters optimalClusters = new OptimalClusters(realsamples, 2);
        final List<List<Sample>> clusters = optimalClusters.getClusters();
        Assert.assertEquals(3, clusters.stream().filter(c -> c.size() > 1).count());

    }

    @Test
    public void getBestCluster() {
        int dimension = 1;
        Function function = new RastriginFunction(new double[]{0.95});
        List<Sample> samples = prepareSamplesFromFunction(function);
        int expectedLargestCluster = samples.size();
        {
            addOutlyingSample(function, samples, 1.0);
        }
        {
            addOutlyingSample(function, samples, 0.9);
            OptimalClusters optimalClustersWithOutlier = new OptimalClusters(samples, dimension);
            final List<Sample> bestClusterFor2 = optimalClustersWithOutlier.getBestCluster(2);
            Assert.assertEquals(2, bestClusterFor2.size());
            final List<Sample> bestClusterFor3 = optimalClustersWithOutlier.getBestCluster(3);
            Assert.assertEquals(expectedLargestCluster, bestClusterFor3.size());
        }
    }
}
