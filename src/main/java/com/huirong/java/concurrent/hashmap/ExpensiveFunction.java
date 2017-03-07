package com.huirong.java.concurrent.hashmap;

import java.math.BigInteger;

/**
 * Created by huirong on 17-3-7.
 */
public class ExpensiveFunction implements Computable<String, BigInteger> {
    @Override
    public BigInteger compute(String arg) throws InterruptedException {
        return new BigInteger(arg);
    }
}
