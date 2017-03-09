package com.huirong.java.concurrent.threadpool;

import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * Created by huirong on 17-3-8.
 */
public abstract class TransformSequential {
    void processSequentially(List<Element> elements){
        for (Element element : elements){
            process(element);
        }
    }

    void processParallel(Executor executor, List<Element> elements){
        for (final Element element : elements){
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    process(element);
                }
            });
        }
    }

    public <T> void sequentialRecursive(List<Node<T>> nodes, Collection<T> results){
        for (Node<T> node : nodes){
            results.add(node.compute());
            sequentialRecursive(node.getCjildren(), results);
        }
    }

    public <T> void parallelRecursive(final Executor executor,
                                      List<Node<T>> nodes,
                                      final Collection<T> results){
        for (final Node<T> node : nodes){
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    results.add(node.compute());
                }
            });
            parallelRecursive(executor, node.getCjildren(), results);
        }
    }

    public <T> Collection<T> getParallelResults(List<Node<T>> nodes) throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        Queue<T> resultQueue = new ConcurrentLinkedDeque<T>();
        parallelRecursive(executorService, nodes, resultQueue);
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        return resultQueue;
    }

    public abstract void process(Element element);



    interface Element{

    }
    interface Node<T>{
        T compute();
        List<Node<T>> getCjildren();
    }
}
