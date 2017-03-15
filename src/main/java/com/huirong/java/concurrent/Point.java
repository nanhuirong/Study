package com.huirong.java.concurrent;

import javax.annotation.concurrent.Immutable;

/**
 * Created by huirong on 17-3-15.
 *
 */
@Immutable
public class Point {
    public final int x, y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
