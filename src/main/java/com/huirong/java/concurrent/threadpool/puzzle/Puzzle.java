package com.huirong.java.concurrent.threadpool.puzzle;

import java.util.Set;

/**
 * Created by huirong on 17-3-9.
 * 表示般箱子问题的抽象
 */
public interface Puzzle<P, M> {
    P initialPosition();
    boolean isGoal(P position);
    Set<M> legalMoves(P position);
    P move(P position, M move);
}
