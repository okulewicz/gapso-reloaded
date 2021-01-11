package pso.coco;

public interface BBOBExperimentConfigurator {
    int[] getDimensionsList();

    String[] getFunctionsList();

    String getExperimentName();

    boolean isFunctionMappingExperiment();
}
