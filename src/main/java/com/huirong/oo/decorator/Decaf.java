package com.huirong.oo.decorator;

/**
 * Created by huirong on 17-4-8.
 */
public class Decaf extends Beverage {

    public Decaf() {
        description = "Decaf Coffee";
    }

    @Override
    public double cost() {
        return 1.05;
    }
}
