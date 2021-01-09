package pl.edu.pw.mini.gapso.optimization.move;

import pl.edu.pw.mini.gapso.generator.Generator;

public class DEBest1Bin {
    public double[] getDESample(double[] current, double[] best, double[] diffVector1, double[] diffVector2, double scale, double crossProb) {
        final int dim = current.length;
        double[] tryX = new double[dim];
        int alwaysSwitchIdx = Generator.RANDOM.nextInt(dim);
        for (int dimIdx = 0; dimIdx < dim; ++dimIdx) {
            tryX[dimIdx] = best[dimIdx] + scale * (diffVector1[dimIdx] - diffVector2[dimIdx]);
            if (alwaysSwitchIdx != dimIdx) {
                double testIfSwitch = Generator.RANDOM.nextDouble();
                if (testIfSwitch > crossProb) {
                    tryX[dimIdx] = current[dimIdx];
                }
            }
        }
        return tryX;
    }
}
