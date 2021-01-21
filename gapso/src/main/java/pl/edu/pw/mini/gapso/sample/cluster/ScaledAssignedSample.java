package pl.edu.pw.mini.gapso.sample.cluster;

import pl.edu.pw.mini.gapso.sample.Sample;

import java.util.ArrayList;
import java.util.List;

public class ScaledAssignedSample extends Sample {
    private Sample originalSample;
    private Sample scaledSample;
    private List<ScaledAssignedSample> group;

    public ScaledAssignedSample(Sample originalSample, Sample scaledSample) {
        this.originalSample = originalSample;
        this.scaledSample = scaledSample;
        group = new ArrayList<>();
        group.add(this);
    }

    public Sample getOriginalSample() {
        return originalSample;
    }

    public List<ScaledAssignedSample> getGroup() {
        return group;
    }

    @Override
    public double[] getX() {
        return scaledSample.getX();
    }

    @Override
    public double getY() {
        return scaledSample.getY();
    }

    public boolean isSameGroup(ScaledAssignedSample otherSample) {
        return this.group == otherSample.group;
    }

    public int mergeGroups(ScaledAssignedSample otherSample) {
        List<ScaledAssignedSample> otherGroup = otherSample.group;
        for (ScaledAssignedSample sampleInOtherGroup : otherGroup) {
            sampleInOtherGroup.group = this.group;
            group.add(sampleInOtherGroup);
        }
        otherGroup.clear();
        return group.size();
    }
}
