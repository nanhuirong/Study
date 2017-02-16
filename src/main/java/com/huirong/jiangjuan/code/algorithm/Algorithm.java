package com.huirong.jiangjuan.code.algorithm;

import java.util.*;

/**
 * Created by Huirong on 17/2/13.
 */
public class Algorithm {
    public final static String PATH = "/Users/quixeynew/data_prodection/";
    public final static String TRAIN = "train.txt";
    public final static String TEST = "test.txt";
    public static void pipeLine()throws Exception{
        System.out.println();
        //读取训练集
        ArrayList<Vector> trainData = ReadData.getData(PATH + TRAIN);
        //读取测试集
        ArrayList<Vector> testData = ReadData.getData(PATH + TEST);
        //为测试集挑选符合特定阈值的向量
        ArrayList<Entity> entities = selectVector(trainData, testData, 0.8);
//        for (Entity entity : entities){
//            System.out.println(entity.key + "\t" + entity.trueFunctionComponents + "\t" + entity.recommendFunctionComponents);
//        }
        //挑选符合特定阈值的功能组件
        selectFunctionComponents(entities, 0.5);

    }

    private static ArrayList<Entity> selectVector(ArrayList<Vector> trainData, ArrayList<Vector> testData, double threshold){
        ArrayList<Entity> entities = new ArrayList<Entity>();
        for (Vector vector : testData){
            Entity entity = new Entity();
            entity.key = vector.getKey();
            entity.trueFunctionComponents = vector.getFunctionComponents();
            for (Vector trainVector : trainData){
                if (computeSim(vector.getVector(), trainVector.getVector()) > threshold){
                    entity.recommendFunctionComponents.addAll(trainVector.getFunctionComponents());
                }
            }
            entities.add(entity);
        }
        return entities;
    }

    private static ArrayList<Entity> selectFunctionComponents(ArrayList<Entity> entities, double threshold){
        ArrayList<Entity> result = new ArrayList<Entity>();
        for (Entity entity : entities){
            HashMap<Integer, Integer> temp = new HashMap<Integer, Integer>();
            ArrayList<Integer> recommendFunctionComponents = entity.recommendFunctionComponents;
            for (Integer elem : recommendFunctionComponents){
                if (temp.get(elem) != null){
                    temp.put(elem, temp.get(elem) + 1);
                }else {
                    temp.put(elem, 1);
                }
            }
            Entity entity1 = new Entity();
            entity1.key = entity.key;
            entity1.trueFunctionComponents = entity.trueFunctionComponents;
            List<Map.Entry<Integer,Integer>> list = new ArrayList<Map.Entry<Integer,Integer>>(temp.entrySet());
            //然后通过比较器来实现排序
            Collections.sort(list,new Comparator<Map.Entry<Integer,Integer>>() {
                //升序排序
                public int compare(Map.Entry<Integer, Integer> o1,
                                   Map.Entry<Integer, Integer> o2) {
                    return  o2.getValue().compareTo(o1.getValue());
                }

            });
            System.out.print(entity1.key + "|" + entity1.trueFunctionComponents + "|");
            int count = 0;
            for(Map.Entry<Integer,Integer> mapping:list){
                if (count++ < entity1.trueFunctionComponents.size())
                    System.out.print(mapping.getKey()+":"+mapping.getValue() + ",");
            }
//            for (Map.Entry<Integer, Integer> entry : temp.entrySet()){
//                System.out.print("[" + entry.getKey() + "," + entry.getValue() + "]");
//            }
            System.out.println();
        }
        return result;
    }

    //计算相似度
    private static double computeSim(ArrayList<Double> src, ArrayList<Double> dest){
        double sim = 0.0;
        for (int i = 0; i < src.size(); i++){
            sim += (src.get(i) - dest.get(i)) * (src.get(i) - dest.get(i));
        }
        sim = 1.0 / ( 1.0 +  Math.sqrt(sim));
        return sim;
    }

    public static void main(String[] args)throws Exception {
        pipeLine();
    }

    private static class Entity{
        public Entity() {
        }
        //UUID
        public String key;

        public ArrayList<Integer> trueFunctionComponents;
        public ArrayList<Integer> recommendFunctionComponents = new ArrayList<Integer>();
    }
}
