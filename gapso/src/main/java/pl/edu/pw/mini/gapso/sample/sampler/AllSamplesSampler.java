package pl.edu.pw.mini.gapso.sample.sampler;

import pl.edu.pw.mini.gapso.sample.Sample;
import pl.edu.pw.mini.gapso.utils.Generator;

import java.util.ArrayList;
import java.util.List;

public class AllSamplesSampler extends Sampler {
    List<Sample> samples = new ArrayList<>();

    @Override
    public boolean tryStoreSample(Sample sample) {
        samples.add(sample);
        samplesCount++;
        return true;
    }

    public List<Sample> getSamples(int samplesCount) {
        List<Integer> integers =
                Generator.getUniqueIntegerSequence(0, samples.size(), samplesCount);
        List<Sample> returnedSamples = new ArrayList<>();
        for (int i : integers) {
            returnedSamples.add(samples.get(i));
        }
        return returnedSamples;
    }
}
