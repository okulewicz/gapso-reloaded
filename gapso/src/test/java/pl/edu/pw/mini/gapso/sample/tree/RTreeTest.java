package pl.edu.pw.mini.gapso.sample.tree;

import org.junit.Assert;
import org.junit.Test;
import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.sample.SingleSample;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RTreeTest {
    @Test
    public void addMoreSamplesThanPageSizeTest() {
        //DATA
        SingleSample sample_1_0__0_0 = new SingleSample(new double[]{1.0, 0.0}, 0.0);
        SingleSample sample_2_0__1_0 = new SingleSample(new double[]{2.0, 1.0}, 0.0);
        SingleSample sample_1_4__2_0 = new SingleSample(new double[]{1.4, 2.0}, 0.0);
        SingleSample sample_1_6__3_0 = new SingleSample(new double[]{1.6, 3.0}, 0.0);
        SingleSample sample_1_6__1_6 = new SingleSample(new double[]{1.6, 1.6}, 0.0);

        //SETUP
        int pageSize = 4;
        double volume =
                Math.abs(sample_1_0__0_0.getX()[0] - sample_2_0__1_0.getX()[0]) *
                        Math.abs(sample_1_0__0_0.getX()[1] - sample_1_6__3_0.getX()[1]);
        double diameter =
                Math.sqrt(
                        Math.abs(sample_1_0__0_0.getX()[0] - sample_2_0__1_0.getX()[0]) *
                                Math.abs(sample_1_0__0_0.getX()[0] - sample_2_0__1_0.getX()[0]) +
                                Math.abs(sample_1_0__0_0.getX()[1] - sample_1_6__3_0.getX()[1]) *
                                        Math.abs(sample_1_0__0_0.getX()[1] - sample_1_6__3_0.getX()[1])
                );
        Page leafPage = new LeafPage(pageSize);
        Page nodePage = new NodePage(pageSize);

        Assert.assertTrue(leafPage.indexSample(sample_1_0__0_0));
        Assert.assertTrue(leafPage.indexSample(sample_2_0__1_0));
        Assert.assertTrue(leafPage.indexSample(sample_1_4__2_0));
        Assert.assertTrue(leafPage.indexSample(sample_1_6__3_0));
        Assert.assertFalse(leafPage.indexSample(sample_1_6__1_6));

        //TEST
        List<Page> pages = leafPage.splitPage();
        Assert.assertEquals(2, pages.size());

        Assert.assertTrue(nodePage.indexSample(sample_1_0__0_0));
        Assert.assertTrue(nodePage.indexSample(sample_2_0__1_0));
        Assert.assertTrue(nodePage.indexSample(sample_1_4__2_0));
        Page.MeasurementsDTO futureMeasures = nodePage.getPossiblePageMeasures(sample_1_6__3_0.getX());
        Assert.assertEquals(volume, futureMeasures.getVolume(), 1e-8);
        Assert.assertEquals(diameter, futureMeasures.getDiameter(), 1e-8);

        Assert.assertTrue(nodePage.indexSample(sample_1_6__3_0));
        Assert.assertTrue(nodePage.indexSample(sample_1_6__1_6));
        Page.MeasurementsDTO measures = nodePage.getPageMeasurements();
        Assert.assertEquals(volume, measures.getVolume(), 1e-8);
        Assert.assertEquals(diameter, measures.getDiameter(), 1e-8);
    }

    @Test
    public void nodePageCapacityLimitsTest() {
        //DATA
        int pageSize = 4;
        List<Sample> samples = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < pageSize * pageSize + 1; ++i) {
            SingleSample sample = new SingleSample(new double[]{r.nextDouble(), r.nextDouble()}, r.nextDouble());
            samples.add(sample);
        }

        //SETUP
        Page page = new NodePage(pageSize);
        RTree rTree = new RTree(pageSize);

        //TEST
        int sampleIdx;
        //first samples must fit
        for (sampleIdx = 0; sampleIdx < (pageSize - 1) * (pageSize / 2) + pageSize; ++sampleIdx) {
            Assert.assertTrue(page.indexSample(samples.get(sampleIdx)));
        }
        //depends on samples distributions - do not test each one
        boolean fittedAll = true;
        for (; sampleIdx < pageSize * pageSize + 1; ++sampleIdx) {
            fittedAll &= page.indexSample(samples.get(sampleIdx));
        }
        //one of those samples cannot fit for sure
        Assert.assertFalse(fittedAll);

        //in complete object all should be added
        for (sampleIdx = 0; sampleIdx < pageSize * pageSize + 1; ++sampleIdx) {
            Assert.assertTrue(rTree.indexSample(samples.get(sampleIdx)));
        }
        Assert.assertEquals(pageSize * pageSize + 1, rTree.getCount());
        Assert.assertEquals(
                samples.stream().mapToDouble(Sample::getY).min().orElse(Double.POSITIVE_INFINITY),
                rTree.getMinValue(),
                0.0);
    }

}
