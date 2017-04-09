package com.huirong.oo.decorator;

/**
 * Created by huirong on 17-4-8.
 * 浓缩咖啡
 */
public class Espresso extends Beverage {
    public Espresso() {
        description = "Espresso";
    }

    @Override
    public double cost() {
        return 1.99;
    }
}
