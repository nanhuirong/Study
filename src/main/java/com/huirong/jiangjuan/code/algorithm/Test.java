package com.huirong.jiangjuan.code.algorithm;

import sun.org.mozilla.javascript.internal.ast.WhileLoop;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Huirong on 17/2/14.
 */
public class Test {
    public static final String PATH = "/Users/quixeynew/data_prodection/";
    public static final String TRAIN = "train_data_topic.txt";
    public static final String TEST = "test_data_topics.txt";
    public static final String FUNCTION = "SRF_data_prodection.txt";

    public static void main(String[] args)throws Exception {
        File file = new File(PATH + FUNCTION);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = null;
        HashMap<String, String> function = new HashMap<String, String>();
        while ((line = br.readLine()) != null){
            String[] split = line.split("\t");
            function.put(split[0].split("-")[0], split[1]);
        }
        br.close();
        file = new File(PATH + TRAIN);
        br = new BufferedReader(new FileReader(file));
        line = null;
        ArrayList<String> train = new ArrayList<String>();
        while ((line = br.readLine()) != null){
            if (line.startsWith("#")){
                continue;
            }
            String[] split = line.split(" ");
            String key = split[1].split("\\\\")[5].split("-")[0];
            String[] array = new String[10];
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
        file = new File(PATH + TEST);
        br = new BufferedReader(new FileReader(file));
        ArrayList<String> test = new ArrayList<String>();
        line = null;
        while ((line = br.readLine()) != null){
            if (line.startsWith("#")){
                continue;
            }
            String[] split = line.split(" ");
            String key = split[1].split("\\\\")[5].split("-")[0];
            String[] array = new String[10];
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
        file = new File(PATH + "train.txt");
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        for (String t : train){
            bw.write(t + "\n");
        }
        bw.close();
        file = new File(PATH + "test.txt");
        bw = new BufferedWriter(new FileWriter(file));
        for (String t : test){
            bw.write(t + "\n");
        }
        bw.close();
    }
}
