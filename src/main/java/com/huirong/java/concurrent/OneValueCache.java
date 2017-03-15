package com.huirong.java.concurrent;

import javax.annotation.concurrent.Immutable;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * Created by huirong on 17-3-15.
 */
@Immutable
public class OneValueCache {
    private final BigInteger lastNumber;
    private final BigInteger[] lastFactors;

    public OneValueCache(BigInteger lastNumber, BigInteger[] lastFactors) {
        this.lastNumber = lastNumber;
        this.lastFactors = lastFactors;
    }
    public BigInteger[] getLastFactors(BigInteger number){
        if (lastNumber == null || !lastNumber.equals(number)){
            return null;
        }
        else {
            return Arrays.copyOf(lastFactors, lastFactors.length);
        }
    }
}
