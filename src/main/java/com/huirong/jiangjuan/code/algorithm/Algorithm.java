package com.huirong.jiangjuan.code.algorithm;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Huirong on 17/2/13.
 */
public class Algorithm {
    public void pipeLine() throws Exception {
        System.out.println();
        //读取训练集
        ArrayList<Vector> trainData = ReadData.getData(Config.TRAIN);
        //读取测试集
        ArrayList<Vector> testData = ReadData.getData(Config.TEST);
        //为测试集挑选符合特定阈值的向量
        ArrayList<Entity> entities = selectVector(trainData, testData, 0.7);
//        System.out.println("------------------------");
//        System.out.print(entities);
        //基于偏度进行二次过滤
        entities = selectVectorAdvance(entities, 0.5);
//        System.out.println("------------------------");
//        System.out.print(entities);
        recommed(entities);
//        selectFunctionComponents(entities, 0.5);


    }

    //计算候选集的偏度, 直到（偏度 == 0 && 候选项集合）
    private double computeSkewness(Vector vector, ArrayList<Vector> candidateSet) {
        //计算请求权限的平均数
        double u = 0.0;
        int sum = 0;
        for (Vector candidate : candidateSet){
            sum += candidate.getFunctionComponents().size();
        }
        u = sum / (double)(candidateSet.size());
        //计算偏度
        double upper = 0.0;
        double downer = 0.0;
        for (Vector candidate: candidateSet){
            upper = Math.pow(candidate.getFunctionComponents().size() - u, 3);
            downer = Math.pow(candidate.getFunctionComponents().size() - u, 2);
        }
        upper = upper / candidateSet.size();
        downer = downer / (candidateSet.size() - 1);
        downer = Math.sqrt(Math.pow(downer, 3));
        double skewness = upper / downer;
        return skewness;
    }

    private  void recommed(ArrayList<Entity> entities){
        for (Entity entity : entities){
            ArrayList<Map.Entry<Integer, Double>> list = computeProbilitity(entity);
            System.out.println(entity.trueVector.getKey() + "\t"
                    + entity.trueVector.getFunctionComponents());
            System.out.println(list);
        }
    }

    //计算可能选择每一个权限的概率--> 得到一个推荐向量
    private ArrayList<Map.Entry<Integer, Double>> computeProbilitity(Entity entity){
        Set<Integer> set = new HashSet<Integer>();
        for (Vector vector : entity.candidateSet){
            set.addAll(vector.getFunctionComponents());
        }
        double[] pro = new double[set.size()];
        int index= 0;
        for (Integer id : set){
            double upper = 0.0;
            double downer = 0.0;
            for (Vector vector : entity.candidateSet){
                downer += vector.getSimilarity();
                if (vector.getFunctionComponents().contains(id)){
                    upper += vector.getSimilarity();
                }
//                System.out.println(downer + "\t" +  upper);
            }
            pro[index++] = upper / downer;
        }
//        System.out.println(Arrays.toString(pro));
        Map<Integer, Double> map = new HashMap<Integer, Double>();
        index = 0;
        for (Integer id : set){
            map.put(id, pro[index++]);
        }
        ArrayList<Map.Entry<Integer, Double>> list =
                new ArrayList<Map.Entry<Integer, Double>>(map.entrySet());
//        Collections.sort(list, new myComparator());
        Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
        return list;
    }

    private ArrayList<Entity> selectVector(ArrayList<Vector> trainData, ArrayList<Vector> testData,
                                           double threshold) {
        ArrayList<Entity> entities = new ArrayList<Entity>();
        for (int i = 0; i < testData.size();i++){
            Vector vector = testData.get(i);
            Entity entity = new Entity();
            entity.trueVector = vector;
            for (int j = 0; j < trainData.size(); j++){
                Vector trainVector = trainData.get(j);
                double sim = computeSim(vector.getVector(), trainVector.getVector());
//                trainVector.setSimilarity(sim);
                if (sim >= threshold){
                    Vector temp = new Vector(trainVector.getKey(), trainVector.getVector(),
                            trainVector.getFunctionComponents(), sim);
                    temp.setSimilarity(sim);
                    entity.candidateSet.add(temp);
                }
            }
            Collections.sort(entity.candidateSet);
            entities.add(entity);
        }
        return entities;
    }

    private ArrayList<Entity> selectVectorAdvance(ArrayList<Entity> entities, double threshold){
        for (Entity entity : entities){
            int size = entity.candidateSet.size();
            double skewness = computeSkewness(entity.trueVector, entity.candidateSet);
            while (Math.abs(0.001) >= 0 && entity.candidateSet.size() > size * threshold){
                if (skewness > 0){
                    entity.candidateSet.remove(entity.candidateSet.size() - 1);
                }else {
                    entity.candidateSet.remove(0);
                }
            }
        }
        return entities;
    }

//    private static ArrayList<Entity> selectFunctionComponents(ArrayList<Entity> entities, double threshold){
//        ArrayList<Entity> result = new ArrayList<Entity>();
//        for (Entity entity : entities){
//            HashMap<Integer, Integer> temp = new HashMap<Integer, Integer>();
//            ArrayList<Integer> recommendFunctionComponents = entity.recommendFunctionComponents;
//            for (Integer elem : recommendFunctionComponents){
//                if (temp.get(elem) != null){
//                    temp.put(elem, temp.get(elem) + 1);
//                }else {
//                    temp.put(elem, 1);
//                }
//            }
//            Entity entity1 = new Entity();
//            entity1.key = entity.key;
//            entity1.trueFunctionComponents = entity.trueFunctionComponents;
//            List<Map.Entry<Integer,Integer>> list = new ArrayList<Map.Entry<Integer,Integer>>(temp.entrySet());
//            //然后通过比较器来实现排序
//            Collections.sort(list,new Comparator<Map.Entry<Integer,Integer>>() {
//                //升序排序
//                public int compare(Map.Entry<Integer, Integer> o1,
//                                   Map.Entry<Integer, Integer> o2) {
//                    return  o2.getValue().compareTo(o1.getValue());
//                }
//
//            });
//            System.out.print(entity1.key + "|" + entity1.trueFunctionComponents + "|");
//            int count = 0;
//            for(Map.Entry<Integer,Integer> mapping:list){
//                if (count++ < entity1.trueFunctionComponents.size())
//                    System.out.print(mapping.getKey()+":"+mapping.getValue() + ",");
//            }
////            for (Map.Entry<Integer, Integer> entry : temp.entrySet()){
////                System.out.print("[" + entry.getKey() + "," + entry.getValue() + "]");
////            }
//            System.out.println();
//        }
//        return result;
//    }

    //计算相似度
    private double computeSim(ArrayList<Double> src, ArrayList<Double> dest) {
        double sim = 0.0;
        for (int i = 0; i < src.size(); i++) {
            sim += (src.get(i) - dest.get(i)) * (src.get(i) - dest.get(i));
        }
        sim = 1.0 / (1.0 + Math.sqrt(sim));
        return sim;
    }

    public static void main(String[] args) throws Exception {
//        pipeLine();
    }

//    private static class Entity{
//        public Entity() {
//        }
//        //UUID
//        public Vector trueVector;
//        public ArrayList<Vector> candidateSet = new ArrayList<Vector>();
//
//        @Override
//        public String toString() {
//            return "Entity{" +
//                    "trueVector=" + trueVector +
//                    ", candidateSet=" + candidateSet +
//                    '}' + '\n';
//        }
//    }
}
