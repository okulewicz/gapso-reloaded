package pl.edu.pw.mini.gapso.sample.tree;

import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.sample.SingleSample;

import java.util.*;
import java.util.stream.Collectors;

public class LeafPage extends Page {
    List<Sample> samples = new ArrayList<>();

    public LeafPage(int pageSize) {
        super(pageSize);
    }

    @Override
    public void clearIndex() {
        samples.clear();
    }

    @Override
    public boolean indexSample(Sample sample) {
        //prevents from indexing identically located samples
        double[] sampleX = sample.getX();
        if (samples.stream().anyMatch(s -> Arrays.equals(s.getX(), sampleX)))
            return true;
        if (count >= pageSize) {
            return false;
        }
        samples.add(sample);

        updateStatistics(sample, 1);
        return true;
    }

    @Override
    public List<Page> splitPage() {
        if (samples.size() < 2)
            return null;
        List<Page> splittedPages = new ArrayList<>();
        Page page1 = new LeafPage(this.pageSize);
        Page page2 = new LeafPage(this.pageSize);
        splittedPages.add(page1);
        splittedPages.add(page2);
        List<Sample> samplesToAdd = fillInInitialSamples(page1, page2);
        divideSamplesAmongPages(page1, page2, samplesToAdd);
        return splittedPages;
    }

    @Override
    public List<Sample> getKEvenlyDistributedSamples(int maxK) {
        List<Sample> returnedSamples = new ArrayList<>();
        int n = samples.size();
        Set<Integer> idxSamples = getKUniqueRandomIntegers(maxK, n);
        for (int idx : idxSamples) {
            returnedSamples.add(samples.get(idx));
        }
        return returnedSamples;
    }

    @Override
    public List<DistancedSample> getKNearestSamples(double[] x, int maxK) {
        List<DistancedSample> orderedChildren =
                samples.stream()
                        .map(sample -> new DistancedSample(sample, x))
                        .sorted(Comparator.comparingDouble(DistancedSample::getDistance))
                        .collect(Collectors.toList());
        if (orderedChildren.size() > maxK)
            return orderedChildren.subList(0, maxK);
        return orderedChildren;
    }

    @Override
    public List<DistancedSample> getKNearestInDimensionSamples(double[] x, int dim, int maxK) {
        List<DistancedSample> orderedChildren =
                samples.stream()
                        .map(sample -> new DistancedSample(sample, x, dim))
                        .sorted(Comparator.comparingDouble(DistancedSample::getDistance))
                        .collect(Collectors.toList());
        if (orderedChildren.size() > maxK)
            return orderedChildren.subList(0, maxK);
        return orderedChildren;
    }

    @Override
    public Sample getCached(double[] x) {
        if (maxValue - minValue < TOLERANCE) {
            return new SingleSample(x, (maxValue + minValue) / 2);
        }
        for (Sample sample : samples) {
            if (Arrays.equals(sample.getX(), x))
                return sample;
        }
        return null;
    }

    @Override
    public boolean someLeafOverlaps(double[] x) {
        return super.contains(x);
    }

    private void divideSamplesAmongPages(Page page1, Page page2, List<Sample> samplesToAdd) {
        while (!samplesToAdd.isEmpty()) {
            Sample sampleToAdd = samplesToAdd.get(0);
            if (page1.count >= pageSize / 2) {
                page2.indexSample(sampleToAdd);
            } else if (page2.count >= pageSize / 2) {
                page1.indexSample(sampleToAdd);
            } else {
                MeasurementsDTO measurementsPage1 = page1.getPageMeasurements();
                MeasurementsDTO measurementsPage2 = page2.getPageMeasurements();
                MeasurementsDTO measurementsPage1Updated = page1.getPossiblePageMeasures(sampleToAdd.getX());
                MeasurementsDTO measurementsPage2Updated = page2.getPossiblePageMeasures(sampleToAdd.getX());
                if ((measurementsPage1Updated.getVolume() - measurementsPage1.getVolume()) < (measurementsPage2Updated.getVolume() - measurementsPage2.getVolume())) {
                    page1.indexSample(sampleToAdd);
                } else if ((measurementsPage2Updated.getVolume() - measurementsPage2.getVolume()) < (measurementsPage1Updated.getVolume() - measurementsPage1.getVolume())) {
                    page2.indexSample(sampleToAdd);
                } else if ((measurementsPage1Updated.getDiameter() - measurementsPage1.getDiameter()) < (measurementsPage2Updated.getDiameter() - measurementsPage2.getDiameter())) {
                    page1.indexSample(sampleToAdd);
                } else {
                    page2.indexSample(sampleToAdd);
                }
            }
            samplesToAdd.remove(0);
        }
    }

    private List<Sample> fillInInitialSamples(Page page1, Page page2) {
        double maxDistance = Double.NEGATIVE_INFINITY;
        Sample seed1 = null, seed2 = null;
        for (Sample sample1 : samples) {
            for (Sample sample2 : samples) {
                double tempDistance = 0.0;
                for (int i = 0; i < sample1.getX().length; ++i) {
                    tempDistance += (sample1.getX()[i] - sample2.getX()[i]) * (sample1.getX()[i] - sample2.getX()[i]);
                }
                if (tempDistance > maxDistance) {
                    maxDistance = tempDistance;
                    seed1 = sample1;
                    seed2 = sample2;
                }
            }
        }
        page1.indexSample(seed1);
        page2.indexSample(seed2);
        List<Sample> samplesToAdd = new ArrayList<>(samples);
        samplesToAdd.remove(seed1);
        samplesToAdd.remove(seed2);
        return samplesToAdd;
    }
}
