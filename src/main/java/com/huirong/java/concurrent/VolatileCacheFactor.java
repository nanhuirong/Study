package com.huirong.java.concurrent;

import java.math.BigInteger;

/**
 * Created by huirong on 17-3-15.
 * 通过不可变对象实现线程安全
 */
public class VolatileCacheFactor {
    private volatile OneValueCache cache = new OneValueCache(null, null);
    public void service(BigInteger number){
        BigInteger[] factors = cache.getLastFactors(number);
        if (factors == null){
            factors = getFactors(number);
            cache = new OneValueCache(number, factors);
        }
    }

    private BigInteger[] getFactors(BigInteger number){
        return null;
    }
}
