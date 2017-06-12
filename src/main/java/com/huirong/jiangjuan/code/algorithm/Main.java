package com.huirong.jiangjuan.code.algorithm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Huirong on 17/2/13.
 */
public class Main {
    public static void main(String[] args)throws Exception {
        Algorithm algorithm = new Algorithm();
//        algorithm.pipeLine();
        List<Double> list = new ArrayList<>();
        list.add(0.0);
        list.add(0.1);
        list.add(0.2);
        list.add(0.3);
        list.add(0.4);
        list.add(0.5);
        list.add(0.6);
        list.add(0.7);
        list.add(0.8);
        list.add(0.9);
        list.add(1.0);
        List<Double> result = new ArrayList<>();
        for (Double elem : list){
            System.out.println(elem);
            result.add(algorithm.pipeLine(elem, 0.5));
        }
        File file = new File(Config.RESULT);
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        StringBuilder sb = new StringBuilder();
        System.out.print(list);
        for (Double elem : list){
            System.out.print(elem + "\t");
            sb.append(elem + ',');
        }
        System.out.print(sb);
        bw.write(sb.substring(0, sb.length() - 1));
        bw.write("\n");
        sb = new StringBuilder();
        for (Double elem : result){
            sb.append(elem + ",");
        }
        bw.write(sb.substring(0, sb.length() - 1));
        bw.write("\n");
        bw.close();
    }

}