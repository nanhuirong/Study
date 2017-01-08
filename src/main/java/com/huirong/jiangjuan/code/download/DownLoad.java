package com.huirong.jiangjuan.code.download;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.tools.nsc.Global;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Huirong on 17/1/7.
 */
public class DownLoad {
    public static final String SUFFIX_PATH = "/src/main/java/com/huirong/jiangjuan/data/certified_products.csv";
    public static final Logger LOGGER = LoggerFactory.getLogger(DownLoad.class);



    public Map<String, String> getUrls(){
        Map<String, String> map = new HashMap<String, String>();
        String sourcePath = LocalPath.getLocalSourcePath();
        map = readFile(sourcePath);
        return map;
    }

    private Map<String, String> readFile(String path){
        Map<String, String> map = new HashMap<String, String>();
        File file = new File(path);
        BufferedReader bufferedReader = null;
        String line = null;
        if (file.exists()){
            try {
                bufferedReader = new BufferedReader(new FileReader(file));
                while ((line = bufferedReader.readLine()) != null){
                    Entry entry = getKeyValue(line);
                    if (entry != null){
                        map.put(entry.getKey(), entry.getValue());
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }else {
            LOGGER.error(file.getAbsolutePath() + "not found");
        }
        return map;
    }

    private Entry getKeyValue(String record){
        String[] array = record.split("\",\"");
        if (array.length == 14 && array[8].endsWith(".pdf") && !array[8].contains(" ") && array[9].endsWith(".pdf") && !array[9].contains(" ")){
            return new Entry(array[8], array[9]);
        }else {
            return null;
        }
    }

    class Entry{
        private String key;
        private String value;

        public Entry(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

}
