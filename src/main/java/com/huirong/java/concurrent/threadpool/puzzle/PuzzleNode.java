package com.huirong.java.concurrent.threadpool.puzzle;

import jdk.nashorn.internal.ir.annotations.Immutable;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by huirong on 17-3-9.
 */
@Immutable
public class PuzzleNode<P, M> {
    final P position;
    final M move;
    final PuzzleNode<P, M> pre;

    public PuzzleNode(P position, M move, PuzzleNode<P, M> pre) {
        this.position = position;
        this.move = move;
        this.pre = pre;
    }

    List<M> asMoveList(){
        List<M> solution = new LinkedList<M>();
        for (PuzzleNode<P, M> node = this; node.move != null; node = node.pre){
            solution.add(0, node.move);
        }
        return solution;
    }
}
