package com.huirong.java.concurrent.tool;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * Created by huirong on 17-3-13.
 * 利用AQS实现二元闭锁
 */
@ThreadSafe
public class OneShotLatch {
    private final Sync sync = new Sync();
    public void sigal(){
        sync.releaseShared(0);
    }
    public void await() throws InterruptedException {
        sync.acquireSharedInterruptibly(0);
    }

    private class Sync extends AbstractQueuedSynchronizer{
        @Override
        protected int tryAcquireShared(int ignore) {
            return (getState() == 1) ? 1 : -1;
        }

        @Override
        protected boolean tryReleaseShared(int ignore) {
            setState(1);
            return true;
        }
    }
}
