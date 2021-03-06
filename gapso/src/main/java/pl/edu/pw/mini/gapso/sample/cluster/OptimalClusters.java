package pl.edu.pw.mini.gapso.sample.cluster;

import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.sample.SingleSample;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OptimalClusters {
    public static final double AVG_DISTANCE_MULTIPLIER = 1.5;
    public static final int MIN_SAMPLES_DIM_MULTIPLIER = 3;
    private static final double DIFFERENCE_THRESHOLD = 1e-4;
    private final List<Sample> samples;
    private final List<ScaledAssignedSample> scaledSamples;
    private final int dimension;
    private final List<Edge> edges;
    private final List<List<Sample>> clusteredSamples;
    private double minValue;
    private double maxValue;

    public OptimalClusters(List<Sample> samples, int dimension) {
        this.samples = samples;
        this.dimension = dimension;
        scaledSamples = scaleSamples(samples);
        edges = createSortedEdges(scaledSamples);
        clusteredSamples = clusterSamples();
    }

    private List<ScaledAssignedSample> scaleSamples(List<Sample> samples) {
        List<ScaledAssignedSample> scaledSamples = new ArrayList<>();
        double[] minValues = new double[dimension];
        double[] maxValues = new double[dimension];
        minValue = Double.POSITIVE_INFINITY;
        maxValue = Double.NEGATIVE_INFINITY;
        Arrays.fill(minValues, Double.POSITIVE_INFINITY);
        Arrays.fill(maxValues, Double.NEGATIVE_INFINITY);
        for (Sample sample : samples) {
            for (int dimIdx = 0; dimIdx < dimension; dimIdx++) {
                double[] x = sample.getX();
                minValues[dimIdx] = Math.min(minValues[dimIdx], x[dimIdx]);
                maxValues[dimIdx] = Math.max(maxValues[dimIdx], x[dimIdx]);
            }
            minValue = Math.min(minValue, sample.getY());
            maxValue = Math.max(maxValue, sample.getY());
        }
        for (Sample sample : samples) {
            double[] x = Arrays.copyOf(sample.getX(), dimension);
            for (int dimIdx = 0; dimIdx < dimension; dimIdx++) {
                if (maxValues[dimIdx] - minValues[dimIdx] > DIFFERENCE_THRESHOLD) {
                    x[dimIdx] = (x[dimIdx] - minValues[dimIdx]) / (maxValues[dimIdx] - minValues[dimIdx]);
                }
            }
            Sample scaledSample = new SingleSample(x, sample.getY());
            ScaledAssignedSample scaledAssignedSample = new ScaledAssignedSample(sample, scaledSample);
            scaledSamples.add(scaledAssignedSample);
        }
        return scaledSamples;
    }

    private List<Edge> createSortedEdges(List<ScaledAssignedSample> samples) {
        List<Edge> edges = new ArrayList<>();
        for (int s1Idx = 0; s1Idx < samples.size(); ++s1Idx) {
            for (int s2Idx = s1Idx + 1; s2Idx < samples.size(); ++s2Idx) {
                ScaledAssignedSample sample1 = samples.get(s1Idx);
                ScaledAssignedSample sample2 = samples.get(s2Idx);
                if (sample1 != sample2) {
                    Edge e = new Edge(sample1, sample2);
                    edges.add(e);
                }
            }
        }
        Stream<Edge> sorted = edges
                .stream()
                .sorted(Comparator.comparingDouble(Edge::getDistance));
        return sorted.collect(Collectors.toList());
    }

    private List<List<Sample>> clusterSamples() {
        double thresholdDistance = AVG_DISTANCE_MULTIPLIER * Math.sqrt(dimension) / samples.size();
        int largestClusterSize = 1;
        for (Edge edge : edges) {
            if (edge.getDistance() > thresholdDistance && largestClusterSize > MIN_SAMPLES_DIM_MULTIPLIER * dimension) {
                break;
            }
            if (edge.getNode1().isSameGroup(edge.getNode2()))
                continue;
            int newClusterSize = edge.getNode1().mergeGroups(edge.getNode2());
            largestClusterSize = Math.max(largestClusterSize, newClusterSize);
        }
        return scaledSamples
                .stream()
                .map(ScaledAssignedSample::getGroup)
                .collect(Collectors.toSet())
                .stream()
                .map(l -> l
                        .stream()
                        .map(ScaledAssignedSample::getOriginalSample)
                        .collect(Collectors.toList()))
                .sorted(Comparator.comparingInt(List::size))
                .collect(Collectors.toList());
    }

    public List<Sample> getLargestCluster() {
        return clusteredSamples.get(clusteredSamples.size() - 1);
    }

    public List<Sample> getBestCluster(int minSamplesInCluster) {
        final Stream<List<Sample>> sorted = clusteredSamples
                .stream()
                .filter(c -> c.size() >= minSamplesInCluster)
                .sorted(Comparator.comparingDouble(
                        c -> c.stream().mapToDouble(Sample::getY).min().orElse(Double.POSITIVE_INFINITY)
                ));
        return sorted.findFirst().orElse(new ArrayList<>());
    }

    public List<List<Sample>> getClusters() {
        return clusteredSamples;
    }
}
