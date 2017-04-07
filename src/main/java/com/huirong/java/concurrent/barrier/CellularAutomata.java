package com.huirong.java.concurrent.barrier;

/**
 * Created by huirong on 17-3-7.
 */
import java.util.concurrent.*;
public class CellularAutomata {
    private final Board mainBoard;
    private final CyclicBarrier barrier;
    private final Worker[] workers;

    public CellularAutomata(Board mainBoard) {
        this.mainBoard = mainBoard;
        int count = Runtime.getRuntime().availableProcessors() / 2;
        this.barrier = new CyclicBarrier(count, new Runnable() {
            @Override
            public void run() {
                mainBoard.commitNewValues();
            }
        });
        this.workers = new Worker[count];
        for (int i = 0; i < count; i++){
            workers[i] = new Worker(mainBoard.getSubBoard(count, i));
        }
    }
    private class Worker implements Runnable{
        private final Board board;
        public Worker(Board board){
            this.board = board;
        }

        @Override
        public void run() {
            while (!board.hasConverged()){
                for (int x = 0; x < board.getMaxX(); x++){
                    for (int y = 0; y < board.getMaxY(); y++){
                        board.setNewValue(x, y, computeValue(x, y));
                    }
                }
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        }
        private int computeValue(int x, int y){
            return 0;
        }
    }
    public void start(){
        for (int i = 0; i < workers.length; i++){
            new Thread(workers[i]).start();
        }
        mainBoard.waitForConvergence();
    }
    interface Board{
        int getMaxX();
        int getMaxY();
        int getValue(int x, int y);
        int setNewValue(int x, int y, int value);
        void commitNewValues();
        boolean hasConverged();
        void waitForConvergence();
        Board getSubBoard(int numPartitions, int index);
    }
}
