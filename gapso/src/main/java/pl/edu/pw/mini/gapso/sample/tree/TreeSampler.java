package pl.edu.pw.mini.gapso.sample.tree;

import pl.edu.pw.mini.gapso.sample.DistancedSample;
import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.sample.Sampler;

import java.util.List;
import java.util.stream.Collectors;

public class TreeSampler extends Sampler {
    public static final int PAGE_SIZE = 20;
    private final RTree tree;

    public TreeSampler() {
        tree = new RTree(PAGE_SIZE);
    }

    @Override
    public boolean tryStoreSample(Sample sample) {
        if (tree.indexSample(sample)) {
            samplesCount++;
            return true;
        }
        return false;
    }

    public List<Sample> getKNearestSamples(double[] refPoint, int k) {
        return tree.getKNearestSamples(refPoint, k)
                .stream().map(DistancedSample::getSample)
                .collect(Collectors.toList());
    }

    public void clear() {
        tree.clearIndex();
    }
}
