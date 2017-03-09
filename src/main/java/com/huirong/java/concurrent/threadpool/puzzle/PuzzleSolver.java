package com.huirong.java.concurrent.threadpool.puzzle;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by huirong on 17-3-9.
 * 解决并行情况下找不到答案的问题
 */
public class PuzzleSolver<P, M> extends  ConcurrentPuzzleSolver<P, M> {
    public PuzzleSolver(Puzzle<P, M> puzzle) {
        super(puzzle);
    }
    private final AtomicInteger taskCount = new AtomicInteger(0);

    @Override
    protected Runnable newTask(P position, M move, PuzzleNode<P, M> node) {
        return new CountingSolverTask(position, move, node);
    }

    class CountingSolverTask extends SolverTask{
        public CountingSolverTask(P position, M move, PuzzleNode<P, M> pre) {
            super(position, move, pre);
            taskCount.incrementAndGet();
        }

        public void run(){
            try {
                super.run();
            }finally {
                if (taskCount.decrementAndGet() == 0){
                    solution.setValue(null);
                }
            }
        }
    }
}
