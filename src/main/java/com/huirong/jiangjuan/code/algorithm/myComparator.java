package com.huirong.jiangjuan.code.algorithm;

import java.util.Comparator;
import java.util.Map;

/**
 * Created by huirong on 17-2-24.
 */
public class myComparator implements
        Comparator<Map.Entry<Integer, Double>> {
    public int compare(Map.Entry<Integer, Double> obj1,
                       Map.Entry<Integer, Double> obj2) {
        return obj1.getValue().compareTo(obj2.getValue());
    }
}
