package pl.edu.pw.mini.gapso.sample.tree;

import pl.edu.pw.mini.gapso.sample.Sample;

import java.util.List;

public class RTree extends Page {
    private Page rootPage;

    public RTree(int pageSize) {
        super(pageSize);
        rootPage = new NodePage(pageSize);
    }

    @Override
    public void clearIndex() {
        rootPage.clearIndex();
        rootPage = new NodePage(pageSize);
    }

    @Override
    public boolean indexSample(Sample sample) {
        while (!rootPage.indexSample(sample)) {
            List<Page> pages = splitPage();
            rootPage = new NodePage(pages, pageSize);
        }
        return true;
    }

    @Override
    public List<Page> splitPage() {
        return rootPage.splitPage();
    }

    @Override
    public List<Sample> getKEvenlyDistributedSamples(int maxK) {
        return rootPage.getKEvenlyDistributedSamples(maxK);
    }

    @Override
    public List<DistancedSample> getKNearestSamples(double[] x, int maxK) {
        return rootPage.getKNearestSamples(x, maxK);
    }

    @Override
    public List<DistancedSample> getKNearestInDimensionSamples(double[] x, int dim, int maxK) {
        return rootPage.getKNearestInDimensionSamples(x, dim, maxK);
    }

    @Override
    public Sample getCached(double[] x) {
        return rootPage.getCached(x);
    }

    @Override
    public boolean someLeafOverlaps(double[] x) {
        return rootPage.someLeafOverlaps(x);
    }

    @Override
    public double getVolume() {
        return rootPage.getVolume();
    }

    @Override
    public double getDiameter() {
        return rootPage.getDiameter();
    }

    @Override
    public int getCount() {
        return rootPage.getCount();
    }

    @Override
    public double getMinValue() {
        return rootPage.getMinValue();
    }

    @Override
    public double[] getMaxBounds() {
        return rootPage.getMaxBounds();
    }

    @Override
    public double[] getMinBounds() {
        return rootPage.getMinBounds();
    }

}
