package com.huirong.jiangjuan.code.download;

/**
 * Created by Huirong on 17/1/8.
 */
public class Consumer extends Thread {
    private Storage storage;

    public Consumer(Storage storage) {
        this.storage = storage;
    }

    @Override
    public void run() {
        while (!storage.isEmpyty()){
            System.out.println(storage.urls.size());
            try {
                this.storage.start.await();
                Thread.sleep((long) Math.random() * 1000);
                consume();
            }catch (Exception e) {

            }finally{
                this.storage.latch.countDown();
                System.out.println(this.storage.latch);
            }

        }
    }

    private void consume(){
        storage.download();
    }
}
