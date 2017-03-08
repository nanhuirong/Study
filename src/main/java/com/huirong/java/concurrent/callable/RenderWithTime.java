package com.huirong.java.concurrent.callable;

import java.util.concurrent.*;

/**
 * Created by huirong on 17-3-8.
 */
public class RenderWithTime {
    private final AD DEFAULT_AD = new AD();
    private final long TIME_OUT = 1000;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    Page renderPageWithAd(){
        long end = System.nanoTime() + TIME_OUT;
        Future<AD> future = executor.submit(new FetchADTask());
        Page page = renderPageBody();
        AD ad = null;
        try {
            long timeLeft = end - System.nanoTime();
            ad = future.get(timeLeft, TimeUnit.NANOSECONDS);
        }catch (InterruptedException e) {
        } catch (ExecutionException e) {
            ad = DEFAULT_AD;
        } catch (TimeoutException e) {
            ad = DEFAULT_AD;
            future.cancel(true);
        }
        page.setAD(ad);
        return page;
    }

    Page renderPageBody(){
        return new Page();
    }




    class  AD{

    }

    class Page{
        public void setAD(AD ad){}
    }
    class FetchADTask implements Callable<AD>{
        @Override
        public AD call() throws Exception {
            return new AD();
        }
    }

}
