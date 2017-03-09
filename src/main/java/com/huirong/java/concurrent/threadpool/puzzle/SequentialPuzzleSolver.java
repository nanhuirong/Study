package com.huirong.java.concurrent.threadpool.puzzle;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by huirong on 17-3-9.
 */
public class SequentialPuzzleSolver<P, M> {
    private final Puzzle<P, M> puzzle;
    private final Set<P> seen = new HashSet<P>();

    public SequentialPuzzleSolver(Puzzle<P, M> puzzle) {
        this.puzzle = puzzle;
    }

    public List<M> solve(){
        P position = puzzle.initialPosition();
        return search(new PuzzleNode<P, M>(position, null, null));
    }

    public List<M> search(PuzzleNode<P, M> node){
        if (!seen.contains(node.position)){
            seen.add(node.position);
            if (puzzle.isGoal(node.position)){
                return node.asMoveList();
            }
            for (M move : puzzle.legalMoves(node.position)){
                P position = puzzle.move(node.position, move);
                PuzzleNode<P, M> child = new PuzzleNode<P, M>(position, move, node);
                List<M> result = search(child);
                if (result != null){
                    return result;
                }
            }
        }
        return null;
    }
}
