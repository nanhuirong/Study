package com.huirong.jiangjuan.code.algorithm;


import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Huirong on 17/2/14.
 */
public class Test {


    public static void main(String[] args)throws Exception {
        File file = new File(Config.FUNCTION);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = null;
        HashMap<String, String> function = new HashMap<String, String>();
        while ((line = br.readLine()) != null){
            System.out.println(line);
            String[] split = line.split("\t");
            function.put(split[0].split("-")[0], split[1]);
        }
        br.close();
        file = new File(Config.SCRAPE_TRAIN);
        br = new BufferedReader(new FileReader(file));
        line = null;
        ArrayList<String> train = new ArrayList<String>();
        while ((line = br.readLine()) != null){
            if (line.startsWith("#")){
                continue;
            }
            String[] split = line.split(" ");
            String key = split[1].split("\\\\")[5].split("-")[0];
            String[] array = new String[Config.TOPIC_NUM];
            System.out.println(Arrays.toString(split));
            for (int i = 2; i < split.length; i = i + 2){
                array[Integer.parseInt(split[i])] = split[i + 1];
            }
            StringBuilder sb = new StringBuilder();
            sb.append(key + "|");
            for (int i = 0; i < array.length; i++){
                if (i == array.length - 1){
                    sb.append(array[i]);
                }else {
                    sb.append(array[i]).append(",");
                }
            }
            if (function.get(key) != null){
                sb.append("|").append(function.get(key));
                train.add(sb.toString());
            }
        }
        br.close();
        file = new File(Config.SCRAPE_TEST);
        br = new BufferedReader(new FileReader(file));
        ArrayList<String> test = new ArrayList<String>();
        line = null;
        while ((line = br.readLine()) != null){
            if (line.startsWith("#")){
                continue;
            }
            String[] split = line.split(" ");
            String key = split[1].split("\\\\")[5].split("-")[0];
            String[] array = new String[Config.TOPIC_NUM];
            for (int i = 2; i < split.length; i = i + 2){
                array[Integer.parseInt(split[i])] = split[i + 1];
            }
            StringBuilder sb = new StringBuilder();
            sb.append(key + "|");
            for (int i = 0; i < array.length; i++){
                if (i == array.length - 1){
                    sb.append(array[i]);
                }else {
                    sb.append(array[i]).append(",");
                }
            }
//            sb.append("|").append(function.get(key));
            if (function.get(key) != null){
                sb.append("|").append(function.get(key));
                test.add(sb.toString());
            }
        }
        file = new File(Config.TRAIN);
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        for (String t : train){
            bw.write(t + "\n");
        }
        bw.close();
        file = new File(Config.TEST);
        bw = new BufferedWriter(new FileWriter(file));
        for (String t : test){
            bw.write(t + "\n");
        }
        bw.close();
    }
}
