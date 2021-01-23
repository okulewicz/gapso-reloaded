package pl.edu.pw.mini.gapso.sample.tree;

import org.apache.commons.math3.ml.distance.EuclideanDistance;
import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.sample.SingleSample;

import java.util.*;

public class NodePage extends Page {
    private List<Page> pages = new ArrayList<>();

    protected NodePage(int pageSize) {
        super(pageSize);
        pages.add(new LeafPage(pageSize));
    }

    protected NodePage(List<Page> initialPages, int pageSize) {
        super(pageSize);
        pages.addAll(initialPages);
        for (Page page : initialPages
        ) {
            updateStatistics(new SingleSample(page.minBounds, page.minValue), page.count);
            updateStatistics(new SingleSample(page.maxBounds, page.maxValue), 0);
        }
    }

    @Override
    public void clearIndex() {
        for (Page page : pages
        ) {
            page.clearIndex();
        }
        pages.clear();
    }

    @Override
    public boolean indexSample(Sample sample) {
        Page closestPage = null;
        double distance = Double.POSITIVE_INFINITY;
        for (Page page : pages) {
            if (page.contains(sample.getX())) {
                if (tryIndexSampleOnPage(sample, page)) {
                    updateStatistics(sample, 1);
                    return true;
                }
            } else {
                double testDistance = page.getMinDistanceLowerBoundary(sample.getX());
                if (testDistance < distance) {
                    distance = testDistance;
                    closestPage = page;
                }
            }
        }
        if (closestPage == null)
            return false;
        if (tryIndexSampleOnPage(sample, closestPage)) {
            updateStatistics(sample, 1);
            return true;
        }
        return false;
    }

    private boolean tryIndexSampleOnPage(Sample sample, Page page) {
        if (page.indexSample(sample)) {
            return true;
        } else if (pages.size() < pageSize) {
            return splitPageAndAddSample(sample, page);
        }
        return false;
    }

    private boolean splitPageAndAddSample(Sample sample, Page page) {
        List<Page> splittedPages = page.splitPage();
        if (splittedPages.get(0).getMinDistanceLowerBoundary(sample.getX()) < splittedPages.get(1).getMinDistanceLowerBoundary(sample.getX())) {
            if (splittedPages.get(0).indexSample(sample)) {
                replacePageWithSplit(page, splittedPages);
                return true;
            }
        } else {
            if (splittedPages.get(1).indexSample(sample)) {
                replacePageWithSplit(page, splittedPages);
                return true;
            }
        }
        return false;
    }

    private void replacePageWithSplit(Page page, List<Page> splittedPages) {
        pages.addAll(pages.indexOf(page), splittedPages);
        pages.remove(page);
    }

    @Override
    public List<Page> splitPage() {
        double largestDistance = Double.NEGATIVE_INFINITY;
        Page seed1 = null;
        Page seed2 = null;
        for (Page page1 : pages
        ) {
            for (Page page2 : pages
            ) {
                double testDistamce = new EuclideanDistance().compute(page1.minBounds, page2.maxBounds);
                if (testDistamce > largestDistance && page1 != page2) {
                    seed1 = page1;
                    seed2 = page2;
                    largestDistance = testDistamce;
                }
            }
        }
        List<Page> pages1 = new ArrayList<>();
        List<Page> pages2 = new ArrayList<>();
        List<Page> pagesToAssign = new ArrayList<>(pages);
        pagesToAssign.remove(seed1);
        pagesToAssign.remove(seed2);
        pages1.add(seed1);
        pages2.add(seed2);
        while (!pagesToAssign.isEmpty()) {
            //Add to one which is still not half full
            if (pages1.size() >= pageSize / 2) {
                pages2.add(pagesToAssign.get(0));
            } else if (pages2.size() >= pageSize / 2) {
                pages1.add(pagesToAssign.get(0));
                //Add to closest
            } else {
                MeasurementsDTO measurements1 = getPotentialSize(pages1, pagesToAssign.get(0));
                MeasurementsDTO measurements2 = getPotentialSize(pages2, pagesToAssign.get(0));
                if (measurements1.getVolume() < measurements2.getVolume()) {
                    pages1.add(pagesToAssign.get(0));
                } else if (measurements1.getVolume() > measurements2.getVolume()) {
                    pages2.add(pagesToAssign.get(0));
                } else if (measurements1.getDiameter() < measurements2.getDiameter()) {
                    pages1.add(pagesToAssign.get(0));
                } else {
                    pages2.add(pagesToAssign.get(0));
                }
            }
            pagesToAssign.remove(0);
        }
        List<Page> splittedPages = new ArrayList<>();
        splittedPages.add(new NodePage(pages1, pageSize));
        splittedPages.add(new NodePage(pages2, pageSize));
        return splittedPages;
    }

    @Override
    public List<Sample> getKEvenlyDistributedSamples(int maxK) {
        HashMap<Page, Integer> samplesCount = new HashMap<>();
        for (Page page : pages
        ) {
            samplesCount.put(page, maxK / pages.size());
        }

        int n = pages.size();
        Set<Integer> idxSamples = getKUniqueRandomIntegers(maxK - ((maxK / pages.size()) * pages.size()), n);
        for (int idx : idxSamples) {
            //distributing reminder from division
            samplesCount.merge(pages.get(idx), 1, Integer::sum);
        }

        List<Sample> returnedSamples = new ArrayList<>();
        for (Page page : pages
        ) {
            returnedSamples.addAll(page.getKEvenlyDistributedSamples(samplesCount.get(page)));
        }
        return returnedSamples;
    }

    @Override
    public List<DistancedSample> getKNearestSamples(double[] x, int maxK) {
        List<DistancedSample> kSamples = new ArrayList<>();
        //iterate over pages in order to get better and better bounds on the problem
        //remove such pages as have the lower bound higher than kth element on list
        List<Page> pagesToConsider = new ArrayList<>(pages);
        while (!pagesToConsider.isEmpty()) {
            if (filterOutPages(x, maxK, kSamples, pagesToConsider)) continue;
            List<DistancedSample> moreSamples = pagesToConsider.get(0).getKNearestSamples(x, maxK);
            pagesToConsider.remove(0);
            kSamples = DistancedSample.mergeOrderedLists(kSamples, moreSamples, maxK);
        }
        return kSamples;
    }

    @Override
    public List<DistancedSample> getKNearestInDimensionSamples(double[] x, int dim, int maxK) {
        List<DistancedSample> kSamples = new ArrayList<>();
        //iterate over pages in order to get better and better bounds on the problem
        //remove such pages as have the lower bound higher than kth element on list
        List<Page> pagesToConsider = new ArrayList<>(pages);
        while (!pagesToConsider.isEmpty()) {
            if (filterOutPages(x, maxK, kSamples, pagesToConsider)) continue;
            List<DistancedSample> moreSamples = pagesToConsider.get(0).getKNearestInDimensionSamples(x, dim, maxK);
            pagesToConsider.remove(0);
            kSamples = DistancedSample.mergeOrderedLists(kSamples, moreSamples, maxK);
        }
        return kSamples;
    }

    @Override
    public Sample getCached(double[] x) {
        for (Page page : pages) {
            if (page.contains(x)) {
                Sample sample = page.getCached(x);
                if (sample != null) {
                    return sample;
                }
            }
        }
        return null;
    }

    @Override
    public boolean someLeafOverlaps(double[] x) {
        if (!super.contains(x))
            return false;
        return pages.stream().anyMatch(p -> p.someLeafOverlaps(x));
    }

    public MeasurementsDTO getPotentialSize(List<Page> pages, Page newPage) {
        double[] minBounds = Arrays.copyOf(newPage.minBounds, newPage.minBounds.length);
        double[] maxBounds = Arrays.copyOf(newPage.maxBounds, newPage.maxBounds.length);
        for (Page singlePage : pages) {
            for (int i = 0; i < minBounds.length && i < maxBounds.length; ++i) {
                minBounds[i] = Math.min(minBounds[i], singlePage.minBounds[i]);
                maxBounds[i] = Math.max(maxBounds[i], singlePage.maxBounds[i]);
            }
        }
        return getMeasurementsDTOAndUpdateBounds(minBounds, minBounds, maxBounds);
    }

    private boolean filterOutPages(double[] x, int maxK, List<DistancedSample> kSamples, List<Page> pagesToConsider) {
        if (kSamples.size() >= maxK) {
            if (pagesToConsider.get(0).getMinDistanceLowerBoundary(x) > kSamples.get(maxK - 1).getDistance()) {
                pagesToConsider.remove(0);
                return true;
            }
        }
        return false;
    }
}
