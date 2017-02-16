package com.huirong.jiangjuan.code.algorithm;

import java.util.ArrayList;

/**
 * Created by Huirong on 17/2/13.
 */
public class Vector {
    //标识向量UUID
    private String key;
    //向量
    private ArrayList<Double> vector;
    //对应功能组件
    private ArrayList<Integer> functionComponents;

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
}
