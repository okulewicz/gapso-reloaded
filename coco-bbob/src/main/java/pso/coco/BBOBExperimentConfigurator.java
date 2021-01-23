package pso.coco;

public interface BBOBExperimentConfigurator {
    int[] getDimensionsList();

    String[] getFunctionsList();

    String getExperimentName();

    String getBuildId();

    boolean isFunctionMappingExperiment();
}
