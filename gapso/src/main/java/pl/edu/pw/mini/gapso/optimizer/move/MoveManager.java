package pl.edu.pw.mini.gapso.optimizer.move;

import java.util.ArrayList;
import java.util.List;

public class MoveManager {
    private final Move[] _moves;

    public MoveManager(Move[] moves) {
        _moves = moves;
    }

    public List<Move> generateMoveSequence(int size) {
        return new ArrayList<>();
    }
}
