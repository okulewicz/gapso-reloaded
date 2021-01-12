package pl.edu.pw.mini.gapso.optimizer.move;

import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import pl.edu.pw.mini.gapso.generator.Generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MoveManager {
    private final Move[] _moves;

    public MoveManager(Move[] moves) {
        _moves = moves;
    }

    public List<Move> generateMoveSequence(int size) {
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
        return
                movesSequence
                        .stream()
                        .sorted(Comparator.comparingDouble(m -> Generator.RANDOM.nextDouble()))
                        .collect(Collectors.toList());
    }
}
