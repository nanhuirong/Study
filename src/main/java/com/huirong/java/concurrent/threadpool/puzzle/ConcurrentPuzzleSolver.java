package com.huirong.java.concurrent.threadpool.puzzle;

import java.util.List;
import java.util.concurrent.*;

/**
 * Created by huirong on 17-3-9.
 */
public class ConcurrentPuzzleSolver<P, M> {
    private final Puzzle<P, M> puzzle;
    private final ExecutorService executor;
    private final ConcurrentMap<P, Boolean> seen;
    protected final ValueLatch<PuzzleNode<P, M>> solution = new ValueLatch<PuzzleNode<P, M>>();

    public ConcurrentPuzzleSolver(Puzzle<P, M> puzzle) {
        this.puzzle = puzzle;
        this.executor = initThreadPool();
        this.seen = new ConcurrentHashMap<P, Boolean>();
        if (executor instanceof ThreadPoolExecutor){
            ThreadPoolExecutor tpe = (ThreadPoolExecutor) executor;
            tpe.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        }
    }

    private ExecutorService initThreadPool(){
        return Executors.newCachedThreadPool();
    }

    private List<M> solve() throws InterruptedException {
        try {
            P position = puzzle.initialPosition();
            executor.execute(newTask(position, null, null));
            PuzzleNode<P, M> solnPuzzleNode = solution.getValue();
            return (solnPuzzleNode == null) ? null : solnPuzzleNode.asMoveList();
        }finally {
            executor.shutdown();
        }
    }

    protected Runnable newTask(P position, M move, PuzzleNode<P, M> node){
        return new SolverTask(position, move, node);
    }


    protected class SolverTask extends PuzzleNode<P, M> implements Runnable{

        public SolverTask(P position, M move, PuzzleNode<P, M> pre) {
            super(position, move, pre);
        }

        @Override
        public void run() {
            if (solution.isSet() || seen.putIfAbsent(position, true) != null){
                return;
            }
            if (puzzle.isGoal(position)){
                solution.setValue(this);
            }else {
                for (M move : puzzle.legalMoves(position)){
                    executor.execute(newTask(puzzle.move(position, move), move, this));
                }
            }
        }
    }
}
