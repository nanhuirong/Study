package com.huirong.java.concurrent.cas;

import jdk.nashorn.internal.ir.annotations.Immutable;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by huirong on 17-3-12.
 */
@ThreadSafe
public class CasNumberRange {
    @Immutable private static class IntPair{
        final int lower;
        final int upper;

        public IntPair(int lower, int upper) {
            this.lower = lower;
            this.upper = upper;
        }
    }

    private final AtomicReference<IntPair> values = new AtomicReference<>(new IntPair(0, 0));

    public int getLower(){
        return values.get().lower;
    }

    public int getUpper(){
        return values.get().upper;
    }

    public void setLower(int newValue){
        while (true){
            IntPair pair = values.get();
            if (newValue > pair.upper){
                throw new IllegalArgumentException("");
            }
            IntPair newPair = new IntPair(newValue, pair.upper);
            if (values.compareAndSet(pair, newPair)){
                return;
            }
        }
    }

    public void setUpper(int newValue){
        while (true){
            IntPair pair = values.get();
            if (newValue < pair.lower){
                throw new IllegalArgumentException();
            }
            IntPair newPair = new IntPair(pair.lower, newValue);
            if (values.compareAndSet(pair, newPair))
                return;
        }
    }
}
