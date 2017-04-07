package com.huirong.java.concurrent;

import java.util.concurrent.Callable;

/**
 * Created by huirong on 17-3-20.
 */
public class FindMaxTask implements Callable<Integer> {
    private int[] data;
    private int start;
    private int end;
    public FindMaxTask(int[] data, int start, int end) {
        this.data = data;
        this.start = start;
        this.end = end;
    }
    @Override
    public Integer call() throws Exception {
        int max = Integer.MIN_VALUE;
        for (int i = start; i < end; i++){
            if (data[i] > max)
                max = data[i];
        }
        return max;
    }
}
