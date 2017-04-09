package com.huirong.oo.decorator;

/**
 * Created by huirong on 17-4-8.
 */
public abstract class Beverage {
    String description = "Unknow Beverage";

    public String getDescription() {
        return description;
    }

    public abstract double cost();
}
