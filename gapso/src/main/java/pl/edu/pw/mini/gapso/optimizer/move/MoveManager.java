package pl.edu.pw.mini.gapso.optimizer.move;

import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import pl.edu.pw.mini.gapso.utils.Generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MoveManager {
    private final Move[] _moves;
    private final static int maxSize = 10;
    private HashMap<Move, List<List<Double>>> movesImprovementsDictionary;

    public MoveManager(Move[] moves) {
        _moves = moves;
        movesImprovementsDictionary = new HashMap<>();
        for (Move move : _moves) {
            movesImprovementsDictionary.put(move, new ArrayList<>());
        }
    }

    public List<Move> generateMoveSequence(int size) {
        recomputeWeights();
        for (Move move : _moves) {
            movesImprovementsDictionary.get(move).add(0, new ArrayList<>());
            final int movesSize = movesImprovementsDictionary.get(move).size();
            if (movesSize > maxSize) {
                movesImprovementsDictionary.get(move).remove(movesSize - 1);
            }
        }
        for (Move move : _moves) {
            move.newIteration();
        }
        int minAmount = Arrays.stream(_moves).mapToInt(Move::getMinNumber).sum();
        if (minAmount > size) {
            throw new IllegalArgumentException("Too many moves for too little slots");
        }
        List<Move> movesSequence = new ArrayList<>();
        final ArrayList<Pair<Move, Double>> pairs = new ArrayList<>();
        for (Move move : _moves) {
            for (int j = 0; j < move.getMinNumber(); ++j) {
                movesSequence.add(move);
            }
            if (move.isAdaptable()) {
                pairs.add(new Pair<>(move, move.getWeight()));
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
        return randomizeMovesOrder(movesSequence);
    }

    private void recomputeWeights() {
        boolean noPositiveWeights = true;
        for (Move move : _moves) {
            if (move.isAdaptable() && movesImprovementsDictionary.get(move).size() >= maxSize) {
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
                //TODO: beware weight will not be reset after restart
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
        movesImprovementsDictionary.get(selectedMove).get(0).add(Math.max(deltaY, 0.0));
    }

    public void registerGlobalImprovementByMove(Move selectedMove, double deltaY) {
        movesImprovementsDictionary.get(selectedMove).get(0).add(Math.max(deltaY, 0.0));
    }
}
