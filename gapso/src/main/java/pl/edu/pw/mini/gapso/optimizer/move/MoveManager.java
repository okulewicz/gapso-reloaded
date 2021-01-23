package pl.edu.pw.mini.gapso.optimizer.move;

import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import pl.edu.pw.mini.gapso.configuration.MoveManagerConfiguration;
import pl.edu.pw.mini.gapso.utils.Generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MoveManager {
    private final Move[] _moves;
    private HashMap<Move, List<List<Double>>> movesImprovementsDictionary;
    private final int maxHistorySize;
    private final boolean initiallyAdaptMoves;
    private final boolean includePersonalImprovements;
    private final boolean includeGlobalImprovements;
    private final double switchingAdaptationOffProbability;
    private boolean adaptMoves;

    public MoveManager(Move[] moves, MoveManagerConfiguration configuration) {
        initiallyAdaptMoves = configuration.isAdaptMoves();
        includeGlobalImprovements = configuration.isIncludeGlobalImprovements();
        includePersonalImprovements = configuration.isIncludePersonalImprovements();
        switchingAdaptationOffProbability = configuration.getSwitchingAdaptationOffProbability();
        maxHistorySize = configuration.getMaxHistorySize();
        _moves = moves;
        reset();
        movesImprovementsDictionary = new HashMap<>();
        for (Move move : _moves) {
            movesImprovementsDictionary.put(move, new ArrayList<>());
        }
    }

    public List<Move> generateMoveSequence(int size) {
        if (adaptMoves) {
            recomputeWeights();
        }
        checkConfigurationConsistency(size);
        final ArrayList<Pair<Move, Double>> pairs = getMovesDistributionWeights();
        List<Move> movesSequence = generateMovesAccordingToWeights(size, pairs);
        return randomizeMovesOrder(movesSequence);
    }

    protected ArrayList<Pair<Move, Double>> getMovesDistributionWeights() {
        final ArrayList<Pair<Move, Double>> pairs = new ArrayList<>();
        for (Move move : _moves) {
            if (move.isAdaptable()) {
                pairs.add(new Pair<>(move, move.getWeight()));
            }
        }
        return pairs;
    }

    protected void checkConfigurationConsistency(int size) {
        int minAmount = Arrays.stream(_moves).mapToInt(Move::getMinNumber).sum();
        if (minAmount > size) {
            throw new IllegalArgumentException("Too many moves for too little slots");
        }
    }

    public void startNewIteration() {
        for (Move move : _moves) {
            movesImprovementsDictionary.get(move).add(0, new ArrayList<>());
            final int movesSize = movesImprovementsDictionary.get(move).size();
            if (movesSize > maxHistorySize) {
                movesImprovementsDictionary.get(move).remove(movesSize - 1);
            }
        }
        for (Move move : _moves) {
            move.newIteration();
        }
    }

    protected List<Move> generateMovesAccordingToWeights(int size, ArrayList<Pair<Move, Double>> pairs) {
        List<Move> movesSequence = new ArrayList<>();
        for (Move move : _moves) {
            final int movesCount = Math.max(
                    (int) Math.floor(size * move.getMinimalRatio()),
                    move.getMinNumber());
            for (int j = 0; j < movesCount; ++j) {
                movesSequence.add(move);
            }
        }
        if (movesSequence.size() < size) {
            EnumeratedDistribution<Move> enumeratedDistribution = new EnumeratedDistribution<>(
                    Generator.RANDOM,
                    pairs
            );
            movesSequence.addAll(
                    Arrays.stream(
                            enumeratedDistribution.sample(size - movesSequence.size(), new Move[0]))
                            .collect(Collectors.toList()));
        }
        return movesSequence;
    }

    private void recomputeWeights() {
        boolean noPositiveWeights = true;
        for (Move move : _moves) {
            if (move.isAdaptable() && movesImprovementsDictionary.get(move).size() >= maxHistorySize) {
                double sum = movesImprovementsDictionary.get(move)
                        .stream()
                        .mapToDouble(
                                list -> list.stream().mapToDouble(el -> el).sum()
                        ).sum();
                int count = movesImprovementsDictionary.get(move)
                        .stream()
                        .mapToInt(
                                list -> (int) list.stream().mapToDouble(el -> el).count()
                        ).sum();
                double weight = sum / count;
                move.setWeight(weight);
                if (weight > 0.0) {
                    noPositiveWeights = false;
                }
            }
        }
        if (noPositiveWeights) {
            for (Move move : _moves) {
                if (move.isAdaptable()) {
                    move.resetWeight();
                }
            }
        }
    }

    private List<Move> randomizeMovesOrder(List<Move> movesSequence) {
        List<Move> tempMovesList = new ArrayList<>(movesSequence);
        List<Move> rearrangedMoves = new ArrayList<>();
        while (!tempMovesList.isEmpty()) {
            int idx = Generator.RANDOM.nextInt(tempMovesList.size());
            Move move = tempMovesList.get(idx);
            tempMovesList.remove(idx);
            rearrangedMoves.add(move);
        }
        return rearrangedMoves;
    }

    public void registerPersonalImprovementByMove(Move selectedMove, double deltaY) {
        if (deltaY > 0) {
            selectedMove.registerPersonalImprovement(deltaY);
        }
        if (includePersonalImprovements) {
            movesImprovementsDictionary.get(selectedMove).get(0).add(Math.max(deltaY, 0.0));
        }
    }

    public void registerGlobalImprovementByMove(Move selectedMove, double deltaY) {
        if (includeGlobalImprovements) {
            movesImprovementsDictionary.get(selectedMove).get(0).add(Math.max(deltaY, 0.0));
        }
    }

    public void maySwitchOffAdaptation() {
        if (Generator.RANDOM.nextDouble() < switchingAdaptationOffProbability) {
            adaptMoves = false;
            for (Move move : _moves) {
                if (move.isAdaptable()) {
                    move.resetWeight();
                }
            }
        }
    }

    public void reset() {
        adaptMoves = initiallyAdaptMoves;
        Arrays.stream(_moves).forEach(Move::resetWeight);
    }

    public Move[] getMoves() {
        return _moves;
    }
}
