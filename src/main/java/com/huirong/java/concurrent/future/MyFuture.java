package com.huirong.java.concurrent.future;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created by huirong on 17-3-7.
 */
public class MyFuture {
    interface ProductInfo{

    }

    ProductInfo loadProductInfo()throws MyException{
        return null;
    }
    private final FutureTask<ProductInfo> future = new FutureTask<ProductInfo>(new Callable<ProductInfo>() {
        @Override
        public ProductInfo call() throws MyException {
            return loadProductInfo();
        }
    });
    private final Thread thread = new Thread(future);
    public void start(){
        thread.start();
    }

    public ProductInfo get(){
        try {
            return future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}

class MyException extends Exception{

}
