package com.huirong.jiangjuan.code.algorithm;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by huirong on 17-2-24.
 */
public class Entity{
    public Entity() {
    }
    //UUID
    public Vector trueVector;
    public ArrayList<Vector> candidateSet = new ArrayList<Vector>();
    public Vector recommend = new Vector();




    @Override
    public String toString() {
        return "Entity{" +
                "trueVector=" + trueVector +
                ", candidateSet=" + candidateSet +
                '}' + '\n';
    }
}