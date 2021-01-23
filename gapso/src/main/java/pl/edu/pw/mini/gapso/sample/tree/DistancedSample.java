package pl.edu.pw.mini.gapso.sample.tree;

import pl.edu.pw.mini.gapso.sample.Sample;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DistancedSample extends Sample {
    private final double distance;
    protected Sample sample;

    public DistancedSample(Sample sample, double[] refPoint) {
        this.sample = sample;
        this.distance = sample.getDistance(refPoint);
    }

    public DistancedSample(Sample sample, double[] refPoint, int dim) {
        this.sample = sample;
        this.distance = sample.getDistanceInDimension(refPoint, dim);
    }

    /**
     * Returns a merged sorted by distance list out of sorted l1 and sorted l2 with a max length of k
     *
     * @param l1 first ordered list
     * @param l2 second ordered list
     * @param k  maximum list length
     * @return sorted list
     */
    public static List<DistancedSample> mergeOrderedLists(List<DistancedSample> l1, List<DistancedSample> l2, int k) {
        List<DistancedSample> mergedList = new ArrayList<>();
        Iterator<DistancedSample> i1 = l1.iterator();
        Iterator<DistancedSample> i2 = l2.iterator();
        //TODO supposedly this can be done better with spliterators
        if (i1.hasNext() && i2.hasNext()) {
            DistancedSample l1HeadSample = i1.next();
            DistancedSample l2HeadSample = i2.next();
            while (true) {
                if (l1HeadSample.getDistance() < l2HeadSample.getDistance()) {
                    if (checkIfListOfDesiredLength(k, mergedList, l1HeadSample))
                        return mergedList;
                    if (i1.hasNext()) {
                        l1HeadSample = i1.next();
                    } else {
                        if (checkIfListOfDesiredLength(k, mergedList, l2HeadSample)) return mergedList;
                        break;
                    }
                } else {
                    if (checkIfListOfDesiredLength(k, mergedList, l2HeadSample)) return mergedList;
                    if (i2.hasNext()) {
                        l2HeadSample = i2.next();
                    } else {
                        if (checkIfListOfDesiredLength(k, mergedList, l1HeadSample)) return mergedList;
                        break;
                    }
                }
            }
        }
        while (i1.hasNext()) {
            if (checkIfListOfDesiredLength(k, mergedList, i1.next())) return mergedList;
        }
        while (i2.hasNext()) {
            if (checkIfListOfDesiredLength(k, mergedList, i2.next())) return mergedList;
        }
        return mergedList;
    }

    private static boolean checkIfListOfDesiredLength(int k, List<DistancedSample> mergedList, DistancedSample l1HeadSample) {
        mergedList.add(l1HeadSample);
        return mergedList.size() > k - 1;
    }

    @Override
    public double[] getX() {
        return sample.getX();
    }

    @Override
    public double getY() {
        return sample.getY();
    }

    public double getDistance() {
        return distance;
    }

    public Sample getSample() {
        return this.sample;
    }
}
