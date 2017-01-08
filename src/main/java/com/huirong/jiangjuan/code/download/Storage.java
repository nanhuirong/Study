package com.huirong.jiangjuan.code.download;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Huirong on 17/1/8.
 */
public class Storage {
    //存放下载的URL
//    private Map<String, String> urls = new HashMap<String, String>();
     List<String> urls = new ArrayList<String>();
    //存放线程停止标记
    public volatile CountDownLatch latch;
    public volatile CountDownLatch start = new CountDownLatch(1);

    public Storage() {
        this.urls = convertMapToList();
        latch = new CountDownLatch(urls.size());
        System.out.println(urls.size());
        System.out.println(latch);
    }

    public boolean isEmpyty(){
        return this.urls.size() == 0;
    }

    private List<String> convertMapToList(){
        DownLoad downLoad = new DownLoad();
        List<String> list = new ArrayList<String>();
        Map<String, String> map = downLoad.getUrls();
        for (Map.Entry entry : map.entrySet()){
            list.add((String) entry.getKey());
            list.add((String) entry.getValue());
        }
        return list;
    }

    public void waitForFinished(){
        try {
            System.out.println("等待结束");
            latch.await();
        }catch (InterruptedException e){

        }

    }

    public void download(){
        String url = null;
        synchronized (urls){
            if (urls.size() >= 0){
                url = urls.get(0);
                urls.remove(url);
            }
            urls.notifyAll();
            System.out.println(urls.size());
        }
        if (url != null){
            URLTool.downloadFormUrl(url, LocalPath.getScrapePath());
        }
    }


}
