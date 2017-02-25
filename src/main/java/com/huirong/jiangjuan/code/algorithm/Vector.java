package com.huirong.jiangjuan.code.algorithm;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by Huirong on 17/2/13.
 */
public class Vector implements Comparable<Vector> {
    //标识向量UUID
    private String key;
    //向量
    private ArrayList<Double> vector;
    //对应功能组件
    private ArrayList<Integer> functionComponents;
    //记录相似度
    private Double Similarity;

    public Vector() {
    }

    public Vector(String key, ArrayList<Double> vector, ArrayList<Integer> functionComponents, Double similarity) {
        this.key = key;
        this.vector = vector;
        this.functionComponents = functionComponents;
        Similarity = similarity;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ArrayList<Double> getVector() {
        return vector;
    }

    public void setVector(ArrayList<Double> vector) {
        this.vector = vector;
    }

    public ArrayList<Integer> getFunctionComponents() {
        return functionComponents;
    }

    public void setFunctionComponents(ArrayList<Integer> functionComponents) {
        this.functionComponents = functionComponents;
    }

    public Double getSimilarity() {
        return Similarity;
    }

    public void setSimilarity(Double similarity) {
        Similarity = similarity;
    }

    //升序
    public int compareTo(Vector vector) {
        return this.getFunctionComponents().size() - (vector.getFunctionComponents().size());
    }

    @Override
    public String toString() {
        return "Vector{" +
                "key='" + key + '\'' +
                ", vector=" + vector +
                ", functionComponents=" + functionComponents +
                ", Similarity=" + Similarity +
                '}' + '\n';
    }
}
