package com.huirong.jiangjuan.code.download;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Huirong on 17/1/8.
 */
public class LocalPath {
    private static final String SOURCE_PATH = "/src/main/java/com/huirong/jiangjuan/data/certified_products.csv";
    private static final String SCRAPE_PATH = "/src/main/java/com/huirong/jiangjuan/data/scrape/";
    private static final String CONTENT_PATH = "/src/main/java/com/huirong/jiangjuan/data/content/";

    public static final Logger LOGGER = LoggerFactory.getLogger(LocalPath.class);

    public static String getLocalSourcePath(){
       return getPWD() + SOURCE_PATH;
    }

    private static String getPWD(){
        Process process = null;
        String line = null;
        try {
            process = Runtime.getRuntime().exec("pwd");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            line = bufferedReader.readLine();
//            System.out.println(line);
        }catch (IOException e){
            LOGGER.error("pwd failed");
        }
        return line;
    }

    public static String getScrapePath(){
        return getPWD() + SCRAPE_PATH;
    }

    public static String getContentPath(){
        return getPWD() + CONTENT_PATH;
    }
}
