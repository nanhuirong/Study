package com.huirong.java.concurrent.callable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by huirong on 17-3-7.
 */
public  abstract class FutureRender {
    private final ExecutorService executor = Executors.newCachedThreadPool();
    void renerPage(CharSequence source){
        final List<ImageInfo> imageInfos = scanForImageInfo(source);
        Callable<List<ImageData>> task = new Callable<List<ImageData>>() {
            @Override
            public List<ImageData> call() throws Exception {
                List<ImageData> result = new ArrayList<>();
                for (ImageInfo info : imageInfos){
                    result.add(info.downloadImage());
                }
                return result;
            }
        } ;
        Future<List<ImageData>> future = executor.submit(task);
        renderText(source);
        try {
            List<ImageData> imageData = future.get();
            for (ImageData data : imageData){
                renderImage(data);
            }
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
            future.cancel(true);
        }catch (ExecutionException e){

        }

    }

    interface ImageData{

    }
    interface ImageInfo{
        ImageData downloadImage();
    }
    abstract void renderText(CharSequence source);
    abstract List<ImageInfo> scanForImageInfo(CharSequence source);
    abstract void renderImage(ImageData data);
}
