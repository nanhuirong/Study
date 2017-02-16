package com.huirong.jiangjuan.code.algorithm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Created by Huirong on 17/2/13.
 */
public class ReadData {
    public final static String PATH = "/Users/quixeynew/data_prodection/";
    public final static String TRAIN = "train.txt";
    public final static String TEST = "test.txt";

    public static ArrayList<Vector> getData(String path)throws Exception{
        ArrayList list = new ArrayList<Vector>();
        File file = new File(path);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = null;
        while ((line = br.readLine()) != null){
            ArrayList<Double> vectorList = new ArrayList<Double>();
            ArrayList<Integer> functionComponents = new ArrayList<Integer>();
            Vector vector = new Vector();
            String[] split = line.split("\\|");
            vector.setKey(split[0]);
            ArrayList<Double> listVector = new ArrayList<Double>();
            for (String elem : split[1].split(",")){
                listVector.add(Double.parseDouble(elem));
            }
            vector.setVector(listVector);
            ArrayList<Integer> listFunction = new ArrayList<Integer>();
            for (String elem : split[2].split(",")){
                listFunction.add(Integer.parseInt(elem));
            }
            vector.setFunctionComponents(listFunction);

            list.add(vector);
        }
        return list;
    }

    public static void main(String[] args)throws Exception {
        ArrayList<Vector> list = getData(PATH + TEST);
        for (Vector vector : list){
            System.out.println(vector.getKey() + "\t" + vector.getVector() + "\t" + vector.getFunctionComponents());
        }
    }

}
