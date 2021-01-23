package pl.edu.pw.mini.gapso.configuration;

import pl.edu.pw.mini.gapso.optimizer.move.Move;
import pl.edu.pw.mini.gapso.optimizer.move.MoveManager;

import java.util.List;

public class MoveManagerConfiguration {
    private boolean adaptMoves;
    private double switchingAdaptationOffProbability;
    private boolean includePersonalImprovements;
    private boolean includeGlobalImprovements;
    private List<MoveConfiguration> moves;

    public MoveManagerConfiguration(boolean adaptMoves, double switchingAdaptationOffProbability, boolean includePersonalImprovements, boolean includeGlobalImprovements, List<MoveConfiguration> moves) {
        this.adaptMoves = adaptMoves;
        this.switchingAdaptationOffProbability = switchingAdaptationOffProbability;
        this.includePersonalImprovements = includePersonalImprovements;
        this.includeGlobalImprovements = includeGlobalImprovements;
        this.moves = moves;
    }


    public boolean isAdaptMoves() {
        return adaptMoves;
    }

    public double getSwitchingAdaptationOffProbability() {
        return switchingAdaptationOffProbability;
    }

    public boolean isIncludePersonalImprovements() {
        return includePersonalImprovements;
    }

    public boolean isIncludeGlobalImprovements() {
        return includeGlobalImprovements;
    }

    public MoveManager getMoveManager() {
        Move[] movesArray = moves.stream().map(MoveConfiguration::getMove).toArray(Move[]::new);
        return new MoveManager(movesArray, this);
    }

}
