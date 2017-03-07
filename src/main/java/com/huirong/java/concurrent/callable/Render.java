package com.huirong.java.concurrent.callable;

import java.util.List;
import java.util.concurrent.*;

/**
 * Created by huirong on 17-3-7.
 * 并行下载图片
 */
public abstract class Render {
    private final ExecutorService service;

    public Render(ExecutorService service) {
        this.service = service;
    }

    void renderPage(CharSequence source){
        List<ImageInfo> infos = scanForImageInfo(source);
        CompletionService<ImageData> completionService = new ExecutorCompletionService<ImageData>(service);
        for (final ImageInfo info : infos){
            completionService.submit(new Callable<ImageData>() {
                @Override
                public ImageData call() throws Exception {
                    return info.downloadImage();
                }
            });
            renderText(source);
        }
        try {
            for (int i = 0; i< infos.size(); i++){
                Future<ImageData> future = completionService.take();
                ImageData date = future.get();
            }
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }catch (ExecutionException e){
            e.printStackTrace();
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
