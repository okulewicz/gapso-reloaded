package pl.edu.pw.mini.gapso.sample.tree;

import pl.edu.pw.mini.gapso.sample.DistancedSample;
import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.utils.Generator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Page {
    protected final double TOLERANCE = 1e-8;
    protected double volume;
    protected double diameter;
    protected int count;
    protected double minValue;
    protected double maxValue;
    protected double[] maxBounds;
    protected double[] minBounds;
    protected int pageSize;

    protected Page(int pageSize) {
        this.pageSize = pageSize;
        minValue = Double.POSITIVE_INFINITY;
        maxValue = Double.NEGATIVE_INFINITY;
        count = 0;
    }

    public double getVolume() {
        return volume;
    }

    public double getDiameter() {
        return diameter;
    }

    public int getCount() {
        return count;
    }

    public double getMinValue() {
        return minValue;
    }

    public double[] getMaxBounds() {
        return maxBounds;
    }

    public double[] getMinBounds() {
        return minBounds;
    }

    public int getPageSize() {
        return pageSize;
    }

    abstract public void clearIndex();

    abstract public boolean indexSample(Sample sample);

    abstract public List<Page> splitPage();

    abstract public List<Sample> getKEvenlyDistributedSamples(int maxK);

    abstract public List<DistancedSample> getKNearestSamples(double[] x, int maxK);

    abstract public List<DistancedSample> getKNearestInDimensionSamples(double[] x, int dim, int maxK);

    abstract public Sample getCached(double[] x);

    public MeasurementsDTO getPossiblePageMeasures(double[] x) {
        double[] tempMinBounds = Arrays.copyOf(minBounds, minBounds.length);
        double[] tempMaxBounds = Arrays.copyOf(maxBounds, maxBounds.length);
        return getMeasurementsDTOAndUpdateBounds(x, tempMinBounds, tempMaxBounds);
    }

    protected void updateStatistics(Sample sample, int countSamples) {
        double[] newX = sample.getX();
        if (maxBounds == null) {
            maxBounds = Arrays.copyOf(newX, newX.length);
        }
        if (minBounds == null) {
            minBounds = Arrays.copyOf(newX, newX.length);
        }
        minValue = Math.min(minValue, sample.getY());
        maxValue = Math.max(maxValue, sample.getY());
        count += countSamples;
        MeasurementsDTO measurementsDTO = getMeasurementsDTOAndUpdateBounds(newX, minBounds, maxBounds);
        volume = measurementsDTO.getVolume();
        diameter = measurementsDTO.getDiameter();
    }

    protected Set<Integer> getKUniqueRandomIntegers(int maxK, int n) {
        Set<Integer> idxSamples = new HashSet<>();
        while (idxSamples.size() < maxK && idxSamples.size() < n) {
            idxSamples.add(Generator.RANDOM.nextInt(n));
        }
        return idxSamples;
    }

    protected MeasurementsDTO getMeasurementsDTOAndUpdateBounds(double[] x, double[] tempMinBounds, double[] tempMaxBounds) {
        MeasurementsDTO measurements = new MeasurementsDTO();
        double tempSquaredDiameter = 0.0;
        double tempVolume = 1.0;
        for (int i = 0; i < x.length; ++i) {
            tempMinBounds[i] = Math.min(tempMinBounds[i], x[i]);
            tempMaxBounds[i] = Math.max(tempMaxBounds[i], x[i]);
            double width = tempMaxBounds[i] - tempMinBounds[i];
            tempVolume *= width;
            tempSquaredDiameter += width * width;
        }
        measurements.setDiameter(Math.sqrt(tempSquaredDiameter));
        measurements.setVolume(tempVolume);
        return measurements;
    }

    public MeasurementsDTO getPageMeasurements() {
        MeasurementsDTO measurements = new MeasurementsDTO();
        measurements.setDiameter(diameter);
        measurements.setVolume(volume);
        return measurements;
    }

    public boolean contains(double[] x) {
        if (getMaxBounds() == null || getMinBounds() == null)
            return false;
        for (int i = 0; i < getMaxBounds().length && i < getMinBounds().length; ++i) {
            if (x[i] < getMinBounds()[i] || x[i] > getMaxBounds()[i]) {
                return false;
            }
        }
        return true;
    }

    public abstract boolean someLeafOverlaps(double[] x);

    public double getMinDistanceLowerBoundary(double[] x) {
        if (getMinBounds() == null || getMaxBounds() == null)
            return Double.MAX_VALUE;
        double squaredDistance = 0.0;
        for (int i = 0; i < getMaxBounds().length && i < getMinBounds().length; ++i) {
            if (x[i] < getMinBounds()[i] || x[i] > getMaxBounds()[i]) {
                squaredDistance += Math.min(
                        (getMaxBounds()[i] - x[i]) * (getMaxBounds()[i] - x[i]),
                        (getMinBounds()[i] - x[i]) * (getMinBounds()[i] - x[i]));
            }
        }
        return Math.sqrt(squaredDistance);
    }

    public double getSafeDensityEstimation() {
        return getCount() / (getVolume() + 1);
    }

    public double getUCBRank(double minValue, double maxValue, double minDensity, double maxDensity, double parentDensity, double cFactor) {
        return (maxValue - getMinValue() + 1) / (maxValue - minValue + 1)
                + cFactor * (maxDensity - getSafeDensityEstimation() / parentDensity)
                / (maxDensity - minDensity);
                /*
        if (getVolume() <= 0)
            return 0;
        double density = getCount() / getVolume();
        return ((getMinValue() - maxValue + 1) / (minValue - maxValue + 1)) / (density)
                + cFactor * Math.sqrt(Math.log(2) / (density));
                */

    }

    static class MeasurementsDTO {
        private double volume;
        private double diameter;

        public double getVolume() {
            return volume;
        }

        public void setVolume(double volume) {
            this.volume = volume;
        }

        public double getDiameter() {
            return diameter;
        }

        public void setDiameter(double diameter) {
            this.diameter = diameter;
        }
    }
}
