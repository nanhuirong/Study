package com.huirong.java.concurrent;


import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import javax.servlet.Servlet;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by huirong on 17-3-15.
 * 实现计算缓存命中
 * 更新缓存
 */
@ThreadSafe
public class CachedFactorizer {
    @GuardedBy("this") private BigInteger lastNumber;
    @GuardedBy("this") private BigInteger[] lastFactors;
    @GuardedBy("this") private long hits;
    @GuardedBy("this") private long cacheHits;

    public synchronized long getHits(){
        return hits;
    }

    public synchronized double getCacheHitRatio(){
        return (double) cacheHits / (double) hits;
    }

    public void service(BigInteger number){
        BigInteger[] factors = null;
        synchronized (this){
            ++hits;
            if (number.equals(lastNumber)){
                ++cacheHits;
                factors = lastFactors;
            }
        }
        if (factors == null){
            factors = getFactors(number);
            synchronized (this){
                lastNumber = number;
                lastFactors = factors.clone();
            }
        }
    }

    private BigInteger[] getFactors(BigInteger number){
        BigInteger[] factors = null;
        return factors;
    }


    public static void main(String[] args) {
        final ConcurrentHashMap<Integer, Integer> conMap = new ConcurrentHashMap<>();
        conMap.put(1, 1);
        conMap.put(2, 2);
        final Map<Integer, Integer> unmodify = Collections.unmodifiableMap(conMap);
        System.out.println(unmodify);
        conMap.put(conMap.get(2), 3);
        System.out.println(conMap);
        System.out.println(unmodify);
    }
}
