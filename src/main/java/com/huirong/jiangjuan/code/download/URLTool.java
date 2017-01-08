package com.huirong.jiangjuan.code.download;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Huirong on 17/1/7.
 *用于判定URL是否失效
 */
public class URLTool {
    private static URL url;
    private static HttpURLConnection con;
    private static int state = -1;

    /**
     * 功能：检测当前URL是否可连接或是否有效,
     * @param urlStr 指定URL网络地址
     * @return URL
     */
    public static synchronized boolean isConnect(String urlStr) {
        int counts = 0;
        if (urlStr == null || urlStr.length() <= 0) {
            return false;
        }
        try {
            url = new URL(urlStr);
            con = (HttpURLConnection) url.openConnection();
            state = con.getResponseCode();
        }catch (Exception e){
//            e.printStackTrace();
            return false;
        }finally {

        }
        if (state == 200){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 从url中获取文件名
     */
    public static String getFileNameFromUrl(String url){
        String name = new Long(System.currentTimeMillis()).toString() + ".X";
        int index = url.lastIndexOf("/");
        if(index > 0){
            name = url.substring(index + 1);
            if(name.trim().length()>0){
                return name;
            }
        }
        return name;
    }

    /**
     * 利用URL下载文件
     */
    public static void downloadFormUrl(String urlStr, String Path){
        String fileName = getFileNameFromUrl(urlStr);
        try {
            if (isConnect(urlStr)){
                FileUtils.copyURLToFile(new URL(urlStr), new File(Path + fileName));
            }
        }catch (Exception e){

        }

    }
}
