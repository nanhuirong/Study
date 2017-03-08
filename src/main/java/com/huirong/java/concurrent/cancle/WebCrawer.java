package com.huirong.java.concurrent.cancle;

import scala.tools.nsc.Global;

import javax.annotation.concurrent.GuardedBy;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by huirong on 17-3-8.
 */
public abstract class WebCrawer {
    @GuardedBy("this") private final Set<URL> urls = new HashSet<>();
    private volatile TrackingExecutor executor;

    private final ConcurrentHashMap<URL, Boolean> seen = new ConcurrentHashMap<>();
    private static final long TIMEOUT = 500;
    private static final TimeUnit UNIT = TimeUnit.MILLISECONDS;

    public WebCrawer(URL startURL) {
        urls.add(startURL);
    }

    public synchronized void start(){
        executor = new TrackingExecutor(Executors.newCachedThreadPool());
        for (URL url : urls){
            submitClawlTask(url);
        }
        urls.clear();
    }

    public synchronized void stop() throws InterruptedException {
        try {
            saveUnCrawled(executor.shutdownNow());
            if (executor.awaitTermination(TIMEOUT, UNIT)){
                saveUnCrawled(executor.getCanceledTasks());
            }
        }finally {
            executor = null;
        }
    }

    protected  abstract List<URL> precessPage(URL url);

    private void saveUnCrawled(List<Runnable> uncrawled){
        for (Runnable task : uncrawled){
            urls.add(((CrawlTask) task).getPage());
        }
    }

    private void submitClawlTask(URL url){
        executor.execute(new CrawlTask(url));
    }
    private class CrawlTask implements Runnable{
        private final URL url;

        public CrawlTask(URL url) {
            this.url = url;
        }

        private int count = 0;
        boolean alreadyCrawled(){
            return seen.putIfAbsent(url, true) != null;
        }

        void markUncrawled(){
            seen.remove(url);

        }

        @Override
        public void run() {
            for (URL url : precessPage(url)){
                if (Thread.currentThread().isInterrupted()){
                    return;
                }
                submitClawlTask(url);
            }
        }

        public URL getPage(){
            return url;
        }
    }
}
